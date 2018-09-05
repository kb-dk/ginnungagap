package dk.kb.ginnungagap.workflow.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQueryUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;

/**
 * The step for performing the update preservation action on the records, which are ready for update.
 * This will extract the metadata, transform it, package it, and send it to preservation.
 * It will not preserve the content file, since the update preservation action, only involves preserving the
 * update metadata.
 * 
 * It will give the records new Metadata GUIDs, though it will use the existing GUID (for the file) and the 
 * Intellectuel Entity guid. 
 */
public class UpdatePreservationStep extends PreservationStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(UpdatePreservationStep.class);
    
    /** The name of the field for the history of preservation updates.*/
    public static final String PRESERVATION_UPDATE_HISTORY_FIELD_NAME = "Bevarings metadata historik";
    /** The header value for the field for the preservation update history.*/
    public static final String PRESERVATION_UPDATE_HISTORY_FIELD_HEADER = "MetadataGUID ## PackageID";

    /** The Cumulus server.*/
    protected final CumulusServer server;
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
    public UpdatePreservationStep(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformationHandler transformationHandler, BitmagPreserver preserver, String catalogName) {
        super(transConf, server, transformationHandler, preserver, catalogName);
        this.server = server;
        this.catalogName = catalogName;
    }

    @Override
    public String getName() {
        return "Preservation Update Step for catalog '" + catalogName + "'";
    }

    @Override
    public void performStep() throws Exception {
        CumulusQuery query = CumulusQueryUtils.getPreservationUpdateQuery(catalogName);
        CumulusRecordCollection items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getCount() + " records for preservation update.");
        preserveRecordItems(items, catalogName);
        setResultOfRun("Updated preservation for " + items.getCount() + " records.");
    }
    
    /**
     * Preserves all the record items of the given collection. 
     * @param items The collection of record items to preserve.
     * @param catalogName The name of the Cumulus catalog with the reccord to have their preservation updated.
     */
    @Override
    protected void preserveRecordItems(CumulusRecordCollection items, String catalogName) {
        int n = items.getCount();
        if(n == 0) {
            log.debug("No items for preservation update from catalog: " + catalogName);
            return;
        }
        for(CumulusRecord record : items) {
            try {
                log.debug("Initiating preservation update on record '" + record.getUUID() + "'");
                String oldMetadataReference = getOldMetadataReference(record);
                sendRecordToPreservation(record);
                saveUpdateMetadataHistoryReference(record, oldMetadataReference);
            } catch (Exception e) {
                log.error("Runtime exception caught while trying to handle Cumulus record '" 
                        + record.getUUID() + "'. Something must be seriously wrong with that item!!!\n"
                        + "Trying to handle next item.", e);
            }
        }
    }
    
    /**
     * Retrieves old value for the preservation update history field.
     * Or the header value for the field, if the field is empty.
     * @param record The Cumulus record with the current value for the preservation update history.
     * @return The current value for the preservation update history field. Or the header for the field.
     */
    protected String getOldMetadataReference(CumulusRecord record) {
        String previousMetadata = record.getFieldValueOrNull(PRESERVATION_UPDATE_HISTORY_FIELD_NAME);
        if(previousMetadata == null || previousMetadata.isEmpty()) {
            previousMetadata = PRESERVATION_UPDATE_HISTORY_FIELD_HEADER;
        }
        return previousMetadata;
    }
    
    /**
     * Sets the new value for the preservation update history field.
     * @param record The Cumulus record to have a new value for the preservation update history.
     * @param previousMetadata The previous value of the preservation update history field.
     */
    protected void saveUpdateMetadataHistoryReference(CumulusRecord record, String previousMetadata) {
        previousMetadata += "\n" + record.getFieldValue(Constants.FieldNames.METADATA_GUID) + " ## "
                + record.getFieldValue(Constants.FieldNames.METADATA_PACKAGE_ID);
        record.setStringValueInField(PRESERVATION_UPDATE_HISTORY_FIELD_NAME, previousMetadata);
    }

    /**
     * The content-file of the given record will not be preserved during a preservation update.
     * @param record The given Cumulus record with the file to preserve.
     */
    @Override
    protected void preserveFile(CumulusRecord record) {
        log.debug("Do not preserve the file again, when performing the preservation update.");
    }
}
