package dk.kb.ginnungagap.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jwat.common.ContentType;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.warc.Digest;
import dk.kb.yggdrasil.warc.WarcWriterWrapper;

/**
 * Packages the warc files.
 */
public class WarcPacker {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(WarcPacker.class);

    /** The content type for the metadata. */
    public static final String METADATA_CONTENT_TYPE = "text/xml";
    
    /** The header value for WARC info records.*/
    protected static final String INFO_RECORD_HEADER = 
            "description: http://id.kb.dk/authorities/agents/kbDkCumulusBevaringsService.html\n"
                    + "conformsTo: http://bibnum.bnf.fr/WARC/WARC_ISO_28500_version1_latestdraft.pdf\n"
                    + "revision: 1.0.0\n";

    /** The warc writer wrapper, for writing the warc records.*/
    protected final WarcWriterWrapper warcWrapper;
    /** The records which has been packaged in the warc file.*/
    protected final List<CumulusRecord> packagedRecords;
    /** The configuration for the bitrepository.*/
    protected final BitmagConfiguration bitmagConf;

    /**
     * Constructor.
     * @param conf Configuration for the bitrepository.
     */
    public WarcPacker(BitmagConfiguration conf) {
        this.bitmagConf = conf;
        this.packagedRecords = new ArrayList<CumulusRecord>();

        try {
            this.warcWrapper = WarcWriterWrapper.getWriter(conf.getTempDir(), UUID.randomUUID().toString());
            writeWarcinfo();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialise the warc writer wrapper.", e);
        }
    }
    
    /**
     * Write the warc info of the WARC file.
     * This should be done as the first thing after instantiating a new WARC file. 
     * @throws YggdrasilException If it fails to write the warc info.
     */
    protected void writeWarcinfo() throws YggdrasilException {
        Digest digestor = new Digest(bitmagConf.getAlgorithm());
        StringBuffer payload = new StringBuffer();
        payload.append(INFO_RECORD_HEADER);

        for(Object key : System.getProperties().keySet()) {
            String value = System.getProperty((String) key);
            payload.append((String) key + ": " + value + "\n");
        }
        for(Map.Entry<String, String> property : System.getenv().entrySet()) {
            payload.append(property.getKey() + ": " + property.getValue() + "\n");
        }
        
        byte[] warcInfoPayloadBytes = payload.toString().getBytes(StandardCharsets.UTF_8);
        warcWrapper.writeWarcinfoRecord(warcInfoPayloadBytes,
                digestor.getDigestOfBytes(warcInfoPayloadBytes));
    }

    /**
     * Pack a record into the Warc file.
     * @param record The record from Cumulus.
     * @param resourceFile The Cumulus Asset file for the record.
     */
    public synchronized void packRecordAssetFile(CumulusRecord record, File resourceFile) {
        ContentType contentType = getContentType(record);
        WarcDigest blockDigest = ChecksumUtils.calculateChecksum(resourceFile, bitmagConf.getAlgorithm());

        packResource(resourceFile, blockDigest, contentType, record.getUUID());
        addRecordToPackagedList(record);
    }
    
    /**
     * Packages a file in a WARC-resource.
     * @param metadataFile The file with the metadata. The name of the file must be the same 
     * as the UUID of the metadata record.
     * @param resourceUUID The UUID of the resource, so it can be references in the WARC header metadata.
     */
    protected void packResource(File resourceFile, WarcDigest blockDigest, ContentType contentType, String uuid) {
        try (InputStream in = new FileInputStream(resourceFile)) {
            Uri uri = warcWrapper.writeResourceRecord(in, resourceFile.length(), contentType, blockDigest, uuid);
            log.debug("Packed file '" + resourceFile.getName() + "' for uuid '" + uuid + "', and the record received "
                    + "the URI:" + uri + "'");
        } catch (Exception e) {
            throw new IllegalStateException("Could not package the metadata into the WARC file.", e);
        }
    }
    
    /**
     * Packages a metadata file.
     * @param metadataFile The file with the metadata. The name of the file must be the same 
     * as the UUID of the metadata record.
     * @param refersTo Value for the refers-to elements in the warc record header. This may be null.
     */
    protected void packMetadata(File metadataFile, Uri refersTo) {
        try (InputStream in = new FileInputStream(metadataFile)) {
            Digest digestor = new Digest(bitmagConf.getAlgorithm());
            WarcDigest blockDigest = digestor.getDigestOfFile(metadataFile);
            warcWrapper.writeMetadataRecord(in, metadataFile.length(), 
                    ContentType.parseContentType(METADATA_CONTENT_TYPE), refersTo, blockDigest, 
                    metadataFile.getName());
        } catch (Exception e) {
            throw new IllegalStateException("Could not package the metadata into the WARC file.", e);
        }
    }

    /**
     * @return The current size of the warc file.
     */
    public long getSize() {
        return warcWrapper.getWarcFileSize();
    }

    /**
     * @return The warc file with the data.
     */
    public File getWarcFile() {
        return warcWrapper.getWarcFile();
    }

    /**
     * Close this warc packer, thus closing any streams and files.
     * This should be called before accessing the file and sending it to the archive.
     */
    public void close() {
        try {
            warcWrapper.close();
        } catch (YggdrasilException e) {
            throw new IllegalStateException("Issue occured while closing the resources of the warc file", e);
        }
    }

    /**
     * Reports back to Cumulus, that the preservation was successful for all records.
     * Should only be called after the warc packer has been closed and send to the archive.
     * @param checksumDigest The digest for the whole WARC file.
     */
    public void reportSucces(WarcDigest checksumDigest) {
        for(CumulusRecord r : packagedRecords) {
            r.setStringValueInField(Constants.FieldNames.METADATAPACKAGEID, warcWrapper.getWarcFileId());
            r.setStringValueInField(Constants.FieldNames.RESOURCEPACKAGEID, warcWrapper.getWarcFileId());
            r.setStringValueInField(Constants.FieldNames.ARCHIVE_MD5, checksumDigest.digestString);
            // TODO: tilføj tid for bevaring
            r.setPreservationFinished();
        }
    }

    /**
     * Report back to Cumulus, that the preservation failed for all records.
     * @param reason The message regarding the reason for the failure.
     */
    public void reportFailure(String reason) {
        for(CumulusRecord r : packagedRecords) {
            r.setPreservationFailed(reason);
        }
    }
    
    /**
     * Adds the record to the list of packaged records, unless it already is part of the list.
     * @param record The record 
     */
    public void addRecordToPackagedList(CumulusRecord record) {
        if(!packagedRecords.contains(record)) {
            packagedRecords.add(record);
        }
    }

    /**
     * Retrieve the content type for the Cumulus record.
     * Use the field 'FILE FORMAT IDENTIFIER'. If empty try to decode the 'FILE FORMAT'. Otherwise binary.
     * @param record The record.
     * @return The content type.
     */
    protected ContentType getContentType(CumulusRecord record) {
        ContentType res = ContentType.parseContentType(record.getFieldValueOrNull(
                Constants.FieldNames.FILE_FORMAT_IDENTIFIER));
        
        if(res == null) {
            String format = record.getFieldValueOrNull(Constants.FieldNames.FILE_FORMAT);
            if(format != null && format.startsWith("TIFF")) {
                res = ContentType.parseContentType("image/tiff");
            }
            res = ContentType.parseContentType("application/binary");
        }

        return res;
    }
}
