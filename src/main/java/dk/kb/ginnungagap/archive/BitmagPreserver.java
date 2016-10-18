package dk.kb.ginnungagap.archive;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;

/**
 * API for packaging data from Cumulus in Warc files and sending it to the Bitrepository.
 * 
 */
public class BitmagPreserver {
    /** The logger.*/
    private final static Logger log = LoggerFactory.getLogger(BitmagPreserver.class);

    /** The archive, interface for the Bitrepository.
     * No other archive should be implemented.*/
    protected final BitmagArchive archive;
    /** The configuration for the bitrepository.*/
    protected final BitmagConfiguration bitmagConf;
    
    /** Mapping between active warc packers and their collection.*/
    protected final Map<String, WarcPacker> warcPackerForCollection;
    
    /**
     * Constructor.
     * @param archive The archive where the data should be sent.
     */
    public BitmagPreserver(BitmagArchive archive, BitmagConfiguration bitmagConf) {
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
     * Pack a record along with its transformed metadata.
     * @param record The record to preserve.
     * @param metadataFile The transformed metadata for the record, which should also be preserved.
     */
    public void packRecord(CumulusRecord record, File metadataFile) {
        String collectionId = record.getFieldValue(Constants.PreservationFieldNames.COLLECTIONID);
        WarcPacker wp = getWarcPacker(collectionId);
        wp.packRecord(record, metadataFile);
        checkConditions();
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
        log.info("Uploading warc file for collection '" + collectionId + "'");
        WarcPacker wp = warcPackerForCollection.get(collectionId);
        wp.close();
        
        archive.uploadFile(wp.getWarcFile(), collectionId);
        wp.reportSucces();
        
        warcPackerForCollection.remove(collectionId);        
    }
}
