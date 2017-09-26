package dk.kb.ginnungagap.emagasin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.exception.RunScriptException;
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
    private static final String OUTPUT_FILE_NAME = "tiff_validation.csv";

    /** The regular expression for extracting the error tags found by the validation script.*/
    private static final String VALIDATION_ERROR_TAG_REGEX = "tag [0-9]++[^\n]*";
    /** The regular expression for extracting the number of errors found by the validation script.*/
    private static final String VALIDATION_ERRORS_REGEX = "Found [0-9]++ errors";
    
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
     * @param validationConf The configuration file for the validation.
     * @param deleteAfterValidation Where or not to delete the TIFF files after validation.
     */
    public TiffValidator(File outputDir, File validationScript, File validationConf, boolean deleteAfterValidation) {
        super(validationScript);
        this.outputDir = outputDir;
        this.validationConf = validationConf;
        this.deleteAfterValidation = deleteAfterValidation;
        outputFile = FileUtils.getNewFile(outputDir, OUTPUT_FILE_NAME);
        initOutputFile();
    }
    
    /**
     * Validates all the TIFF ARC-records within the given ARC file.
     * @param arcFile The ARC file to have it's TIFF ARC-records validated.
     * @throws IOException If it fails to extract or operate on the TIFF file.
     */
    public void validateTiffRecordsInArcFile(File arcFile) throws IOException {
        ARCReader arcReader = ARCReaderFactory.get(arcFile);
        
        for(ArchiveRecord arcRecord : arcReader) {
            String uid = GuidExtrationUtils.extractGuid(arcRecord.getHeader().getUrl());
            try {
                validateArcRecordIfTiff(arcRecord, uid);
                writeCsvToOutput(Arrays.asList("Success", uid));
            } catch (RunScriptException e) {
                log.debug("Invalid tiff record", e);
                writeValidationFailure(uid, e);                
            }
        }
    }
    
    /**
     * Validate the ARC record, if it is a TIFF file.
     * @param arcRecord The arc record.
     * @param uid The UUID of the record.
     * @throws IOException If it fails to extract the ARC record of
     * @throws RunScriptException If a failure occurs while running the script. 
     */
    public void validateArcRecordIfTiff(ArchiveRecord arcRecord, String uid) throws IOException, RunScriptException {
        String mimetype = arcRecord.getHeader().getMimetype();
        if(isTiffMimetype(mimetype)) {
            log.info("Validating Tiff record '" + uid + "'");
            File tiffFile = new File(outputDir, uid);
            StreamUtils.copyInputStreamToOutputStream(arcRecord, 
                    new FileOutputStream(tiffFile));
            
            try {
                validateTiffFile(tiffFile);
            } finally {
                if(deleteAfterValidation) {
                    Files.delete(tiffFile.toPath());
                }
            }
        } else {
            log.debug("No validation of mimetype '" + mimetype + "' for record '" + uid + "'");
        }
    }
    
    /**
     * @param mimetype The mimetype.
     * @return Whether the mimetype is a TIFF mimetype.
     */
    public boolean isTiffMimetype(String mimetype) {
        return mimetype.equals(CONTENT_TYPE_TIFF);
    }
    
    /**
     * Validate the TIFF file.
     * Just calls the script with the right paths.
     * @param tiffFile The TIFF file to validate.
     * @throws RunScriptException If the validation fails. 
     */
    public void validateTiffFile(File tiffFile) throws RunScriptException {
        callVoidScript(tiffFile.getAbsolutePath(), validationConf.getAbsolutePath());
    }
    
    /**
     * @return The file with the results of the validation.
     */
    public File getOutputFile() {
        return outputFile;
    }
        
    /**
     * Write about the failed validation of a given TIFF file.
     * It will add the status, the filename, the number of errors reported in the exception,
     * and all the different lines with tag errors (thus tags which occur more than once, will on be 
     * added once).
     * @param filename The name of the TIFF file, which is invalid.
     * @param e The error from the checkit_tiff. Should contain number of errors and which tags failed.
     */
    protected void writeValidationFailure(String filename, RunScriptException e) {
        Pattern errorsPattern = Pattern.compile(VALIDATION_ERRORS_REGEX);
        Matcher errorsMatch = errorsPattern.matcher(e.getMessage());
        Pattern errorTagPattern = Pattern.compile(VALIDATION_ERROR_TAG_REGEX);
        Matcher errorTagMatch = errorTagPattern.matcher(e.getMessage());
        List<String> errors = new ArrayList<String>();
        errors.add("Failure");
        errors.add(filename);
        if(errorsMatch.find()) {
            errors.add(errorsMatch.group());
        }
        while(errorTagMatch.find()) {
            String error = errorTagMatch.group();
            if(!errors.contains(error)) {
                errors.add(error);
            }
        }
                
        writeCsvToOutput(errors);
    }
    
    /**
     * Write the initial line of the output file, where the columns are described.
     */
    protected void initOutputFile() {
        writeCsvToOutput(Arrays.asList("Success/Failure", "UUID", "# errors", "Tags"));
    }
    
    /**
     * Write a line in the output CSV file.
     * @param strings The value for each column in the CSV row.
     */
    protected void writeCsvToOutput(Collection<String> strings) {
        try (OutputStream out = new FileOutputStream(outputFile, true)) {
            for(String s : strings) {
                out.write(s.getBytes(StandardCharsets.UTF_8));
                out.write(";".getBytes(StandardCharsets.UTF_8));
            }
            out.write("\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Could not write to the output file", e);
        }        
    }
}
