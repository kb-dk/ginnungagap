package dk.kb.ginnungagap.archive;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.metadata.utils.GuidExtractionUtils;

/**
 * API for packaging data from Cumulus in Warc files and sending it to the Bitrepository.
 * 
 */
public class BitmagPreserver {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(BitmagPreserver.class);

    /** The archive, interface for the Bitrepository.
     * No other archive should be implemented.*/
    protected final Archive archive;
    /** The configuration for the bitrepository.*/
    protected final BitmagConfiguration bitmagConf;

    /** Mapping between active warc packers and their collection.*/
    protected final Map<String, WarcPacker> warcPackerForCollection;

    /**
     * Constructor.
     * @param archive The archive where the data should be sent.
     * @param bitmagConf Configuration for the bitrepository.
     */
    public BitmagPreserver(Archive archive, BitmagConfiguration bitmagConf) {
        this.archive = archive;
        this.warcPackerForCollection = new HashMap<String, WarcPacker>();
        this.bitmagConf = bitmagConf;
    }

    /**
     * Retrieves the Warc packer for a given Bitrepository collection.
     * If no Warc packer exists for the given Bitrepository collection, then a new one is created.
     * @param collectionId The id of the collection.
     * @return The Warc packer for a given Bitrepository collection.
     */
    protected WarcPacker getWarcPacker(String collectionId) {
        if(!warcPackerForCollection.containsKey(collectionId)) {
            warcPackerForCollection.put(collectionId, new WarcPacker(bitmagConf));
        }
        return warcPackerForCollection.get(collectionId);
    }

    /**
     * Packages the Asset File of a Cumulus record.
     * @param record The record to package.
     */
    public void packRecordResource(CumulusRecord record) {
        WarcPacker wp = getWarcPacker(record.getPreservationCollectionID());
        File resourceFile = record.getFile();
        wp.packRecordAssetFile(record, resourceFile);
        wp.addRecordToPackagedList(record);
    }
    
    /**
     * Packages the metadata of a given Cumulus Record
     * @param record The Cumulus Record for the metadata.
     * @param metadataFile The file with the transformed metadata for the Cumulus Record.
     */
    public void packRecordMetadata(CumulusRecord record, File metadataFile) {
        try {
            WarcPacker wp = getWarcPacker(record.getPreservationCollectionID());
            String fileGuid = GuidExtractionUtils.extractGuid(record.getFieldValue(Constants.FieldNames.GUID));

            Uri refersToUri = new Uri("urn:uuid:" + fileGuid);
            wp.packMetadata(metadataFile, refersToUri);
            wp.addRecordToPackagedList(record);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not package metadata.", e);
        }
    }
    
    /**
     * Packages the metadata of a representation. 
     * @param metadataFile The file with the metadata.
     * @param collectionID The ID of the preservation collection, where the metadata must be preserved.
     */
    public void packRepresentationMetadata(File metadataFile, String collectionID) {
        WarcPacker wp = getWarcPacker(collectionID);
        wp.packMetadata(metadataFile, null);        
    }
    
    /**
     * Checks the conditions for all the current instantiated warc packers.
     * If any of the them satisfies the conditions, then the file is finished and sent to the archive.
     */
    public void checkConditions() {
        for(Map.Entry<String, WarcPacker> warc : warcPackerForCollection.entrySet()) {
            if(warc.getValue().getSize() > bitmagConf.getWarcFileSizeLimit()) {
                String collectionId = warc.getKey();
                uploadWarcFile(collectionId);
            }
        }
    }

    /**
     * Uploads all warc files to their given collection.
     */
    public void uploadAll() {
        for(String collectionId : warcPackerForCollection.keySet()) {
            uploadWarcFile(collectionId);
        }
    }

    /**
     * Performs the upload of the warc file for the given collection.
     * @param collectionId The id of the collection to upload to.
     */
    protected void uploadWarcFile(String collectionId) {
        WarcPacker wp = warcPackerForCollection.get(collectionId);
        wp.close();
        if(!wp.hasContent()) {
            log.debug("WARC file without content for collection '" + collectionId + "' will not be uploaded.");
            FileUtils.deleteFile(wp.getWarcFile());
            return;
        }

        log.info("Uploading warc file for collection '" + collectionId + "'");
        WarcDigest checksumDigest = ChecksumUtils.calculateChecksum(wp.getWarcFile(), ChecksumUtils.MD5_ALGORITHM);

        boolean uploadSucces = archive.uploadFile(wp.getWarcFile(), collectionId);
        if(uploadSucces) {
            log.info("Successfully uploaded the WARC file '" + wp.getWarcFile().getName() + "'"); 
            wp.reportSucces(checksumDigest);
        } else {
            log.warn("Failed to upload the file '" + wp.getWarcFile().getName() + "'. "
                    + "Keeping it in temp dir: '" + bitmagConf.getTempDir().getAbsolutePath() + "'");
            wp.reportFailure("Could not upload the file to the archive.");
        }
        warcPackerForCollection.remove(collectionId);
    }
}
