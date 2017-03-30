package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.emagasin.EmagasinRetriever;
import dk.kb.ginnungagap.utils.FileUtils;

/**
 * Class for extracting ARC files and ARC records from Emagasinet.
 * 
 * This takes the following arguments:
 *  1. File with list of ARC files.
 *  2. Script for extracting the files from Emagasinet.
 *  3. [OPTIONAL] Path to output directory
 *    - Default is at the current directory
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagExtractor arc-list.txt emag-get-file.sh /path/to/output
 */
public class EmagExtractor {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagExtractor.class);

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        File arcListFile = null;
        File scriptFile = null;
        File outputDir = null;
        String outputDirPath = null;
        if(args.length < 2) {
            System.err.println("Missing arguments. Requires the following arguments:");
            System.err.println("  1. File with list of ARC files");
            System.err.println("  2. Script for extracting the files from Emagasinet.");
            System.err.println("  [OPTIONAL] 3. Path to output directory (default is current directory)");
            System.exit(-1);
        } else {
            arcListFile = new File(args[0]);
            scriptFile = new File(args[1]);
            if(args.length > 2) {
                outputDirPath = args[2];
            } else {
                outputDirPath = ".";
            }
        }
        
        if(!arcListFile.isFile()) {
            System.err.println("The file with the list of ARC filenames does not exist at '"
                    + arcListFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        if(!scriptFile.isFile()) {
            System.err.println("The script does not exist at '" + scriptFile.getAbsolutePath() + "'");
            System.exit(-1);
        }
        
        outputDir = FileUtils.getDirectory(outputDirPath);
        
        EmagasinRetriever retriever = new EmagasinRetriever(scriptFile, outputDir);
        
        try (BufferedReader arcListReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                arcListFile)));) {
            String arcFilename;
            while((arcFilename = getNextArcFilename(arcListReader)) != null) {
                retriever.extractArcFile(arcFilename);
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
