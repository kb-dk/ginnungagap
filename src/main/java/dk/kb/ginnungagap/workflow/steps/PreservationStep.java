package dk.kb.ginnungagap.workflow.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.StringUtils;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;

/**
 * The preservation step.
 */
public class PreservationStep implements WorkflowStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(PreservationStep.class);

    /** The suffix of the raw-files.*/
    protected static final String RAW_FILE_SUFFIX = "_raw.xml";
    
    /** Transformation configuration for the metadata.*/
    protected final TransformationConfiguration conf;
    /** The Cumulus server.*/
    protected final CumulusServer server;
    /** The metadata transformation handler.*/
    protected final MetadataTransformationHandler transformationHandler;
    /** The bitrepository preserver.*/
    protected final BitmagPreserver preserver;
    /** The name of the catalog to preserve.*/
    protected final String catalogName;

    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformationHandler The metadata transformer handler.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     * @param catalogName The name of the catalog for this step.
     */
    public PreservationStep(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformationHandler transformationHandler, BitmagPreserver preserver, String catalogName) {
        this.conf = transConf;
        this.server = server;
        this.transformationHandler = transformationHandler;
        this.preserver = preserver;
        this.catalogName = catalogName;
    }

    @Override
    public String getName() {
        return "Preservation Step";
    }

    @Override
    public void performStep() throws Exception {
        CumulusQuery query = CumulusQuery.getPreservationAllQuery(catalogName);
        CumulusRecordCollection items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getCount() + " records to be preserved.");
        preserveRecordItems(items, catalogName);
    }

    /**
     * Preserves all the record items of the given collection. 
     * @param items The collection of record items to preserve.
     */
    protected void preserveRecordItems(CumulusRecordCollection items, String catalogName) {
        int n = items.getCount();
        if(n == 0) {
            log.debug("No items to preserve from catalog: " + catalogName);
            return;
        }
        for(CumulusRecord record : items) {
            try {
                log.debug("Initiating preservation on record '" + record.getUUID() + "'");
                sendRecordToPreservation(record);
            } catch (RuntimeException e) {
                log.error("Runtime exception caught while trying to handle Cumulus record '" 
                        + record.getUUID() + "'. Something must be seriously wrong with that item!!!\n"
                        + "Trying to handle next item.", e);
            }
        }
    }

    /**
     * Preserves the record, and if it is a master-asset, then the representation is also preserved.
     * @param record The given Cumulus record to preserve.
     */
    protected void sendRecordToPreservation(CumulusRecord record) {
        try {
            record.initFieldsForPreservation();
            record.resetMetadataGuid();

            record.validateRequiredFields(conf.getRequiredFields());
            preserveFile(record);
            preserveMetadata(record);
            preserveIntellectuelEntity(record);

            if(record.isMasterAsset()) {
                record.initRepresentationFields();
                record.resetRepresentationMetadataGuid();

                transformAndPreserveRepresentation(record);
                preserveRepresentationIntellectuelEntity(record);
            }
            preserver.checkConditions();
        } catch (Exception e) {
            log.warn("Preserving the record '" + record + "' failed.", e);
            record.setPreservationFailed("Failed to preserve record '" + record.getUUID() + ": \n" + e.getMessage());
        }
    }

    /**
     * Preserves the content-file of a given record.
     * @param record The given Cumulus record with the file to preserve.
     * @throws IOException If it fails to package the file.
     */
    protected void preserveFile(CumulusRecord record) throws IOException {
        preserver.packRecordResource(record);
    }

    /**
     * Preserves the metadata of a Cumulus record.
     * Transformes the metadata
     * @param record The Cumulus record.
     * @throws IOException If it fails to read or write metadata.
     */
    protected void preserveMetadata(CumulusRecord record) throws IOException {
        File metadataFile = transformAndValidateMetadata(record);

        setMetadataStandardsForRecord(record, metadataFile);
        preserver.packRecordMetadata(record, metadataFile);
    }
    
    /**
     * Preserve the intellectual entity for the Cumulus record.
     * @param record The record to have its intellectual entity preserved.
     */
    protected void preserveIntellectuelEntity(CumulusRecord record) throws IOException {
        String ieUUID = record.getFieldValue(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        String metadataUUID = record.getMetadataGUID();
        String fileUUID = record.getUUID();
        transformAndPreserveIntellectualEntity(ieUUID, metadataUUID, fileUUID, record);
    }

    /**
     * Preserve the representation part of a master asset as its own METS.
     * @param record The Cumulus record.
     * @throws IOException If an issue occurs when writing or preserving the Master asset metadata.
     */
    protected void transformAndPreserveRepresentation(CumulusRecord record) throws IOException {
        String representationMetadataGuid = record.getFieldValue(
                Constants.FieldNames.REPRESENTATION_METADATA_GUID);
        File metadataFile = new File(conf.getMetadataTempDir(), representationMetadataGuid);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getMetadataTempDir(), representationMetadataGuid 
                    + RAW_FILE_SUFFIX);
            MetadataTransformer transformer = transformationHandler.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION);
            transformer.transformXmlMetadata(record.getMetadata(cumulusMetadataFile), os);
            os.flush();
        }

        try (InputStream is = new FileInputStream(metadataFile)) {
            transformationHandler.validate(is);
        }
        preserver.packRepresentationMetadata(metadataFile, record.getPreservationCollectionID());
    }

    /**
     * Preserve the intellectual entity for the Cumulus record.
     * @param record The record to have its intellectual entity preserved.
     * @throws IOException If it fails to write the metadata file.
     */
    protected void preserveRepresentationIntellectuelEntity(CumulusRecord record) throws IOException {
        String ieUUID = record.getFieldValue(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID);
        String metadataUUID = record.getFieldValue(Constants.FieldNames.REPRESENTATION_METADATA_GUID);
        transformAndPreserveIntellectualEntity(ieUUID, metadataUUID, null, record);
    }

    /**
     * Transforms and preserves the intellectual entity.
     * @param ieUUID The UUID for the intellectual entity.
     * @param metadataUUID The UUID for the metadata object.
     * @param fileUUID The UUID for the file. This may be null.
     * @throws IOException If it fails to write the metadata file.
     */
    protected void transformAndPreserveIntellectualEntity(String ieUUID, String metadataUUID, String fileUUID, 
            CumulusRecord record) throws IOException {
        File ieRawFile = createIErawFile(ieUUID, metadataUUID, fileUUID);
        File metadataFile = new File(conf.getMetadataTempDir(), ieUUID);
        try (OutputStream os = new FileOutputStream(metadataFile);
                InputStream in = new FileInputStream(ieRawFile)) {
            MetadataTransformer transformer = transformationHandler.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);
            transformer.transformXmlMetadata(in, os);
            os.flush();
        }

        try (InputStream is = new FileInputStream(metadataFile)) {
            transformationHandler.validate(is);
        }
        preserver.packRepresentationMetadata(metadataFile, record.getPreservationCollectionID());
    }
    
    /**
     * Creates the raw XML file for the intellectual entity, for the transformation.
     * It should have the following format:
     * <record>
     *   <ie_uuid>IE_UUID</ie_uuid>
     *   <object_uuid>OBJECT_UUID</object_uuid>
     *   <file_uuid>FILE_UUID</file_uuid>
     * </record>
     * (where the file uuid field is optional).
     * @param ieUUID The UUID for the intellectual entity.
     * @param metadataUUID The UUID for the metadata object.
     * @param fileUUID The UUID for the file. This may be null.
     * @return The raw XML file.
     */
    protected File createIErawFile(String ieUUID, String metadataUUID, String fileUUID) {
        try {
            File ieRawFile = new File(conf.getMetadataTempDir(), ieUUID + RAW_FILE_SUFFIX); 

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("record");
            doc.appendChild(rootElement);

            Element ieField = doc.createElement("ie_uuid");
            rootElement.appendChild(ieField);
            ieField.appendChild(doc.createTextNode(ieUUID));

            Element metadataField = doc.createElement("object_uuid");
            rootElement.appendChild(metadataField);
            metadataField.appendChild(doc.createTextNode(metadataUUID));
            
            if(!StringUtils.isNullOrEmpty(fileUUID)) {
                Element fileField = doc.createElement("file_uuid");
                rootElement.appendChild(fileField);
                fileField.appendChild(doc.createTextNode(fileUUID));
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(ieRawFile);

            transformer.transform(source, result);
            return ieRawFile;
        } catch(Exception e) {
            throw new IllegalStateException("Cannot create the raw IntellectualEntity metadata file.", e);
        }
    }

    /**
     * Transforms and validates the metadata from the Cumulus record.
     * 
     * @param record The record with the metadata to transform and validate.
     * @return The file containing the transformed metadata.
     * @throws IOException If an error occurs when reading or writing the metadata.
     */
    protected File transformAndValidateMetadata(CumulusRecord record) throws IOException {
        String metadataUUID = record.getMetadataGUID();
        File metadataFile = new File(conf.getMetadataTempDir(), metadataUUID);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getMetadataTempDir(), metadataUUID + RAW_FILE_SUFFIX);
            MetadataTransformer transformer = transformationHandler.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);
            transformer.transformXmlMetadata(record.getMetadata(cumulusMetadataFile), os);
            os.flush();
        }

        try (InputStream is = new FileInputStream(metadataFile)) {
            transformationHandler.validate(is);
        }

        return metadataFile;
    }

    /**
     * Finds the metadata standards used in the metadata file, and set it as the value for the corresponding field 
     * in the cumulus record.
     * @param record The cumulus record where the metadata standards are written to.
     * @param metadataFile The file with the metadata.
     * @throws IOException If it fails to read the metadata standards from the metadata file.
     */
    protected void setMetadataStandardsForRecord(CumulusRecord record, File metadataFile) throws IOException {
        try (InputStream is = new FileInputStream(metadataFile)){
            Collection<String> namespaces = transformationHandler.getMetadataStandards(is);
            StringBuilder value = new StringBuilder();
            for(String s : namespaces) {
                value.append(s);
                value.append("\n");
            }
            record.setStringValueInField(Constants.FieldNames.BEVARINGS_METADATA, value.toString());
        }
    }
}
