package dk.kb.ginnungagap.workflow.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jwat.warc.WarcDigest;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.StreamUtils;
import dk.kb.yggdrasil.bitmag.Bitrepository;

/**
 * Workflow step for simple validation of a specific Cumulus catalog.
 */
public class FullValidationStep extends ValidationStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(FullValidationStep.class);

    /** The Bitmag client*/
    protected final Bitrepository bitmag;
    /** The configuration.*/
    protected final Configuration conf;
    
    /**
     * Constructor.
     * @param server The Cumulus server.
     * @param catalogName The name of the catalog.
     * @param bitmag The bitrepository where the data must be validated.
     */
    public FullValidationStep(CumulusServer server, String catalogName, Bitrepository bitmag, Configuration conf) {
        super(server, catalogName, Constants.FieldValues.PRESERVATION_VALIDATION_FULL_CHECK);
        this.bitmag = bitmag;
        this.conf = conf;
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
            String uuid = record.getUUID();
            File f = bitmag.getFile(warcId, collectionId, null);
            validateWarcFileChecksum(record, f);
            
            WarcRecord warcRecord = getWarcRecord(f, uuid);
            
            validateSize(warcRecord, record);
            validateRecordChecksum(warcRecord, record);
        } catch (IllegalStateException e) {
            String errMsg = "The record '" + record + "' is invalid: " + e.getMessage();
            log.info(errMsg, e);
            setInvalid(record, errMsg);            
        } catch (Exception e) {
            String errMsg = "Error when trying to validate record '" + record + "'";
            log.warn(errMsg, e);
            setInvalid(record, errMsg + " : " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the WARC record from the WARC file.
     * Will throw an exception, if the record is not found.
     * @param file The WARC file to extract the WARC record from.
     * @param recordId The id of the WARC record.
     * @return The WARC record.
     * @throws IOException If an error occurs when reading the WARC file.
     */
    protected WarcRecord getWarcRecord(File file, String recordId) throws IOException {
        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(file))) {
            for(WarcRecord record : reader) {
                if(record.header.warcRecordIdStr.contains(recordId)) {
                    return record;
                }
            }
            throw new IllegalStateException("Could not find the record '" + recordId + "' in the file '" 
                    + file.getName() + "'");
        }
    }
    
    /**
     * Validate the checksum of the WARC file.
     * @param cumulusRecord The Cumulus record with the expected checksum for the WARC file.
     * @param warcFile The WARC file to validate.
     */
    protected void validateWarcFileChecksum(CumulusRecord cumulusRecord, File warcFile) {
        WarcDigest digest = ChecksumUtils.calculateChecksum(warcFile, ChecksumUtils.MD5_ALGORITHM);
        String warcChecksum = digest.digestString;
        String cumulusWarcChecksum = cumulusRecord.getFieldValue(Constants.FieldNames.ARCHIVE_MD5);
        if(!warcChecksum.equals(cumulusWarcChecksum)) {
            throw new IllegalStateException("The WARC file checksum for the Cumulus record '" + cumulusRecord 
                    + "' is not valid! Expected: '" + cumulusWarcChecksum + "', but calculated it as: '" 
                    + warcChecksum + "'.");
        }
    }
    
    /**
     * Validate the size of the WARC record compared with the expected file data size in the Cumulus record.
     * @param warcRecord The WARC record.
     * @param cumulusRecord The Cumulus record.
     */
    protected void validateSize(WarcRecord warcRecord, CumulusRecord cumulusRecord) {
        long cumulusSize = cumulusRecord.getFieldLongValue(Constants.PreservationFieldNames.FILE_DATA_SIZE);
        long warcSize = warcRecord.getPayload().getTotalLength();
        if(cumulusSize != warcSize) {
            throw new IllegalStateException("Cumulus record expected the size '" + cumulusSize 
                    + "', but the Warc record was '" + warcSize + "'.");
        }
    }
    
    /**
     * Validates the checksum for the WARC record.
     * @param warcRecord The WARC record to have its checksum calculated.
     * @param cumulusRecord The Cumulus record.
     * @throws IOException If an issue occurs when calculating the checksum of the WARC record.
     */
    protected void validateRecordChecksum(WarcRecord warcRecord, CumulusRecord cumulusRecord) throws IOException {
        File tmpFile = new File(conf.getBitmagConf().getTempDir(), cumulusRecord.getUUID());
        
        WarcDigest digest;
        try (OutputStream os = new FileOutputStream(tmpFile)) {
            StreamUtils.copyInputStreamToOutputStream(warcRecord.getPayloadContent(), os);
            digest = ChecksumUtils.calculateChecksum(tmpFile, ChecksumUtils.MD5_ALGORITHM);
        } finally {
            tmpFile.delete();
        }
        
        String warcRecordChecksum = digest.digestString;
        String cumulusRecordChecksum = cumulusRecord.getFieldValue(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER);
        if(!warcRecordChecksum.contains(cumulusRecordChecksum)) {
            throw new IllegalStateException("The WARC record for '" + cumulusRecord.getUUID() + "' had the checksum '"
                    + warcRecordChecksum + "', but the Cumulus record expected '" + cumulusRecordChecksum + "'.");
        }
    }
}
