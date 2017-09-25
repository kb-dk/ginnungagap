package dk.kb.ginnungagap.emagasin.prevalidation;

import java.util.HashMap;
import java.util.Map;

import dk.kb.metadata.utils.GuidExtrationUtils;

/**
 * Class for extracted entries for Cumulus.
 * It contains the name of the arc file, the uuid, the size and the checksum of the record.
 * The line format is not fixed, so each field must be prefixed with its type, e.g.
 * ** ARCHIVE_FILENAME:$ARCFILE ; Catalog Name: $CATALOG_NAME ; CHECKSUM_ORIGINAL_MASTER: $CHECKSUM ; GUID: $GUID ; ARCHIVE_MD5: $ARCHIVE_MD5; File Data Size: $SIZE
 * E.g. 
 * 
 * ** Catalog Name:Letters OM;CHECKSUM_ORIGINAL_MASTER:d5565ee032806d722e4a1771d6c24258;ARCHIVE_FILENAME:KBDOMS-20140918154510-03651-dia-prod-dom-02.kb.dk.arc;GUID:Uid:dk:kb:doms:2007-01/8f4102b0-abf0-11e3-aab4-0016357f605f;ARCHIVE_MD5:d5565ee032806d722e4a1771d6c24258;File Data Size:22915330
 * ** ARCHIVE_FILENAME:KBDOMS-20120209144621-00000-dia-prod-dom-01.kb.dk.arc;Catalog Name:Audio;CHECKSUM_ORIGINAL_MASTER:77ad7deafde564b4eb20ac858407ec3d;GUID:Uid:dk:kb:doms:2007-01/4bde6970-523f-11e1-9888-0017a4f603c1;ARCHIVE_MD5:77ad7deafde564b4eb20ac858407ec3d;File Data Size:1680088238
 */
class CumulusExtractRecord {
    /** Name of the arc file.*/
    protected final String arcFilename;
    /** The name of the catalog.*/
    protected final String catalogName;
    /** UUID of the record.*/
    protected final String uuid;
    /** The size of the record.*/
    protected final Long size;
    /** The Original Master Checksum of the record.*/
    protected final String checksumOriginal;
    /** The ArchiveMD5 checksum of the record.*/
    protected final String checksumArchiveMD5;
    /** The complete line.*/
    protected final String line;
    
    /**
     * Constructor.
     * @param arcFilename Name of the arc file.
     * @param catalogName The name of the catalog.
     * @param checksumOriginalMaster The value of the Original Master Checksum field.
     * @param uuid UUID of the record.
     * @param checksumArchiveMD5 The value of the Archive_MD5 field (which is a checksum).
     * @param size The size of the record.
     * @param line The complete line.
     */
    private CumulusExtractRecord(String arcFilename, String catalogName, String checksumOriginalMaster, String uuid, 
            String checksumArchiveMD5, Long size, String line) {
        this.arcFilename = arcFilename;
        this.catalogName = catalogName;
        this.checksumOriginal = checksumOriginalMaster;
        this.uuid = GuidExtrationUtils.extractGuid(uuid);
        this.checksumArchiveMD5 = checksumArchiveMD5;
        this.size = size;
        this.line = line;
    }
    
    /**
     * @return Name of the arc file.
     */
    public String getArcFilename() {
        return arcFilename;
    }
    
    /**
     * @return The name of the catalog.
     */
    public String getCatalogName() {
        return catalogName;
    }
    
    /**
     * @return The checksum of the record.
     */
    public String getChecksumOriginalMaster() {
        return checksumOriginal;
    }
    
    /**
     * @return The checksum of the record.
     */
    public String getChecksumArchiveMD5() {
        return checksumArchiveMD5;
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
     * @return The complete line.
     */
    public String getLine() {
        return line;
    }

    /** The name of the ARCHIVE_FILENAME field in Cumulus and the converted Cumulus extract file. */
    protected static final String SEGMENT_PREFIX_ARCHIVE_FILENAME = "ARCHIVE_FILENAME";
    /** The name of the Catalog Name field in Cumulus and the converted Cumulus extract file. */
    protected static final String SEGMENT_PREFIX_CATALOG_NAME = "Catalog Name"; 
    /** The name of the CHECKSUM_ORIGINAL_MASTER field in Cumulus and the converted Cumulus extract file. */
    protected static final String SEGMENT_PREFIX_CHECKSUM_ORIGINAL_MASTER = "CHECKSUM_ORIGINAL_MASTER";
    /** The name of the GUID field in Cumulus and the converted Cumulus extract file. */
    protected static final String SEGMENT_PREFIX_GUID = "GUID"; 
    /** The name of the ARCHIVE_MD5 field in Cumulus and the converted Cumulus extract file. */
    protected static final String SEGMENT_PREFIX_ARCHIVE_MD5 = "ARCHIVE_MD5"; 
    /** The name of the File Data Size field in Cumulus and the converted Cumulus extract file. */
    protected static final String SEGMENT_PREFIX_FILE_DATA_SIZE = "File Data Size"; 

    /**
     * Creates the EmagasinExtractRecord from a line in the batchjob output.
     * @param line The line from the batchjob output.
     * @return The EmagasinExtractRecord for the line, or null if the line is invalid.
     */
    protected static CumulusExtractRecord getArchiveRecord(String line) {
        String[] split = line.split(";");
        if(split.length < 6) {
            return null;
        }
        Map<String, String> segments = new HashMap<String, String>();
        for(String s : split) {
            int segmentSplitIndex = s.indexOf(":");
            if(segmentSplitIndex < 0) {
                return null;
            }
            segments.put(s.substring(0, segmentSplitIndex), s.substring(segmentSplitIndex+1));
        }
        return new CumulusExtractRecord(segments.get(SEGMENT_PREFIX_ARCHIVE_FILENAME), 
                segments.get(SEGMENT_PREFIX_CATALOG_NAME), 
                segments.get(SEGMENT_PREFIX_CHECKSUM_ORIGINAL_MASTER), 
                segments.get(SEGMENT_PREFIX_GUID), 
                segments.get(SEGMENT_PREFIX_ARCHIVE_MD5), 
                Long.valueOf(segments.get(SEGMENT_PREFIX_FILE_DATA_SIZE)), 
                line);
    }
}
