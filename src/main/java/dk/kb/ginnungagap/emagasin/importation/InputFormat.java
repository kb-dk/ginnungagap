package dk.kb.ginnungagap.emagasin.importation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The input format for the importation.
 * 
 * Each import only imports for a specific Cumulus catalog.
 * This input format is basically a list of arc files with all the record UUIDs expected for that given arc file.
 * The problem is, that some of the records have different UUIDs for their ARC-record and their Cumulus-record.
 * Therefore we must have both UUIDs for each record.
 * 
 * The input format is expected to be in the following CSV format:
 * ARC-FILENAME; ARC-RECORD-UUID; CUMULUS-RECORD-UUID
 */
public class InputFormat {
    /** The name of the catalog for the current import..*/
    protected final String catalogID;
    /** The mapping between arc filenames and their list of records UUIDs.*/
    protected final Map<String, List<RecordUUIDs>> uuidsForArcFiles;
    
    /**
     * Constructor.
     * @param catalogID The ID of the catalog for the import.
     * @param inputFile The CSV file with the data to import.
     */
    public InputFormat(String catalogID, File inputFile) {
        this.catalogID = catalogID;
        this.uuidsForArcFiles = new HashMap<String, List<RecordUUIDs>>();
    }
    
    /**
     * Load the file.
     * @param inputFile The CSV file to load.
     */
    protected void loadFile(File inputFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                String arcFilename = split[0];
                String arcRecordUUID = split[1];
                String cumulusRecordUUID = split[2];
                
                List<RecordUUIDs> listForArcFile;
                if(uuidsForArcFiles.containsKey(arcFilename)) {
                    listForArcFile = uuidsForArcFiles.get(arcFilename);
                } else {
                    listForArcFile = new ArrayList<RecordUUIDs>();
                }
                listForArcFile.add(new RecordUUIDs(catalogID, arcFilename, arcRecordUUID, cumulusRecordUUID));
                uuidsForArcFiles.put(arcFilename, listForArcFile);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file '" + inputFile.getAbsolutePath() + "'", e);
        }
    }
    
    /**
     * @return The ID of the Cumulus catalog.
     */
    public String getCatalogID() {
        return catalogID;
    }
    
    /**
     * @return The collection of arc filenames.
     */
    public Collection<String> getArcFilenames() {
        return uuidsForArcFiles.keySet();
    }
    
    /**
     * 
     * @param arcFilename
     * @param arcRecordUUID
     * @return
     */
    public String getCumulusRecordUUIDForArcRecordUUID(String arcFilename, String arcRecordUUID) {
        for(RecordUUIDs r : uuidsForArcFiles.get(arcFilename)) {
            if(r.arcRecordUUID.equalsIgnoreCase(arcRecordUUID)) {
                return r.cumulusRecordUUID;
            }
        }
        return null;
    }
}
