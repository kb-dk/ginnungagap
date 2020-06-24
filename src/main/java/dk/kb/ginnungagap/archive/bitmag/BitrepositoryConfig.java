package dk.kb.ginnungagap.archive.bitmag;

import dk.kb.ginnungagap.exception.ArgumentCheck;

import java.io.File;

/**
 * Configuratio for the Bitrepository.
 */
public class BitrepositoryConfig {

    /** The archive settings directory needed to upload to a bitmag style repository. May not be null. */
    private final File settingsDir;
    /** The authentication key used by the bitrepository clients and the messagequeue. */
    private File privateKeyFile;
    /** The maximum number of failing pillars. Default is 0, can be overridden by settings in the bitmag.yml. */
    private int maxNumberOfFailingPillars = 0; 
    /** The bitrepository component id. */
    private final String componentId;
    
    /** Name of YAML property used to find settings dir. */
    public static final String YAML_BITMAG_SETTINGS_DIR_PROPERTY = "settings_dir";
    /** Name of YAML property used to find keyfile. */
    public static final String YAML_BITMAG_KEYFILE_PROPERTY = "keyfile";

    /** Name of the YAML sub-map client*/
    public static final String YAML_BITMAG_CLIENTS = "client";
    /** Name of the YAML property under the client sub-map for maximum number of pillars accept to fail.*/
    public static final String YAML_BITMAG_CLIENT_PUTFILE_MAX_PILLAR_FAILURES = "putfile_max_pillars_failures";
    
    /**
     * Constructor.
     * @param settingsDir The settings directory.
     * @param privateKeyFile The private key/certificate file.
     * @param maxNumberOfFailingPillars The maximum number of pillars allowed to fail a given operation, 
     * before calling it a failure.
     * @param componentId The id of the bitrepository client.
     */
    public BitrepositoryConfig(File settingsDir, File privateKeyFile, int maxNumberOfFailingPillars, 
            String componentId) {
        ArgumentCheck.checkExistsDirectory(settingsDir, "File settingsDir");
        ArgumentCheck.checkNotNegativeInt(maxNumberOfFailingPillars, "int maxNumberOfFailingPillars");
        ArgumentCheck.checkNotNullOrEmpty(componentId, "String componentId");
        this.settingsDir = settingsDir;
        this.privateKeyFile = privateKeyFile;
        this.maxNumberOfFailingPillars = maxNumberOfFailingPillars; 
        this.componentId = componentId;
    }

    /**
     * @return The settings directory for the bitrepository settings files.
     */
    public File getSettingsDir() {
        return settingsDir;
    }
    
    /** 
     * @return The authentication key used by the putfileClient. 
     */
    public File getPrivateKeyFile() {
        return privateKeyFile;
    }
    
    /** 
     * @return The maximum number of failing pillars. 
     */
    public int getMaxNumberOfFailingPillars() {
        return maxNumberOfFailingPillars;
    }
    
    /** 
     * @return The bitrepository component id. 
     */
    public String getComponentId() {
        return componentId;
    }
}
