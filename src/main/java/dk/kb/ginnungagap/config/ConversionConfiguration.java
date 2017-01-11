package dk.kb.ginnungagap.config;

import java.io.File;

/**
 * Configuration for the conversion from E-magasinet.
 */
public class ConversionConfiguration {
    /** The temporary directory for data during the conversion.*/
    protected final File tempDir;
    /** The script for retrieving the ARC-files.*/
    protected final File scriptFile;

    /**
     * Constructor.
     * @param tempDir The temporary directory for data during the conversion.
     * @param scriptFile The file with the script for retrieving the ARC-files.
     */
    public ConversionConfiguration(File tempDir, File scriptFile) {
        this.tempDir = tempDir;
        this.scriptFile = scriptFile;
    }
    
    /** @return The temporary directory for data during the conversion.*/
    public File getTempDir() {
        return tempDir;
    }
    /** @return The script for retrieving the ARC-files.*/
    public File getScriptFile() {
        return scriptFile;
    }
}
