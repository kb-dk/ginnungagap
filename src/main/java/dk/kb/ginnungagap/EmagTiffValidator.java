package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.emagasin.EmagasinRetriever;
import dk.kb.ginnungagap.emagasin.TiffValidator;
import dk.kb.ginnungagap.utils.FileUtils;

/**
 * Class for validating tiff files in Emagasinet.
 * It extracts a given set of ARC-files, where each record of a TIFF is validated using a script. 
 * The whole thing is placed in 
 * 
 * This takes the following arguments:
 *  1. File with list of ARC files (one arc-filename per line).
 *  2. Script for extracting the files from Emagasinet.
 *  3. Script for running the FITS
 *  4. [OPTIONAL] Path to output directory
 *    - Default is at the current directory
 *  5. [OPTIONAL] Whether to remove the retrieved ARC-files and ARC-records afterwared
 *    - Default yes, remove them.
 *    - Give argument y/n
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagExtractor arc-list.txt emag-get-file.sh /path/to/output
 */
public class EmagTiffValidator {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagTiffValidator.class);

    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        File arcListFile = null;
        File retrieveScriptFile = null;
        File validationScriptFile = null;
        File outputDir = null;
        String outputDirPath = null;
        boolean removeAfterwards = true;
        if(args.length < 3) {
            System.err.println("Missing arguments. Requires the following arguments:");
            System.err.println("  1. File with list of ARC files");
            System.err.println("  2. Script for extracting the files from Emagasinet.");
            System.err.println("  3. Script for running the FITS.");
            System.err.println("  [OPTIONAL] 4. Path to output directory (default is current directory)");
            System.err.println("  [OPTIONAL] 5. Where or not to remove the retrieved ARC files afterwards "
                    + "(default is yes)");
            System.exit(-1);
        } else {
            arcListFile = new File(args[0]);
            retrieveScriptFile = new File(args[1]);
            validationScriptFile = new File(args[2]);
            if(args.length > 3) {
                outputDirPath = args[3];
            } else {
                outputDirPath = ".";
            }
        }
        
        if(!arcListFile.isFile()) {
            System.err.println("The file with the list of ARC filenames does not exist at '"
                    + arcListFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        if(!retrieveScriptFile.isFile()) {
            System.err.println("The retrieve script does not exist at '" + retrieveScriptFile.getAbsolutePath() + "'");
            System.exit(-1);
        }
        if(!validationScriptFile.isFile()) {
            System.err.println("The validation script does not exist at '" 
                    + validationScriptFile.getAbsolutePath() + "'");
            System.exit(-1);
        }
        // TODO: make this into an optional argument
        File checkitConf = new File("conf/cit_tiff.cfg");
        
        outputDir = FileUtils.getDirectory(outputDirPath);

        EmagasinRetriever retriever = new EmagasinRetriever(retrieveScriptFile, outputDir);
        TiffValidator validator = new TiffValidator(outputDir, validationScriptFile, checkitConf, removeAfterwards);
        
        try (BufferedReader arcListReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                arcListFile)));) {
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
     * Extracts the next valid line from the list.
     * TODO: perhaps make more tests, that the line does not contain invalid characters, etc.
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
