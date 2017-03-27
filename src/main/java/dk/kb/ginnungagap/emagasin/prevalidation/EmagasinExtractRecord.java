package dk.kb.ginnungagap.emagasin.prevalidation;

import dk.kb.metadata.utils.GuidExtrationUtils;

/**
 * Class for extracted entries for the Emagasin through the batchjob.
 * It contains the name of the arc file, the uuid, the size and the checksum of the record.
 * It must be converted from a line in the format:
 * ARCFILE ## UUID ## SIZE ## CHECKSUM
 * E.g. 
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/742a6c00-908a-11e2-a385-0016357f605f#0##47667682##e113fb8d9b20515056b53eff07c98a45
 */
class EmagasinExtractRecord {
    /** Name of the arc file.*/
    protected final String arcFilename;
    /** UUID of the record.*/
    protected final String uuid;
    /** The size of the record.*/
    protected final Long size;
    /** The checksum of the record.*/
    protected final String checksum;
    /** The complete line.*/
    protected final String line;
    
    /** Whether or not this specific record has been found.*/
    protected boolean found;
    
    /**
     * Constructor.
     * @param arcFilename Name of the arc file.
     * @param uuid UUID of the record.
     * @param size The size of the record.
     * @param checksum The checksum of the record.
     * @param line The complete line.
     */
    private EmagasinExtractRecord(String arcFilename, String uuid, Long size, String checksum, String line) {
        this.arcFilename = arcFilename;
        this.uuid = GuidExtrationUtils.extractGuid(uuid);
        this.size = size;
        this.checksum = checksum;
        this.line = line;
    }
    
    /**
     * @return Name of the arc file.
     */
    public String getArcFilename() {
        return arcFilename;
    }
    
    /**
     * @return UUID of the record.
     */
    public String getUuid() {
        return uuid;
    }
    
    /**
     * @return The size of the record.
     */
    public Long getSize() {
        return size;
    }
    
    /**
     * @return The checksum of the record.
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * @return The complete line.
     */
    public String getLine() {
        return line;
    }
    
    /**
     * @return Whether or not this record has been found.
     */
    public boolean hasBeenFound() {
        return found;
    }
    
    /**
     * Sets this record to found.
     */
    public void setFound() {
        found = true;
    }
    
    /**
     * Creates the EmagasinExtractRecord from a line in the batchjob output.
     * @param line The line from the batchjob output.
     * @return The EmagasinExtractRecord for the line, or null if the line is invalid.
     */
    protected static EmagasinExtractRecord getArchiveRecord(String line) {
        String[] split = line.split("##");
        if(split.length < 4) {
            return null;
        }
        String arcFilename = split[0];
        String uuid = split[1];
        Long size = Long.valueOf(split[2]);
        String checksum = split[3];
        return new EmagasinExtractRecord(arcFilename, uuid, size, checksum, line);
    }
}
