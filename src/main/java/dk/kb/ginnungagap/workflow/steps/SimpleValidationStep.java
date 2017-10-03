package dk.kb.ginnungagap.workflow.steps;

import java.util.Map;

import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.metadata.utils.CalendarUtils;
import dk.kb.yggdrasil.bitmag.Bitrepository;

/**
 * Workflow step for simple validation of a specific Cumulus catalog.
 */
public class SimpleValidationStep extends ValidationStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(SimpleValidationStep.class);
    /** The Bitmag client*/
    protected final Bitrepository bitmag;

    /**
     * Constructor.
     * @param server The Cumulus server.
     * @param catalogName The name of the catalog.
     * @param bitmag The bitrepository where the data must be validated.
     */
    public SimpleValidationStep(CumulusServer server, String catalogName, Bitrepository bitmag) {
        super(server, catalogName, Constants.FieldValues.PRESERVATION_VALIDATION_SIMPLE_CHECK);
        this.bitmag = bitmag;
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
            Map<String, ChecksumsCompletePillarEvent> completeEvents = bitmag.getChecksums(warcId, collectionId);
            String checksumResult = ChecksumUtils.getAgreedChecksum(completeEvents.values());
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
