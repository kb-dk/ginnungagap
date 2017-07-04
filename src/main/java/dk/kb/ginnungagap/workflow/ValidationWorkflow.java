package dk.kb.ginnungagap.workflow;

import java.util.Map;

import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.metadata.utils.CalendarUtils;
import dk.kb.yggdrasil.bitmag.Bitrepository;

/**
 * Workflow for validating the records.
 * There are two types of validation:
 * Simple validation, and full validation.
 * 
 * The simple validation just checks the default checksum for the WARC file.
 * 
 * The full validation retrieves the file and validates the specific WARC-record.
 */
public class ValidationWorkflow {
    /** The configuration.*/
    protected final Configuration conf;
    /** The Cumulus Server.*/
    protected final CumulusServer server;
    /** The Bitmag client*/
    protected final Bitrepository bitmag;
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param server The Cumulus server.
     * @param bitmag The Bitrepository client.
     */
    public ValidationWorkflow(Configuration conf, CumulusServer server, Bitrepository bitmag) {
        this.conf = conf;
        this.server = server;
        this.bitmag = bitmag;
    }

    /**
     * Running the workflow.
     */
    public void run() {
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            simpleValidationOnCatalog(catalogName);
            fullValidationOnCatalog(catalogName);
        }
    }
    
    /**
     * Perform the simple validation on a specific catalog.
     * Will retrieve all the record, which should be validated from the given catalog, 
     * then each record is validated individually.
     * @param catalogName The name of the catalog.
     */
    protected void simpleValidationOnCatalog(String catalogName) {
        CumulusQuery query = CumulusQuery.getQueryForPreservationValidation(catalogName, 
                Constants.FieldValues.PRESERVATION_VALIDATION_SIMPLE_CHECK);
        
        RecordItemCollection items = server.getItems(catalogName, query);
        FieldExtractor fe = new FieldExtractor(items.getLayout());
        for(Item item : items) {
            CumulusRecord record = new CumulusRecord(fe, item);
            simpleValidationOnRecord(record);
        }
    }
    
    /**
     * Perform the simple validation on 
     * @param record
     */
    protected void simpleValidationOnRecord(CumulusRecord record) {
        try {
            String warcId = record.getFieldValue(Constants.PreservationFieldNames.RESOURCEPACKAGEID);
            String collectionId = record.getFieldValue(Constants.PreservationFieldNames.COLLECTIONID);
            Map<String, ChecksumsCompletePillarEvent> completeEvents = bitmag.getChecksums(warcId, collectionId);
            if(ChecksumUtils.validateChecksumResults(completeEvents.values())) {
                setValid(record);
            } else {
                String message = "WARC file exists, but it has an integrity issue. Discovered at: " 
                        + CalendarUtils.getCurrentDate();
                setInvalid(record, message);
            }
        } catch (Exception e) {
            setInvalid(record, "Error when trying to validate record '" + record + "': " + e.getMessage());
        }
    }
    
    protected void fullValidationOnCatalog(String catalogName) {
        // TODO: Implement!
    }
    
    /**
     * Report back that the validation of the record failed.
     * @param record The record which is invalid.
     * @param message The message regarding why the WARC file is invalid.
     */
    protected void setInvalid(CumulusRecord record, String message) {
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK, 
                Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE);
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK_STATUS, message);
    }

    /**
     * Report back that the validation was succes-full.
     * @param record The record which is valid.
     */
    protected void setValid(CumulusRecord record) {
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK, 
                Constants.FieldValues.PRESERVATION_VALIDATION_OK);
        String message = "Validated at: " + CalendarUtils.getCurrentDate();
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK_STATUS, message);
    }
}
