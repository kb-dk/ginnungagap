package dk.kb.ginnungagap.workflow.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.UUID;

import dk.kb.ginnungagap.workflow.reporting.WorkflowReport;
import dk.kb.metadata.utils.GuidExtractionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusPreservationUtils;
import dk.kb.ginnungagap.cumulus.CumulusQueryUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;

/**
 * The preservation step.
 */
public class PreservationStep extends WorkflowStep {
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
        super(catalogName);
        this.conf = transConf;
        this.server = server;
        this.transformationHandler = transformationHandler;
        this.preserver = preserver;
        this.catalogName = catalogName;
    }

    @Override
    public String getName() {
        return "Preservation Step for catalog '" + catalogName + "'";
    }

    @Override
    protected void performStep(WorkflowReport report) throws Exception {
        CumulusQuery query = CumulusQueryUtils.getPreservationAllQuery(catalogName);
        CumulusRecordCollection items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getCount() + " records to be preserved.");
        preserveRecordItems(items, catalogName, report);
    }

    /**
     * Preserves all the record items of the given collection. 
     * @param items The collection of record items to preserve.
     * @param report The report for workflow.
     */
    protected void preserveRecordItems(CumulusRecordCollection items, String catalogName, WorkflowReport report) {
        if(items.getCount() == 0) {
            log.debug("No items to preserve from catalog: " + catalogName);
            setResultOfRun("No preservable records found for catalog '" + catalogName + "'.");
            return;
        }
        int i = 0;
        int failures = 0;
        String failure = "";
        for(CumulusRecord record : items) {
            try {
                setResultOfRun("Running! Preservation of #" + i + ", " + record.getUUID());
                log.debug("Initiating preservation on record '" + record.getUUID() + "'");
                sendRecordToPreservation(record);
                report.addSuccessRecord(CumulusPreservationUtils.getRecordName(record), catalogName);
            } catch (Exception e) {
                report.addFailedRecord(CumulusPreservationUtils.getRecordName(record), e.getMessage(), catalogName);
                log.error("Runtime exception caught while trying to handle Cumulus record '"
                        + record.getUUID() + "'. Something must be seriously wrong with that item!!!\n"
                        + "Trying to handle next item.", e);
                failures++;
                if(failure.isEmpty()) {
                    failure = e.getMessage();
                }
            }
            i++;
        }
        String results = "Preservation of " + i + " records";
        if(failures > 0) {
            results += ", with " + failures + " failures.";
            results += " First error message: " + failure;
        }
        setResultOfRun(results);
    }

    /**
     * Preserves the record, and if it is a master-asset, then the representation is also preserved.
     * @param record The given Cumulus record to preserve.
     */
    protected void sendRecordToPreservation(CumulusRecord record) throws Exception {
        try {
            log.info("Sending record: {} to preservation", record.getFieldValue(Constants.FieldNames.RECORD_NAME)); //todo: log.debug
            CumulusPreservationUtils.initialiseRecordForPreservation(record);

            record.validateFieldsExists(conf.getRequiredFields().getWritableFields());
            record.validateFieldsHasValue(conf.getRequiredFields().getBaseFields());
            
            preserveFile(record);
            preserveMetadata(record);
            preserveIntellectuelEntity(record);

            if(record.isMasterAsset()) {
                log.info("Record {} is Master asset. Initialize Representation", record.getFieldValue(Constants.FieldNames.RECORD_NAME));
                CumulusPreservationUtils.initializeRecordRepresentaitonForPreservation(record);

                transformAndPreserveRepresentation(record);
                preserveRepresentationIntellectuelEntity(record);
            }
            preserver.checkConditions();
        } catch (Exception e) {
            log.warn("Preserving the record '" + record + "' failed.", e);
            CumulusPreservationUtils.setPreservationFailed(record, "Failed to preserve record '" 
                    + record.getUUID() + ": \n" + e.getMessage());
            throw e;
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
    protected void preserveMetadata(CumulusRecord record) throws Exception {
        File metadataFile = transformAndValidateMetadata(record);

        setMetadataStandardsForRecord(record, metadataFile);
        preserver.packRecordMetadata(record, metadataFile);
    }
    
    /**
     * Preserve the intellectual entity for the Cumulus record.
     * @param record The record to have its intellectual entity preserved.
     */
    protected void preserveIntellectuelEntity(CumulusRecord record) throws IOException {
        String ieUUID = GuidExtractionUtils.extractGuid(record.getFieldValue(
                Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        String metadataUUID = CumulusPreservationUtils.getMetadataUUID(record);
        String fileUUID = record.getUUID();
        transformAndPreserveIntellectualEntity(ieUUID, metadataUUID, fileUUID, record);
    }

    /**
     * Preserve the representation part of a master asset as its own METS.
     * @param record The Cumulus record.
     * @throws Exception If an issue occurs when writing or preserving the Master asset metadata,
     */
    protected void transformAndPreserveRepresentation(CumulusRecord record) throws Exception {
        String representationMetadataGuid = record.getFieldValue(
                Constants.FieldNames.REPRESENTATION_METADATA_GUID);
        File metadataFile = new File(conf.getMetadataTempDir(), representationMetadataGuid);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getMetadataTempDir(), representationMetadataGuid 
                    + RAW_FILE_SUFFIX);
            MetadataTransformer transformer = transformationHandler.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION);
            try (OutputStream metadataOutputStream = new FileOutputStream(cumulusMetadataFile)) {
                record.writeFieldMetadata(metadataOutputStream);
            }
            try (InputStream cumulusIn = new FileInputStream(cumulusMetadataFile)) {
                transformer.transformXmlMetadata(cumulusIn, os);
            }
            os.flush();
        }

        try (InputStream is = new FileInputStream(metadataFile)) {
            transformationHandler.validate(is);
        }
        preserver.packRepresentationMetadata(metadataFile, record.getFieldValue(Constants.FieldNames.COLLECTION_ID),
                UUID.randomUUID().toString());
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
        File ieRawFile = new File(conf.getMetadataTempDir(), ieUUID + RAW_FILE_SUFFIX);
        CumulusPreservationUtils.createIErawFile(ieUUID, metadataUUID, fileUUID, ieRawFile);
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
        preserver.packRepresentationMetadata(metadataFile, record.getFieldValue(Constants.FieldNames.COLLECTION_ID), 
                UUID.randomUUID().toString());
    }
    
    /**
     * Transforms and validates the metadata from the Cumulus record.
     * 
     * @param record The record with the metadata to transform and validate.
     * @return The file containing the transformed metadata.
     * @throws IOException If an error occurs when reading or writing the metadata.
     */
    protected File transformAndValidateMetadata(CumulusRecord record) throws Exception {
        String metadataUUID = CumulusPreservationUtils.getMetadataUUID(record);
        File metadataFile = new File(conf.getMetadataTempDir(), metadataUUID);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getMetadataTempDir(), metadataUUID + RAW_FILE_SUFFIX);
            MetadataTransformer transformer = transformationHandler.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);
            try (OutputStream cumulusOut = new FileOutputStream(cumulusMetadataFile)) {
                record.writeFieldMetadata(cumulusOut);
            }
            try (InputStream cumulusIn = new FileInputStream(cumulusMetadataFile)) {
                transformer.transformXmlMetadata(cumulusIn, os);
            }
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
