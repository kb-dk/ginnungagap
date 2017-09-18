package dk.kb.ginnungagap.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.metadata.utils.StringUtils;

/**
 * Simple workflow for preserving Cumulus items.
 * 
 * It extracts the record from Cumulus, which match the preservation query.
 * Each Cumulus record will first be validated against its required fields, 
 * then all the metadata fields are extracted and transformed.
 * And finally the asset (content file) and transformed metadata will be packaged and sent to the bitrepository.
 */
public class PreservationWorkflow implements Workflow {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(PreservationWorkflow.class);

    /** Transformation configuration for the metadata.*/
    private final TransformationConfiguration conf;
    /** The Cumulus server.*/
    private final CumulusServer server;
    /** The metadata transformer.*/
    private final MetadataTransformer transformer;
    /** The transformer for the representation transformer.*/
    private final MetadataTransformer representationTransformer;
    /** The bitrepository preserver.*/
    private final BitmagPreserver preserver;

    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformer The metadata transformer for transforming the metadata.
     * @param representationTransformer The metadata transformer for the representation metadata.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     */
    public PreservationWorkflow(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformer transformer, MetadataTransformer representationTransformer, 
            BitmagPreserver preserver) {
        this.conf = transConf;
        this.server = server;
        this.transformer = transformer;
        this.representationTransformer = representationTransformer;
        this.preserver = preserver;
    }

    /**
     * Run the workflow!
     */
    public void run() {
        for(String catalogName : server.getCatalogNames()) {
            log.debug("Run '" + this.getClass().getName() + "' on catalog: " + catalogName);

            try {
                runOnCatalog(catalogName);
            } catch (RuntimeException e) {
                log.error("Cannot handle the catalog '" + catalogName + "'. It might not exist, "
                        + "or it might be lacking rights, or be corrupt", e);
            }
        }
    }

    /**
     * Run the extraction on a given catalog, and preserve each Cumulus record.
     * First preserves the sub-asset records, then the master records, and finally all the remaining records.
     * @param catalogName The name of the catalog.
     */
    protected void runOnCatalog(String catalogName) {
        // First on the sub-asset records
        CumulusQuery query = CumulusQuery.getPreservationSubAssetQuery(catalogName);
        RecordItemCollection items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " sub-asset records to be preserved.");
        preserveRecordItems(items, catalogName);
        // Then on master records
        query = CumulusQuery.getPreservationMasterAssetQuery(catalogName);
        items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " master records to be preserved.");
        preserveRecordItems(items, catalogName);
        // Finally on the rest
        query = CumulusQuery.getPreservationAllQuery(catalogName);
        items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " remaining records to be preserved.");
        preserveRecordItems(items, catalogName);
    }

    /**
     * Preserves all the record items of the given collection. 
     * @param items The collection of record items to preserve.
     */
    protected void preserveRecordItems(RecordItemCollection items, String catalogName) {
        int n = items.getItemCount();
        if(n == 0) { 
            return;
        }
        FieldExtractor fe = new FieldExtractor(items.getLayout(), server, catalogName);
        for(Item item : items) {
            try {
                log.debug("Initiating preservation on '" + item.getDisplayString() + "'");
                CumulusRecord record = new CumulusRecord(fe, item);
                sendRecordToPreservation(record);
            } catch (RuntimeException e) {
                log.error("Runtime exception caught while trying to handle Cumulus item with ID '" + item.getID() 
                + "'. Something must be seriously wrong with that item!!!\n Trying to handle next item.", e);
            }
        }
    }

    /**
     * Preserves the record, and if it is a master-asset, then it is 
     * @param record The given Cumulus record to preserve.
     */
    protected void sendRecordToPreservation(CumulusRecord record) {
        try {
            record.initFields();
            record.resetMetadataGuid();
            record.validateRequiredFields(conf.getRequiredFields());

            preserveRecord(record);

            if(record.isMasterAsset()) {
                preserveMasterAsset(record);
            }
        } catch (Exception e) {
            log.warn("Preserving the record '" + record + "' failed.", e);
            record.setPreservationFailed("Failed to preserve record '" + record.getUUID() + ": \n" + e.getMessage());
        }
    }

    /**
     * Preserves a given record.
     * @param record The given Cumulus record to preserve.
     * @throws IOException 
     */
    protected void preserveRecord(CumulusRecord record) throws IOException {
        File metadataFile = transformAndValidateMetadata(record);

        setMetadataStandardsForRecord(record, metadataFile);
        preserver.packRecord(record, metadataFile);
    }

    /**
     * Preserve the representation part of a master asset as its own METS.
     * @param record The Cumulus record.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    protected void preserveMasterAsset(CumulusRecord record) throws FileNotFoundException, IOException {
        String origMetadataGuid = record.getMetadataGUID();
        String representationMetadataGuid = UUID.randomUUID().toString();
        String combinedMetadataGuid = origMetadataGuid + "##" + representationMetadataGuid;
        record.setStringValueInField(Constants.PreservationFieldNames.METADATA_GUID, combinedMetadataGuid);

        String origIntellectualGuid = record.getFieldValue(
                Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        if(!origIntellectualGuid.contains("##")) {
            String representationIntellectualGuid = UUID.randomUUID().toString();
            String combinedIntellectualGuid = origIntellectualGuid + "##" + representationIntellectualGuid;
            record.setStringValueInField(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY, 
                    combinedIntellectualGuid);
        }
        File metadataFile = new File(conf.getMetadataTempDir(), representationMetadataGuid);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getMetadataTempDir(), representationMetadataGuid + "_raw.xml");
            representationTransformer.transformXmlMetadata(record.getMetadata(cumulusMetadataFile), os);
            os.flush();
        }

        transformer.validate(new FileInputStream(metadataFile));
        preserver.packMetadataRecordWithoutCumulusReference(metadataFile, 
                record.getFieldValue(Constants.PreservationFieldNames.COLLECTIONID));
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
            File cumulusMetadataFile = new File(conf.getMetadataTempDir(), metadataUUID + "_raw.xml");
            transformer.transformXmlMetadata(record.getMetadata(cumulusMetadataFile), os);
            os.flush();
        }

        transformer.validate(new FileInputStream(metadataFile));

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
        if(true) {
            return;
        }
        Collection<String> namespaces = transformer.getMetadataStandards(new FileInputStream(metadataFile));
        StringBuilder value = new StringBuilder();
        for(String s : namespaces) {
            value.append(s);
            value.append("\n");
        }
        log.warn("TODO: set Metadata Standards: " + value.toString());
        //        record.setStringValueInField(fieldName, value);
    }
}
