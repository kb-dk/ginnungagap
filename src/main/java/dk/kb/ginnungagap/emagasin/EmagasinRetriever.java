package dk.kb.ginnungagap.emagasin;

import java.io.File;

import org.apache.commons.lang.NotImplementedException;

import dk.kb.ginnungagap.utils.ScriptWrapper;

/**
 * Class for retrieving the ARC files from E-magasinet
 * It is just wrapper for a script, which make a commando-linje call for the NAS-API to retrieve the file.
 * This then just returns the given file.
 * 
 * It works on two levels. It can either retrieve a whole ARC file, or a given ARC record within an ARC file.
 */
public class EmagasinRetriever extends ScriptWrapper {

    /** The directory where the ARC files should be placed.*/
    protected final File outputDir;
    
    /**
     * Constructor.
     * @param script The shell script for retrieving ARC files.
     * @param outputDir The directory where the retrieved ARC file should be placed.
     */
    public EmagasinRetriever(File script, File outputDir) {
        super(script);
        this.outputDir = outputDir;
    }

    /**
     * Retrieves a given ARC file.
     * Will throw an exception, if it fails to retrieve the file.
     * @param filename The name of the ARC file to retrieve.
     * @return The retrieved ARC file.
     */
    public File extractArcFile(String filename) {
        File outputFile = new File(outputDir, filename);
        if(outputFile.exists()) {
            throw new IllegalStateException("The file '" + outputFile.getAbsolutePath() + "' already exists");
        }
        callVoidScript(filename, outputFile.getAbsolutePath());
        
        if(!outputFile.exists()) {
            throw new IllegalStateException("No file will be retrieved from E-magasinet.");
        }
        return outputFile;
    }
    
    /**
     * 
     * TODO: figure out, whether we can do this, without retrieving the whole ARC file.
     * @param filename
     * @param uuid
     * @return
     */
    public File extractArcRecord(String filename, String uuid) {
        // TODO this!!!
        throw new NotImplementedException("TODO");
    }
}
