package dk.kb.ginnungagap.convert.prevalidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * The file must be in the format:
 * $ARC-filename; $Catalog; $Record name; $Checksum
 * e.g.
 * KBDOMS-20120222161256-00006-dia-prod-dom-01.kb.dk.arc;Audio;DFS_MC_GD-BP_0120_B.wav;dc53813fcce683e6c5e4416d2c0e1e88
 */
public class CatalogArchiveIndex {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CatalogArchiveIndex.class);

    /** The arc files from the index, matched with the expected records.*/
    private final Map<String, ArcFileContent> arcFiles;
    /** The name of the catalog.*/
    private final String catalogName;
    
    public CatalogArchiveIndex(File cumulusExtract, String catalogName) {
        this.catalogName = catalogName;
        arcFiles = new HashMap<String, ArcFileContent>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(cumulusExtract))) {
            String line;
            while((line = reader.readLine()) != null) {
                handleLine(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Issue occured when reading file '" + cumulusExtract.getAbsolutePath() 
                    + "'", e);
        }
    }
    
    /**
     * Handles a line from the Cumulus extract file.
     * The line will be ignored, if it does not have enough element, or if it does not belong to the right catalog.
     * @param line The line to handle.
     */
    protected void handleLine(String line) {
        String[] split = line.split(";");
        if(split.length < 4) {
            log.trace("Ignored reading line from cumulus extract: " + line);
            return;
        } if(split.length > 4) {
            log.trace("Only handle the first 4 entries in the line: " + line);
        }
        String arcFileName = split[0];
        String catalogName= split[1];
        String recordName = split[2];
        String checksum = split[3];
        
        // Only handle the specific catalog.
        if(!catalogName.equalsIgnoreCase(this.catalogName)) {
            return;
        }
        
        if(arcFiles.containsKey(arcFileName)) {
            arcFiles.get(arcFileName).addEntry(recordName, checksum);
        } else {
            arcFiles.put(arcFileName, new ArcFileContent(recordName, checksum));
        }
    }
    
    /**
     * Checks whether the current catalog has the given arc file.
     * @param arcFileName The name of the arc file.
     * @return Whether or not the catalog has record(s) in the given arc file.
     */
    public boolean hasArcFile(String arcFileName) {
        return arcFiles.containsKey(arcFileName);
    }
    
    /**
     * Handles the results of the prevalidation batchjob.
     * 
     * Will throw an exception, if the checksum does not match.
     * @param arcFileName The name of the file in the archive.
     * @param recordName The name of the record.
     * @param checksum The checksum of the arc-record in the archive.
     * @return True if the file belongs to this catalog and the checksums are identical.
     */
    public boolean handleArcRecordFromArchive(String arcFileName, String recordName, String checksum) {
        if(!hasArcFile(arcFileName)) {
            return false;
        }
        if(!arcFiles.get(arcFileName).hasEntry(recordName)) {
            return false;
        }
        if(checksum.equalsIgnoreCase(arcFiles.get(arcFileName).getChecksum(recordName))) {
            arcFiles.get(arcFileName).setFound(recordName);
            return true;
        } else {
            throw new IllegalStateException("Checksums does not match for record '" + recordName);
        }
    }
    
    /**
     * Retrieves the arc files, which has records which have not been found.
     * @return The list of arc files with records, which has not been found.
     */
    public List<String> getUnfoundArcFile() {
        List<String> res = new ArrayList<String>();
        for(Map.Entry<String, ArcFileContent> arcFile : arcFiles.entrySet()) {
            if(arcFile.getValue().hasUnfoundRecords()) {
                res.add(arcFile.getKey());
            }
        }
        return res;
    }
    
    /**
     * Class for handling each index content of the arc file.
     */
    private class ArcFileContent {
        /** Map between the records and the checksums.*/
        Map<String, String> recordsWithChecksum;
        
        Map<String, Boolean> recordsFound;
        
        protected ArcFileContent(String recordName, String checksum) {
            recordsWithChecksum = new HashMap<String, String>();
            recordsWithChecksum.put(recordName, checksum);
        }
        
        protected void addEntry(String recordName, String checksum) {
            recordsWithChecksum.put(recordName, checksum);
            recordsFound.put(recordName, false);
        }
        
        protected boolean hasEntry(String recordName) {
            return recordsWithChecksum.containsKey(recordName);
        }
        
        protected String getChecksum(String recordName) {
            return recordsWithChecksum.get(recordName);
        }
        
        protected void setFound(String recordName) {
            recordsFound.put(recordName, true);
        }
        
        protected boolean hasUnfoundRecords() {
            for(boolean found : recordsFound.values()) {
                if(!found) {
                    return true;
                }
            }
            return false;
        }
    }
}
