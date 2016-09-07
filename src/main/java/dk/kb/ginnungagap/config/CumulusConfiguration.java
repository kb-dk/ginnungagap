package dk.kb.ginnungagap.config;

/**
 * Configuration for accessing Cumulus.
 */
public class CumulusConfiguration {
    /** Whether or not we need write access.*/
    protected boolean writeAccess;
    /** The URL to the Cumlus server.*/
    protected String serverUrl;
    /** The username for logging into the server.*/
    protected String userName;
    /** The password for logging into the server.*/
    protected String userPassword;
    
    /**
     * Constructor.
     * @param writeAccess Whether or not we need write access.
     * @param serverUrl The URL to the Cumlus server.
     * @param userName The username for logging into the server.
     * @param userPassword The password for logging into the server.
     */
    public CumulusConfiguration(boolean writeAccess, String serverUrl, String userName, String userPassword) {
        this.writeAccess = writeAccess;
        this.serverUrl = serverUrl;
        this.userName = userName;
        this.userPassword = userPassword;
    }
    
    /** @return Whether or not we need write access.*/
    public boolean getWriteAccess() {
        return writeAccess;
    }
    
    /** @return The URL to the Cumlus server.*/
    public String getServerUrl() {
        return serverUrl;
    }
    
    /** @return The username for logging onto the server.*/
    public String getUserName() {
        return userName;
    }
    
    /** @return The password for logging onto the server.*/
    public String getUserPassword() {
        return userPassword;
    }
}
