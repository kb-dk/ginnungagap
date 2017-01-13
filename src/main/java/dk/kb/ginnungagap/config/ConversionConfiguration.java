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
    /** The base directory for the pre-ingest area.*/
    protected final File preIngestBaseDir;

    /**
     * Constructor.
     * @param tempDir The temporary directory for data during the conversion.
     * @param scriptFile The file with the script for retrieving the ARC-files.
     */
    public ConversionConfiguration(File tempDir, File scriptFile, File preIngestBaseDir) {
        this.tempDir = tempDir;
        this.scriptFile = scriptFile;
        this.preIngestBaseDir = preIngestBaseDir;
    }
    
    /** @return The temporary directory for data during the conversion.*/
    public File getTempDir() {
        return tempDir;
    }
    /** @return The script for retrieving the ARC-files.*/
    public File getScriptFile() {
        return scriptFile;
    }
    /** @return The base directory for the pre-ingest area.*/
    public File getPreIngestBaseDir() {
        return preIngestBaseDir;
    }
}
