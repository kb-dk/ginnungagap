package dk.kb.ginnungagap.convert.prevalidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.kb.ginnungagap.utils.FileUtils;

/**
 * Prevalidator.
 * 
 * It compares the outputs of Cumulus and Emagasinet, and separates each record into one of the following cases:
 *  - Valid: The record exists both in Cumulus and Emagasinet, and it has the same checksum and size. 
 *  - Invalid: The record exists both in Cumulus and Emagasinet, but it has different checksum or size.
 *  - Not Found In Emagasinet: The record only exists in Cumulus, or if it is an invalid line in the Cumulus file.
 *  - Not Found In Cumulus: The record only exists in Emagasinet, or if it is an invalid line in the Emagasin file.
 *  
 * All of the records from Emagasinet is loaded into memory, and then each record from Cumulus is tested.
 * Finally we take all the records from Emagasinet which has not been matched with a record from Cumulus,
 * and they are put into the Not Found In Cumulus results.  
 */
public class ImportPrevalidator {
    /** The prefix for the file with the valid results.*/
    protected static final String VALID_ENTRY_FILE_PREFIX = "valid";
    /** The prefix for the file with the invalid results.*/
    protected static final String INVALID_ENTRY_FILE_PREFIX = "invalid";
    /** The prefix for the file with the records not found in Cumulus.*/
    protected static final String NOT_FOUND_CUMULUS_FILE_PREFIX = "not_found_cumulus";
    /** The prefix for the file with the records not found in Emagasinet.*/
    protected static final String NOT_FOUND_EMAGASIN_FILE_PREFIX = "not_found_emagasin";
    
    /** The suffix for the result files.*/
    protected static final String SUFFIX_FILES = ".txt";
    
    /** The new line string.*/
    protected static final String NEW_LINE = "\n";
    
    /** The file for the valid results.*/
    protected final File validFile;
    /** The file with the invalid results.*/
    protected final File invalidFile;
    /** The file with the records, which are not in Cumulus.*/
    protected final File notFoundInCumulusFile;
    /** The file with the records, which are not in Emagasinet.*/
    protected final File notFoundInEmagasinFile;
    
    /** The records in Emagasinet. 
     * Map between the name of the ARC files and the list of records within the ARC files.*/
    protected Map<String, List<EmagasinExtractRecord>> emagasinRecords = new HashMap<String, 
            List<EmagasinExtractRecord>>();
    
    /**
     * Constructor.
     * @param outputDir The directory, where the output files should be made.
     */
    public ImportPrevalidator(File outputDir) {
        this.validFile = FileUtils.getNewFile(outputDir, VALID_ENTRY_FILE_PREFIX + SUFFIX_FILES);
        this.notFoundInEmagasinFile = FileUtils.getNewFile(outputDir, NOT_FOUND_EMAGASIN_FILE_PREFIX + SUFFIX_FILES);
        this.notFoundInCumulusFile = FileUtils.getNewFile(outputDir, NOT_FOUND_CUMULUS_FILE_PREFIX + SUFFIX_FILES);
        this.invalidFile = FileUtils.getNewFile(outputDir, INVALID_ENTRY_FILE_PREFIX + SUFFIX_FILES);        
    }
    
    /**
     * The main method.
     * Compares the emagasin records and the cumulus records.
     * @param emagasinRecordReader The reader of the file with the EmagasinRecords.
     * @param cumulusRecordReader The reader of the flie with the CumulusRecords.
     */
    public void compare(BufferedReader emagasinRecordReader, BufferedReader cumulusRecordReader) {
        loadEmagasinRecords(emagasinRecordReader);
        try {
            CumulusExtractRecord cumulusRecord;
            while((cumulusRecord = getNextCumulusExtractRecord(cumulusRecordReader)) != null) {
                matchCumulusRecord(cumulusRecord);
            }
            handleNotFoundEmagasinRecords();
        } catch (IOException e) {
            throw new IllegalStateException("", e);
        }
    }

    /**
     * Loads all the EmagasinRecords into memory.
     * @param emagasinRecordReader The reader of the file with the EmagasinRecords.
     */
    protected void loadEmagasinRecords(BufferedReader emagasinRecordReader) {
        EmagasinExtractRecord eRecord;
        try {
            while((eRecord = getNextEmagasinExtractRecord(emagasinRecordReader)) != null) {
                List<EmagasinExtractRecord> list;
                if(emagasinRecords.containsKey(eRecord.arcFilename)) {
                    list = emagasinRecords.get(eRecord.arcFilename);
                } else {
                    list = new ArrayList<EmagasinExtractRecord>();
                }
                list.add(eRecord);
                emagasinRecords.put(eRecord.arcFilename, list);
            }            
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the emagasin records.", e);
        }
    }
    
