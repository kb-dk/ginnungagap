package dk.kb.ginnungagap.archive;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.metadata.utils.GuidExtractionUtils;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API for packaging data from Cumulus in Warc files and sending it to the Bitrepository.
 * 
 */
@Component
public class BitmagPreserver {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(BitmagPreserver.class);
    
    /** The archive, interface for the Bitrepository.
     * No other archive should be implemented.*/
    @Autowired
    protected ArchiveWrapper archive;
    /** The configuration.*/
    @Autowired
    protected Configuration conf;

    /** Mapping between active warc packers and their collection.*/
//    protected final Map<String, WarcPacker> warcPackerForCollection = new HashMap<String, WarcPacker>(); // original
    protected final Map<String, WarcPacker> warcPackerForCollection =  new ConcurrentHashMap<String, WarcPacker>();
//    protected final Map<String, WarcPacker> warcPackerForCollection = Collections.synchronizedMap(new HashMap<String, WarcPacker>();

    /**
     * Retrieves the Warc packer for a given Bitrepository collection.
     * If no Warc packer exists for the given Bitrepository collection, then a new one is created.
     * @param collectionId The id of the collection.
     * @return The Warc packer for a given Bitrepository collection.
     */
    protected WarcPacker getWarcPacker(String collectionId) {
        synchronized(warcPackerForCollection) {
            if(!warcPackerForCollection.containsKey(collectionId)) {
                log.debug("Create new WarPacker, collection: {}", collectionId);
                warcPackerForCollection.put(collectionId, new WarcPacker(conf.getBitmagConf()));
            }
            log.debug("Return WarcPacker for collection: {}", collectionId);
            return warcPackerForCollection.get(collectionId);
        }
    }
    
    /**
     * Packages the Asset File of a Cumulus record.
     * @param record The record to package.
     */
    public void packRecordResource(CumulusRecord record) {
        WarcPacker wp = getWarcPacker(record.getFieldValue(Constants.FieldNames.COLLECTION_ID));
        log.debug("In packRecordResource");
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
            WarcPacker wp = getWarcPacker(record.getFieldValue(Constants.FieldNames.COLLECTION_ID));
            log.debug("In packRecordMetadata");
            String fileGuid = GuidExtractionUtils.extractGuid(record.getFieldValue(Constants.FieldNames.GUID));

            Uri refersToUri = new Uri("urn:uuid:" + fileGuid);
            wp.packMetadata(metadataFile, refersToUri, metadataFile.getName());
            wp.addRecordToMetadataPackagedList(record);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not package metadata.", e);
        }
    }
    
    /**
     * Packages the metadata of a representation. 
     * @param metadataFile The file with the metadata.
     * @param collectionID The ID of the preservation collection, where the metadata must be preserved.
     * @param warcRecordId The ID of the warc record.
     */
    public void packRepresentationMetadata(File metadataFile, String collectionID, String warcRecordId) {
        if(warcRecordId == null) {
            warcRecordId = metadataFile.getName();
        }
        WarcPacker wp = getWarcPacker(collectionID);
        log.debug("packRepresentationMetadata: WarcPacker created");
        wp.packMetadata(metadataFile, null, warcRecordId);
    }
    
    /**
     * Checks the conditions for all the current instantiated warc packers.
     * If any of the them satisfies the conditions, then the file is finished and sent to the archive.
     */
    public void checkConditions() {
        log.debug("In checkConditions. ");
        for(Map.Entry<String, WarcPacker> warc : warcPackerForCollection.entrySet()) {
            if(warc.getValue().getSize() > conf.getBitmagConf().getWarcFileSizeLimit()) {
                String collectionId = warc.getKey();
                uploadWarcFile(collectionId);
            }
        }
    }
    
    /**
     * Uploads all warc files to their given collection.
     */
    public void uploadAll() {
        log.debug("In uploadAll");
        for(String collectionId : warcPackerForCollection.keySet()) {
            log.debug("uploadAll: collectionID: {}, thread ID: {}", collectionId, Thread.currentThread().getId());
            uploadWarcFile(collectionId);
        }
    }
    
    /**
     * Performs the upload of the warc file for the given collection.
     * @param collectionId The id of the collection to upload to.
     */
    protected synchronized void uploadWarcFile(String collectionId) {
        synchronized(warcPackerForCollection) {
            WarcPacker wp = warcPackerForCollection.get(collectionId);
            log.debug("In uploadWarcFile: collectionId= {}, thread ID: {}", collectionId, Thread.currentThread().getId());
            wp.close();
            if(!wp.hasContent()) {
                log.info("WARC file without content for collection '" + collectionId + "' will not be uploaded.");
                FileUtils.deleteFile(wp.getWarcFile());
                warcPackerForCollection.remove(collectionId);
                return;
            }

            log.info("Uploading warc file for collection '" + collectionId + "'");
            WarcDigest checksumDigest = ChecksumUtils.calculateChecksum(wp.getWarcFile(), ChecksumUtils.MD5_ALGORITHM);

            boolean uploadSuccess = archive.uploadFile(wp.getWarcFile(), collectionId);
            if(uploadSuccess) {
                log.info("Successfully uploaded the WARC file '" + wp.getWarcFile().getName() + "'"); 
                wp.reportSucces(checksumDigest);
            } else {
                log.warn("Failed to upload the file '" + wp.getWarcFile().getName() + "'. "
                        + "Keeping it in temp dir: '" + conf.getBitmagConf().getTempDir().getAbsolutePath() + "'");
                wp.reportFailure("Could not upload the file to the archive.");
            }
            warcPackerForCollection.remove(collectionId);
        }
    }
}
