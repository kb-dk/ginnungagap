package dk.kb.ginnungagap.workflow.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.metadata.utils.CalendarUtils;

/**
 * Workflow step for simple validation of a specific Cumulus catalog.
 */
public class SimpleValidationStep extends ValidationStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(SimpleValidationStep.class);
    /** The Bitrepository client.*/
    protected final Archive archive;

    /**
     * Constructor.
     * @param server The Cumulus server.
     * @param catalogName The name of the catalog.
     * @param archive The bitrepository archive where the data must be validated.
     */
    public SimpleValidationStep(CumulusServer server, String catalogName, Archive archive) {
        super(server, catalogName, Constants.FieldValues.PRESERVATION_VALIDATION_SIMPLE_CHECK);
        this.archive = archive;
    }

    @Override
    public String getName() {
        return "Simple Validation Step for '" + catalogName + "'";
    }

    @Override
    protected void validateRecord(CumulusRecord record) {
        try {
            String warcId = record.getFieldValue(Constants.PreservationFieldNames.RESOURCEPACKAGEID);
            String collectionId = record.getFieldValue(Constants.PreservationFieldNames.COLLECTIONID);
            String checksumResult = archive.getChecksum(warcId, collectionId);
            String warcChecksum = record.getFieldValue(Constants.FieldNames.ARCHIVE_MD5);
            
            if(checksumResult.equalsIgnoreCase(warcChecksum)) {
                setValid(record);
            } else {
                throw new IllegalStateException("Checksums did not match. Expected '" + warcChecksum 
                        + "', but received '" + checksumResult + "' from the archive.");
            }
        } catch (IllegalStateException e) {
            log.info("Failed to validate the WARC file", e);
            String message = "WARC file exists, but it has an integrity issue. Discovered at: " 
                    + CalendarUtils.getCurrentDate();
            setInvalid(record, message);
        } catch (Exception e) {
            String errMsg = "Error when trying to validate record '" + record + "'";
            log.warn(errMsg, e);
            setInvalid(record, errMsg + " : " + e.getMessage());
        }
    }
}
