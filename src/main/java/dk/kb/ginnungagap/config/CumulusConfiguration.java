package dk.kb.ginnungagap.config;

public class CumulusConfiguration {

    protected boolean writeAccess;
    protected String serverUrl;
    protected String userName;
    protected String userPassword;
    
    public CumulusConfiguration(boolean writeAccess, String serverUrl, String userName, String userPassword) {
        this.writeAccess = writeAccess;
        this.serverUrl = serverUrl;
        this.userName = userName;
        this.userPassword = userPassword;
    }
    
    public boolean getWriteAccess() {
        return writeAccess;
    }
    
    public String getServerUrl() {
        return serverUrl;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getUserPassword() {
        return userPassword;
    }
}