    /**
     * Matches a given Cumulus record with the Emagasin records.
     * First it sees if any Emagasin record with the same ARC file and UUID exists.
     * If not, then the Cumulus record does not exist in Emagasinet.
     * Otherwise it compares the size and checksums of these values.
     * If these differ, then the Cumulus record and Emagasin record are invalid.
     * Otherwise it is valid.
     * @param cumulusRecord The given cumulus record to match.
     */
    protected void matchCumulusRecord(CumulusExtractRecord cumulusRecord) {
        EmagasinExtractRecord eRecord = findEmagasinRecord(cumulusRecord.getArcFilename(), cumulusRecord.getUuid());
        if(eRecord == null) {
            recordNotInEmagasinResult(cumulusRecord.getArcFilename() + " -> " + cumulusRecord.getUuid() + " -> " 
                    + cumulusRecord.getCatalogName());
            return;
        }
        eRecord.setFound();
        if(!eRecord.getChecksum().equals(cumulusRecord.getChecksumOriginalMaster()) ||
                !eRecord.getChecksum().equals(cumulusRecord.getChecksumArchiveMD5())) {
            recordInvalidResult("INVALID CHECKSUM: " + eRecord.getChecksum() + " != " 
                    + cumulusRecord.getChecksumOriginalMaster() + " || " + cumulusRecord.getChecksumOriginalMaster() 
                    + " ( " + cumulusRecord.getArcFilename() + " -> " + cumulusRecord.getUuid() + " -> " 
                    + cumulusRecord.getCatalogName() + " )");
            return;
        }
        if(eRecord.getSize().longValue() != cumulusRecord.getSize().longValue()) {
            recordInvalidResult("INVALID SIZE: " + eRecord.getSize() + " != " + cumulusRecord.getSize() + " ( "
                    + cumulusRecord.getArcFilename() + " -> " + cumulusRecord.getUuid() + " -> " 
                            + cumulusRecord.getCatalogName() + " )");
            return;
        }
        recordValidResult(cumulusRecord.getArcFilename() + " -> " + cumulusRecord.getUuid() + " -> "
                + cumulusRecord.getCatalogName());
    }
    
    /**
     * Goes through all the Emagasin records, find the ones which have not been found, and set them to Not In Cumulus.
     */
    protected void handleNotFoundEmagasinRecords() {
        for(List<EmagasinExtractRecord> list : emagasinRecords.values()) {
            for(EmagasinExtractRecord eRecord : list) {
                if(!eRecord.hasBeenFound()) {
                    recordNotInCumulusResult(eRecord.getArcFilename() + " -> " + eRecord.getUuid());
                }
            }
        }
    }

    /**
     * Find a Emagasin record with a given ARC filename and UUID.
     * Returns null, if no record is found.
     * @param filename The name of the ARC file.
     * @param uuid The UUID of the record.
     * @return The Emagasin record, or null if no record was found.
     */
    protected EmagasinExtractRecord findEmagasinRecord(String filename, String uuid) {
        List<EmagasinExtractRecord> emagasinList = emagasinRecords.get(filename);
        if(emagasinList == null) {
            return null;
        }
        for(EmagasinExtractRecord eRecord : emagasinList) {
            if(eRecord.getUuid().equals(uuid)) {
                return eRecord;
            }
        }
        return null;
    }

    /**
     * Extracts the next valid Emagasin record from the Emagasin file.
     * If a non-valid line is encountered, then it is put into the Not In Cumulus results.
     * @param emagasinRecordReader The reader with lines from the Emagasin file.
     * @return The next Emagasin record, or null when there are no more lines in the Emagasin file.
     * @throws IOException If the reader fails to read the next line.
     */
    protected EmagasinExtractRecord getNextEmagasinExtractRecord(BufferedReader emagasinRecordReader) 
            throws IOException {
        EmagasinExtractRecord res;
        String line;
        while((line = emagasinRecordReader.readLine()) != null) {
            res = EmagasinExtractRecord.getArchiveRecord(line);
            if(res != null) {
                return res;
            } else {
                recordNotInCumulusResult(line);
            }
        }
        return null;
    }
    
    /**
     * Extracts the next valid Cumulus record from the Cumulus file.
     * If a non-valid line is encountered, then it is put into the Not In Emagasin results.
     * @param emagasinRecordReader The reader with lines from the Cumulus file.
     * @return The next Cumulus record, or null when there are no more lines in the Cumulus file.
     * @throws IOException If the reader fails to read the next line.
     */
    protected CumulusExtractRecord getNextCumulusExtractRecord(BufferedReader cumulusRecordReader) 
            throws IOException {
        CumulusExtractRecord res;
        String line;
        while((line = cumulusRecordReader.readLine()) != null) {
            res = CumulusExtractRecord.getArchiveRecord(line);
            if(res != null) {
                return res;
            } else {
                recordNotInEmagasinResult(line);
            }
        }
        return null;
    }
    
    /**
     * Writes a result to the valid result file.
     * @param line The line with the information about the valid records to be written to the Valid file.
     */
    protected void recordValidResult(String line) {
        try (FileOutputStream fos = new FileOutputStream(validFile, true)) {
            fos.write(line.getBytes());
            fos.write(NEW_LINE.getBytes());
            fos.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write valid results", e);
        }
    }
    
    /**
     * Writes a result to the invalid result file.
     * @param line The line with the information about the invalid records to be written to the Invalid file.
     */
    protected void recordInvalidResult(String line) {
        try (FileOutputStream fos = new FileOutputStream(invalidFile, true)) {
            fos.write(line.getBytes());
            fos.write(NEW_LINE.getBytes());
            fos.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write invalid results", e);
        }
    }

    /**
     * Writes a result to the Not In Emagasinet file.
     * @param line The line with the information about the record which is not in Emagasinet to be written 
     * to the Not In Emagasinet file.
     */
    protected void recordNotInEmagasinResult(String line) {
        try (FileOutputStream fos = new FileOutputStream(notFoundInEmagasinFile, true)) {
            fos.write(line.getBytes());
            fos.write(NEW_LINE.getBytes());
            fos.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write records not found in Emagasinet", e);
        }
    }

    /**
     * Writes a result to the Not In Cumulus file.
     * @param line The line with the information about the record which is not in Cumulus  to be written 
     * to the Not In Cumulus file.
     */
    protected void recordNotInCumulusResult(String line) {
        try (FileOutputStream fos = new FileOutputStream(notFoundInCumulusFile, true)) {
            fos.write(line.getBytes());
            fos.write(NEW_LINE.getBytes());
            fos.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write records not found in Cumulus", e);
        }
    }
}
