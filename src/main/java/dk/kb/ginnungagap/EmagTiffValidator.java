package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.emagasin.EmagasinRetriever;
import dk.kb.ginnungagap.emagasin.TiffValidator;
import dk.kb.ginnungagap.utils.FileUtils;

/**
 * Class for validating tiff files in Emagasinet.
 * It extracts a given set of ARC-files, where each TIFF-record is validated using a script. 
 * 
 * This takes the following arguments:
 *  1. File with list of ARC files (one arc-filename per line).
 *  2. Script for extracting the files from Emagasinet.
 *  3. [OPTIONAL] Script for running the checkit_tiff
 *    - The default script is in the bin folder ("bin/run_checkit_tiff.sh")
 *  4. [OPTIONAL] Path to output directory
 *    - Default is at the current directory
 *  5. [OPTIONAL] Whether to remove the retrieved ARC-files and ARC-records afterwards
 *    - Default yes, remove them.
 *    - Give argument y/n
 *  6. [OPTIONAL] A configuration for checkit_tiff
 *    - The default configuration is in the conf folder ("conf/cit_tiff.cfg")
 *     
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagExtractor arc-list.txt emag-get-file.sh /path/to/output
 */
public class EmagTiffValidator {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagTiffValidator.class);

    /** The file with the list of arc files.*/
    private static File arcListFile = null;
    /** The file with the script for retrieving the ARC files from E-magasinet.*/
    private static File retrieveScriptFile = null;
    /** The script for running the checkit_tiff validation.*/
    private static File validationScriptFile = new File("bin/run_checkit_tiff.sh");
    /** The output directory.*/
    private static File outputDir = null;
    /** The path for the output directory.*/
    private static String outputDirPath = ".";
    /** Whether to remove the retrieved ARC-files afterwards.*/
    private static boolean removeAfterwards = true;
    /** The configuration for checkit.*/
    private static File checkitConf = new File("conf/cit_tiff.cfg");        

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments and accepts further 3 optional arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??
        if(args.length < 2) {
            failPrintErrorAndExit();
        } else {
            arcListFile = new File(args[0]);
            retrieveScriptFile = new File(args[1]);
        }
        if(args.length > 2) {
            validationScriptFile = new File(args[2]);
            if(args.length > 3) {
                outputDirPath = args[3];
                if(args.length > 4) {
                    removeAfterwards = args[4].startsWith("y") || args[4].startsWith("Y");
                    if(args.length > 5) {
                        checkitConf = new File(args[5]);
                    }
                }
            }
        }
        
        if(!arcListFile.isFile()) {
            System.err.println("The file with the list of ARC filenames does not exist at '"
                    + arcListFile.getAbsolutePath() + "'.");
            failPrintErrorAndExit();
        }
        if(!retrieveScriptFile.isFile()) {
            System.err.println("The retrieve script does not exist at '" + retrieveScriptFile.getAbsolutePath() + "'");
            failPrintErrorAndExit();
        }
        if(!validationScriptFile.isFile()) {
            System.err.println("The validation script does not exist at '" 
                    + validationScriptFile.getAbsolutePath() + "'");
            failPrintErrorAndExit();
        }
        if(!checkitConf.isFile()) {
            System.err.println("The checkit_tiff configuration does not exist at '" 
                    + checkitConf.getAbsolutePath() + "'");
            failPrintErrorAndExit();
        }
        
        outputDir = FileUtils.getDirectory(outputDirPath);

        EmagasinRetriever retriever = new EmagasinRetriever(retrieveScriptFile, outputDir);
        TiffValidator validator = new TiffValidator(outputDir, validationScriptFile, checkitConf, removeAfterwards);
        
        try (BufferedReader arcListReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                arcListFile), Charset.defaultCharset()));) {
            String arcFilename;
            while((arcFilename = getNextArcFilename(arcListReader)) != null) {
                File arcFile = retriever.extractArcFile(arcFilename);
                validator.validateTiffRecordsInArcFile(arcFile);
                if(removeAfterwards) {
                    Files.delete(arcFile.toPath());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read the list of arc files.", e);
        }
    }
    
    /**
     * Prints the arguments for this method and then exit.
     */
    protected static void failPrintErrorAndExit() {
        System.err.println("Missing arguments. Requires the following arguments:");
        System.err.println("  1. File with list of ARC files");
        System.err.println("  2. Script for extracting the files from Emagasinet.");
        System.err.println("  [OPTIONAL] 3. Script for running the checkit_tiff.");
        System.err.println("  [OPTIONAL] 4. Path to output directory (default is current directory)");
        System.err.println("  [OPTIONAL] 5. Where or not to remove the retrieved ARC files afterwards "
                + "(default is yes)");
        System.err.println("  [OPTIONAL] 6. Another configuration for checkit_tiff "
                + "(default is in the conf folder)");
        System.exit(-1);
    }
    
    /**
     * Extracts the next valid line from the list.
     * @param reader The reader.
     * @return The next valid line, or null when no more line can be read.
     * @throws IOException If it fails to read.
     */
    protected static String getNextArcFilename(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            if(!line.isEmpty() && !line.contains(" ")) {
                return line;
            } else {
                log.warn("Could not interpret line '" + line + "'");
            }
        }
        return null;
    }
}
