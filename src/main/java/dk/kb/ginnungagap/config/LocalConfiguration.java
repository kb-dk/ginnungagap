package dk.kb.ginnungagap.config;

import java.io.File;

/**
 * The configuration for local archival properties, including whether it is running in test mode.
 */
public class LocalConfiguration {
    /** The directory for the local archive.*/
    protected final File localArchiveDir;
    /** The directory for the local output.*/
    protected final File localOutputDir;
    /** Whether or not it is running in test mode.*/
    protected final boolean isTest;
    
    /**
     * Constructor.
     * @param localArchiveDir The directory for the local archive.
     * @param localOutputDir The directory for the local output.
     * @param isTest Whether or not it is running in test mode.
     */
    public LocalConfiguration(File localArchiveDir, File localOutputDir, Boolean isTest) {
        this.localArchiveDir = localArchiveDir;
        this.localOutputDir = localOutputDir;
        this.isTest = isTest;
    }
    
    /** @return The directory for the local output. */
    public File getLocalOutputDir() {
        return localOutputDir;
    }
    /** @return The directory for the local archive. */
    public File getLocalArchiveDir() {
        return localArchiveDir;
    }
    /** @return Whether or not it is running in test mode.*/
    public boolean getIsTest() {
        return isTest;
    }
}
