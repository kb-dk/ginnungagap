package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.UUID;

import dk.kb.yggdrasil.utils.HostName;

/**
 * Configuration for the bitrepository.
 */
public class BitmagConfiguration {
    /** The settings directory.*/
    protected final File settingsDir;
    /** The private key file. May be null, when no authentication is used.*/
    protected File privateKeyFile;
    /** The maximum number of failing pillars.*/
    protected final int maxNumberOfFailingPillars;
    /** The id which this component will use when communication on queues of the bitrepository.*/
    protected final String componentId;

    /**
     * Constructor.
     * @param settingsDir The directory with bitrepository settings files.
     * @param privateKeyFile The private key file.
     * @param maxFailingPillars The maximum number of failing pillars.
     */
    public BitmagConfiguration(File settingsDir, File privateKeyFile, int maxFailingPillars) {
        this.settingsDir = settingsDir;
        this.privateKeyFile = privateKeyFile;
        this.maxNumberOfFailingPillars = maxFailingPillars;
        this.componentId = generateComponentID();
    }
    
    /** @return The settings directory.*/
    public File getSettingsDir() {
        return settingsDir;
    }
    /** @return The private key file. May be null, when no authentication is used. */
    public File getPrivateKeyFile() {
        return privateKeyFile;
    }
    /** @return The maximum number of failing pillars.*/
    public int getMaxNumberOfFailingPillars() {
        return maxNumberOfFailingPillars;
    }
    /** @return The id which this component will use when communication on queues of the bitrepository.*/
    public String getComponentId() {
        return componentId;
    }
    
    /**
     * Generates a component id, which includes the hostname and a random UUID.
     * @return The Bitrepository component id for this instance of Ginnungagap.
     */
    public static String generateComponentID() {
        HostName hostname = new HostName();
        String hn = hostname.getHostName();
        return "GinnungagapClient-" + hn + "-" + UUID.randomUUID();
    }
}
