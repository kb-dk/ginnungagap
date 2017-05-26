package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.List;
import java.util.Map;

import dk.kb.ginnungagap.emagasin.importation.PathSubstituteConfiguration;

/**
 * Configuration for the importation from E-magasinet.
 */
public class ImportationConfiguration {
    /** The temporary directory for data during the conversion.*/
    protected final File tempDir;
    /** The script for retrieving the ARC-files.*/
    protected final File scriptFile;
    /** The path substitutions.*/
    protected final PathSubstituteConfiguration substitute;
    
    /**
     * Constructor.
     * @param tempDir The temporary directory for data during the conversion.
     * @param scriptFile The file with the script for retrieving the ARC-files.
     * @param substituteMap The substitutes part of the importation configuration.
     */
    public ImportationConfiguration(File tempDir, File scriptFile, List<Map<String, String>> substituteMap) {
        this.tempDir = tempDir;
        this.scriptFile = scriptFile;
        this.substitute = new PathSubstituteConfiguration(substituteMap);
    }
    
    /** @return The temporary directory for data during the conversion.*/
    public File getTempDir() {
        return tempDir;
    }
    /** @return The script for retrieving the ARC-files.*/
    public File getScriptFile() {
        return scriptFile;
    }
    /** @return The path substitutions.*/
    public PathSubstituteConfiguration getSubstitute() {
        return substitute;
    }
}
