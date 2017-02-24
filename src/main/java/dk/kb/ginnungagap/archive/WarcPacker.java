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
import dk.kb.yggdrasil.warc.YggdrasilWarcConstants;

/**
 * Packages the warc files.
 */
public class WarcPacker {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(WarcPacker.class);

    /** The content type for the metadata. */
    public static final String METADATA_CONTENT_TYPE = "text/xml";

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
        // TODO: write this in a nicer way.
        payload.append("description: http://id.kb.dk/authorities/agents/kbDkCumulusBevaringsService.html\n");
        payload.append("conformsTo: http://bibnum.bnf.fr/WARC/WARC_ISO_28500_version1_latestdraft.pdf\n");
        payload.append("revision: 1.0.0\n");

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
     * @param metadataFile The file with the transformed metadata.
     */
    public synchronized void packRecord(CumulusRecord record, File resourceFile, File metadataFile) {
        ContentType contentType = getContentType(record);
        WarcDigest blockDigest = ChecksumUtils.calculateChecksum(resourceFile, bitmagConf.getAlgorithm());

        Uri resourceUUID = packResource(resourceFile, blockDigest, contentType, record.getUUID());
        packMetadata(metadataFile, resourceUUID);

        packagedRecords.add(record);
    }
    
    /**
     * Packages a file in a WARC-resource.
     * @param metadataFile The file with the metadata. The name of the file must be the same 
     * as the UUID of the metadata record.
     * @param resourceUUID The UUID of the resource, so it can be references in the WARC header metadata.
     */
    protected Uri packResource(File resourceFile, WarcDigest blockDigest, ContentType contentType, String uuid) {
        try (InputStream in = new FileInputStream(resourceFile)) {
            Uri res = warcWrapper.writeResourceRecord(in, resourceFile.length(), contentType, blockDigest, uuid);
            log.debug("Packed file '" + resourceFile.getName() + "' for uuid '" + uuid + "', and the record received "
                    + "the URI:" + res + "'");
            return res;
        } catch (Exception e) {
            throw new IllegalStateException("Could not package the metadata into the WARC file.", e);
        }
    }

    /**
     * Packages a metadata file.
     * @param metadataFile The file with the metadata. The name of the file must be the same 
     * as the UUID of the metadata record.
     * @param resourceUUID The UUID of the resource, so it can be references in the WARC header metadata.
     */
    protected void packMetadata(File metadataFile, Uri resourceUUID) {
        try (InputStream in = new FileInputStream(metadataFile)) {
            Digest digestor = new Digest(bitmagConf.getAlgorithm());
            WarcDigest blockDigest = digestor.getDigestOfFile(metadataFile);
            warcWrapper.writeMetadataRecord(in, metadataFile.length(), 
                    ContentType.parseContentType(METADATA_CONTENT_TYPE), resourceUUID, blockDigest, 
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
     */
    public void reportSucces(WarcDigest checksumDigest) {
        for(CumulusRecord r : packagedRecords) {
            r.setStringValueInField(Constants.PreservationFieldNames.METADATAPACKAGEID, warcWrapper.getWarcFileId());
            r.setStringValueInField(Constants.PreservationFieldNames.RESOURCEPACKAGEID, warcWrapper.getWarcFileId());
            r.setStringValueInField(Constants.FieldNames.ARCHIVE_MD5, checksumDigest.digestString);
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
     * Retrieve the content type for the Cumulus record.
     * @param record The record.
     * @return The content type.
     */
    protected ContentType getContentType(CumulusRecord record) {
        // TODO this part!
        return ContentType.parseContentType("application/binary");
    }
}
