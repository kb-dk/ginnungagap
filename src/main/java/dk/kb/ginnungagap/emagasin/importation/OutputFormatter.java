package dk.kb.ginnungagap.emagasin.importation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.bitrepository.common.utils.FileUtils;

/**
 * Class for dealing with the outputs of the importation.
 * The successes imported records will be written to the successfile, 
 * and the failed imported records will be written to the failurefile along with the reason for the failure.
 * 
 * Both files will be in CSV format;
 * Successfile with following 3 coloumns:
 * ARC-file; ARC-record-uuid; Cumulus-record-UUID
 * 
 * Failurefile with the following 4 coloumns:
 * ARC-file; ARC-record-uuid; Cumulus-record-UUID; Error
 */
public class OutputFormatter {
    /** The name of the success output file.*/
    protected static final String FILENAME_SUCCES_FILE = "import_succes.txt";
    /** The name of the failure output file.*/
    protected static final String FILENAME_FAILURE_FILE = "import_failure.txt";
    
    /** The first line and CSV format of the success file.*/
    protected static final String COLOUMN_NAMES_SUCCES_FILE = 
            "Catalog;ARC-file; ARC-record-uuid; Cumulus-record-UUID;\n";
    /** The first line and CSV format of the failure file.*/
    protected static final String COLOUMN_NAMES_FAILURE_FILE = 
            "Catalog;ARC-file; ARC-record-uuid; Cumulus-record-UUID; Error\n";
    
    /** The file containing the successful import results.*/
    protected File succesFile;
    /** The file containing the failed import results.*/
    protected File failureFile;
    
    /**
     * Constructor.
     * @param outputDir The directory for the output files.
     */
    public OutputFormatter(File outputDir) {
        this.succesFile = new File(outputDir, FILENAME_SUCCES_FILE);
        this.failureFile = new File(outputDir, FILENAME_FAILURE_FILE);
        try {
            initializeFiles();
        } catch (IOException e) {
            throw new IllegalStateException("Could not instantiate the output files.");
        }
    }
    
    /**
     * Initializes the two output files.
     * @throws IOException
     */
    protected void initializeFiles() throws IOException {
        if(succesFile.exists()) {
            FileUtils.deprecateFile(succesFile);
        }
        try (OutputStream os = new FileOutputStream(succesFile)) {
            os.write(COLOUMN_NAMES_SUCCES_FILE.getBytes(StandardCharsets.UTF_8));
        }
        if(failureFile.exists()) {
            FileUtils.deprecateFile(failureFile);
        }        
        try (OutputStream os = new FileOutputStream(failureFile)) {
            os.write(COLOUMN_NAMES_FAILURE_FILE.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * Write a successful importation of a record.
     * @param recordUUIDs The record which has been successfully imported into Cumulus.
     */
    public void writeSucces(RecordUUIDs recordUUIDs) {
        try (OutputStream os = new FileOutputStream(succesFile, true)) {
            StringBuffer sb = new StringBuffer();
            sb.append(recordUUIDs.getCatalogID() + ";");
            sb.append(recordUUIDs.getArcFilename() + ";");
            sb.append(recordUUIDs.getArcRecordUUID() + ";");
            sb.append(recordUUIDs.getCumulusRecordUUID() + "\n");
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Issue occured while writing succes for the record '"
                    + recordUUIDs + "'", e);
        }
    }
    
    /**
     * Write a failed importation of a record.
     * @param recordUUIDs The record which has failed the importation into Cumulus.
     * @param cause The cause of that failure.
     */
    public void writeFailure(RecordUUIDs recordUUIDs, String cause) {
        try (OutputStream os = new FileOutputStream(failureFile, true)) {
            StringBuffer sb = new StringBuffer();
            sb.append(recordUUIDs.getCatalogID() + ";");
            sb.append(recordUUIDs.getArcFilename() + ";");
            sb.append(recordUUIDs.getArcRecordUUID() + ";");
            sb.append(recordUUIDs.getCumulusRecordUUID() + ";");
            sb.append(cause + "\n");
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Issue occured while writing failurefor the record '"
                    + recordUUIDs + "'", e);
        }
    }
    
    /**
     * Writes the failure when encountering a non expected ARC record.
     * @param arcFilename The name of the ARC file.
     * @param arcRecordID The ID of the arc record.
     * @param cause The cause of the failure.
     */
    public void writeFailure(String arcFilename, String arcRecordID, String cause) {
        try (OutputStream os = new FileOutputStream(failureFile, true)) {
            StringBuffer sb = new StringBuffer();
            sb.append("??;");
            sb.append(arcFilename + ";");
            sb.append(arcRecordID + ";");
            sb.append(";");
            sb.append(cause + "\n");
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Issue occured while writing failure for the unexpected record '"
                    + arcRecordID + "' in arc file '" + arcFilename + "'", e);
        }
    }
}

