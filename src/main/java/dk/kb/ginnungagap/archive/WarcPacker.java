package dk.kb.ginnungagap.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jwat.common.ContentType;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.warc.Digest;
import dk.kb.yggdrasil.warc.WarcWriterWrapper;
import dk.kb.yggdrasil.warc.YggdrasilWarcConstants;

/**
 * Packages the warc files.
 */
public class WarcPacker {
    /** The digest algorithm.*/
    public static final String DIGEST_ALGORITHM = "SHA-1";
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
            // TODO make warc info?
            Digest digestor = new Digest("SHA-1");
            String warcInfoPayload = YggdrasilWarcConstants.getWarcInfoPayload();
            byte[] warcInfoPayloadBytes = warcInfoPayload.getBytes("UTF-8");
            warcWrapper.writeWarcinfoRecord(warcInfoPayloadBytes,
                    digestor.getDigestOfBytes(warcInfoPayloadBytes));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialise the warc writer wrapper.", e);
        }
    }
    
    /**
     * Pack a record into the Warc file.
     * @param record The record from Cumulus.
     * @param metadataFile The file with the transformed metadata.
     */
    public synchronized void packRecord(CumulusRecord record, File metadataFile) {
        ContentType contentType = getContentType(record);
        String uuid = getUUID(record);
        Uri resourceUUID = packResource(record.getFile(), contentType, uuid);
        packMetadata(metadataFile, resourceUUID);

        packagedRecords.add(record);
    }
    
    /**
     * Packages a metadata file.
     * @param metadataFile The file with the metadata. The name of the file must be the same 
     * as the UUID of the metadata record.
     * @param resourceUUID The UUID of the resource, so it can be references in the WARC header metadata.
     */
    protected Uri packResource(File resourceFile, ContentType contentType, String uuid) {
        try (InputStream in = new FileInputStream(resourceFile)) {
            Digest digestor = new Digest(DIGEST_ALGORITHM);
            WarcDigest blockDigest = digestor.getDigestOfFile(resourceFile);
            Uri res = warcWrapper.writeResourceRecord(in, resourceFile.length(), contentType, blockDigest, uuid);
            // TODO log this!
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
            Digest digestor = new Digest(DIGEST_ALGORITHM);
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
    public void reportSucces() {
        for(CumulusRecord r : packagedRecords) {
            r.setPreservationResourcePackage(warcWrapper.getWarcFileId());
            r.setPreservationMetadataPackage(warcWrapper.getWarcFileId());
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
    
    protected ContentType getContentType(CumulusRecord record) {
        // TODO this part!
        return ContentType.parseContentType("application/binary");
    }
    
    protected String getUUID(CumulusRecord record) {
        // TODO this part!!
        return "" + record.getID();
    }
}
