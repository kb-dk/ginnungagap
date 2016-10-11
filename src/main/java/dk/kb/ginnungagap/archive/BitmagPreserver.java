package dk.kb.ginnungagap.archive;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dk.kb.ginnungagap.record.Record;

/**
 * API for packaging data from Cumulus in Warc files and sending it to the Bitrepository.
 * 
 */
public class BitmagPreserver {
    /** The maximum size of the WARC file, before sending it to the bitrepository and begining a new one.*/
    public static final long MAX_FILE_SIZE = 1000000000L;
    
    /** The archive, interface for the Bitrepository.
     * No other archive should be implemented.*/
    protected final Archive archive;
    
    /** Mapping between active warc packers and their collection.*/
    protected final Map<String, WarcPacker> warcPackerForCollection;
    
    /**
     * Constructor.
     * @param archive The archive where the data should be sent.
     */
    public BitmagPreserver(Archive archive) {
        this.archive = archive;
        this.warcPackerForCollection = new HashMap<String, WarcPacker>();
    }
    
    /**
     * Retrieves the Warc packer for a given Bitrepository collection.
     * If no Warc packer exists for the given Bitrepository collection, then a new one is created.
     * @param collectionId The id of the collection.
     * @return The Warc packer for a given Bitrepository collection.
     */
    protected WarcPacker getWarcPacker(String collectionId) {
        if(!warcPackerForCollection.containsKey(collectionId)) {
            warcPackerForCollection.put(collectionId, new WarcPacker());
        }
        return warcPackerForCollection.get(collectionId);
    }
    
    /**
     * Pack a record along with its transformed metadata.
     * @param record The record to preserve.
     * @param metadataFile The transformed metadata for the record, which should also be preserved.
     * @param collectionId The id of the collection.
     */
    public void packRecord(Record record, File metadataFile, String collectionId) {
        WarcPacker wp = getWarcPacker(collectionId);
        wp.packRecord(record, metadataFile);
    }
    
    /**
     * Checks the conditions for all the current instantiated warc packers.
     * If any of the them satisfies the conditions, then the file is finished and sent to the archive.
     */
    public void checkConditions() {
        for(Map.Entry<String, WarcPacker> warc : warcPackerForCollection.entrySet()) {
            if(warc.getValue().getSize() > MAX_FILE_SIZE) {
                String collectionId = warc.getKey();
                WarcPacker wp = warc.getValue();
                wp.close();
                
                archive.uploadFile(wp.getWarcFile(), collectionId);
                wp.reportSucces();
                
                warcPackerForCollection.remove(collectionId);
            }
        }
    }
}
