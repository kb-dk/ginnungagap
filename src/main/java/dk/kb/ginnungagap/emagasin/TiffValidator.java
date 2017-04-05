package dk.kb.ginnungagap.emagasin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Iterator;

import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.ginnungagap.utils.ScriptWrapper;
import dk.kb.ginnungagap.utils.StreamUtils;
import dk.kb.metadata.utils.GuidExtrationUtils;

/**
 * Class for validating TIFF files in ARC-record of ARC-files.
 * Extracts the TIFF files from the ARC-record. 
 * All non-TIFF ARC-records within an ARC-file are ignored.
 * 
 * Whether or not it is valid, is written to an output file within the given output-directory.
 * Also, it is optional whether or not the ARC-records is deleted after the validation.
 */
public class TiffValidator extends ScriptWrapper {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(TiffValidator.class);

    /** The mimetype / content type for a tiff ARC record.*/
    private static final String CONTENT_TYPE_TIFF = "image/tiff";
    
    /** The name of the output file for the validation results..*/
    private static final String OUTPUT_FILE_NAME = "tiff_validation.txt";
    
    /** The directory where the output should be placed.*/
    protected final File outputDir;
    /** The file where the output is written.*/
    protected final File outputFile;
    /** The configuration file for the validation.*/
    protected final File validationConf;
    /** Whether or not to delete the files afterwards.*/
    protected final boolean deleteAfterValidation;
    
    /**
     * Constructor.
     * @param outputDir The directory where the TIFF files should be stored after extraction from the ARC-files.
     * @param validationScript The script to use for the validation.
     * @param deleteAfterValidation Where or not to delete the TIFF files after validation.
     */
    public TiffValidator(File outputDir, File validationScript, File validationConf, boolean deleteAfterValidation) {
        super(validationScript);
        this.outputDir = outputDir;
        this.validationConf = validationConf;
        this.deleteAfterValidation = deleteAfterValidation;
        outputFile = FileUtils.getNewFile(outputDir, OUTPUT_FILE_NAME);
    }
    
    /**
     * Validates all the TIFF ARC-records within the given ARC file.
     * @param arcFile The ARC file to have it's TIFF ARC-records validated.
     * @throws IOException If it fails to extract or operate on the TIFF file.
     */
    public void validateTiffRecordsInArcFile(File arcFile) throws IOException {
        ArcReader arcReader = ArcReaderFactory.getReader(new FileInputStream(arcFile));
        Iterator<ArcRecordBase> arcIterator = arcReader.iterator();
        
        while(arcIterator.hasNext()) {
            ArcRecordBase arcRecord = arcIterator.next();
            System.err.println("ContentType: " + arcRecord.getContentTypeStr());
            if(arcRecord.getContentTypeStr().equals(CONTENT_TYPE_TIFF)) {
                String uid = GuidExtrationUtils.extractGuid(arcRecord.getUrlStr());
                File tiffFile = new File(outputDir, uid);
                StreamUtils.copyInputStreamToOutputStream(arcRecord.getPayloadContent(), 
                        new FileOutputStream(tiffFile));
                
                if(deleteAfterValidation) {
                    Files.delete(tiffFile.toPath());
                }
            }
        }
    }
    
    /**
     * @return The file with the results of the validation.
     */
    public File getOutputFile() {
        return outputFile;
    }
    
    /**
     * Validates a given TIFF file.
     * @param tiffFile The TIFF file to validate.
     */
    protected void validateTiffFile(File tiffFile) {
        try {
            callVoidScript(tiffFile.getAbsolutePath(), validationConf.getAbsolutePath());
            writeValidationSucces(tiffFile.getName());
        } catch (IllegalStateException e) {
            log.debug("Invalid tiff record", e);
            writeValidationFailure(tiffFile.getName(), e.getMessage());
        }
    }
    
    /**
     * Write about the successful validation of a given TIFF file.
     * @param filename The name of the TIFF file, which is valid.
     */
    protected void writeValidationSucces(String filename) {
        try (OutputStream out = new FileOutputStream(outputFile, true)) {
            out.write(new String("Success: " + filename).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Could not write a success about file '" + filename + "'", e);
        }
    }
    
    /**
     * Write about the failed validation of a given TIFF file.
     * @param filename The name of the TIFF file, which is invalid.
     */
    protected void writeValidationFailure(String filename, String cause) {
        try (OutputStream out = new FileOutputStream(outputFile, true)) {
            out.write(new String("Failed: " + filename + " -> " + cause).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Could not write a success about file '" + filename + "'", e);
        }
    }
}
