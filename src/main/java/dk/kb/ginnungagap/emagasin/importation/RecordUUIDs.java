package dk.kb.ginnungagap.emagasin.importation;

/**
 * The UUIDs for an entry.
 * Both the record of the ARC-file and the record in Cumulus.
 * This is made since some ARC-record in E-magasinet does not have the same UUID
 * as the corresponding Cumulus record.
 */
public class RecordUUIDs {
    /** The id of the Cumulus catalog.*/
    protected final String catalogID;
    /** The name of the ARC file.*/
    protected final String arcFilename;
    /** The UUID of the ARC record.*/
    protected final String arcRecordUUID;
    /** The UUID of the Cumulus record.*/
    protected final String cumulusRecordUUID;
    /** Whether or not it has been found.*/
    protected boolean found;
    
    /**
     * Constructor.
     * @param arcRecord The uuid of the arc record.
     * @param cumulusRecord The uuid of the cumulus record.
     */
    public RecordUUIDs(String catalogID, String arcFilename, String arcRecord, String cumulusRecord) {
        this.catalogID = catalogID;
        this.arcFilename = arcFilename;
        this.arcRecordUUID = arcRecord;
        this.cumulusRecordUUID = cumulusRecord;
        this.found = false;
    }
    
    /**
     * @return The id of the Cumulus catalog.
     */
    public String getCatalogID() {
        return catalogID;
    }
    
    /** 
     * param The name of the ARC file.
     */
    public String getArcFilename() {
        return arcFilename;
    }
    
    /** 
     * @param The UUID of the ARC record.
     */
    public String getArcRecordUUID() {
        return arcRecordUUID;
    }
    
    /** 
     * @param The UUID of the Cumulus record.
     */
    public String getCumulusRecordUUID() {
        return cumulusRecordUUID;
    }
    
    /** 
     * @param Whether or not it has been found.
     */
    public boolean isFound() {
        return found;
    }
    
    /**
     * Set this record to being found.
     */
    public void setFound() {
        found = true;
    }
    
    @Override
    public String toString() {
        return "ArcFile: " + arcFilename + ", ArcRecordUUID: " + arcRecordUUID + ", CumulusRecordUUID: " 
                + cumulusRecordUUID + ", found:" + found;
    }
}
