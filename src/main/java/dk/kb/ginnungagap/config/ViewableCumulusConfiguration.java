package dk.kb.ginnungagap.config;

import java.util.List;

import dk.kb.cumulus.config.CumulusConfiguration;

/**
 * A viewable version of the cumulus configuration, where it is not possible to extract the password.
 */
public class ViewableCumulusConfiguration {
    /** The configuration for Cumulus.*/
    protected final CumulusConfiguration cumulusConf;
    
    /**
     * Constructor.
     * @param conf The configuration for Cumulus.
     */
    public ViewableCumulusConfiguration(CumulusConfiguration conf) {
        this.cumulusConf = conf;
    }
    
    /**
     * @return The list of catalogs accessible through Cumulus.
     */
    public List<String> getCatalogs() {
        return cumulusConf.getCatalogs();
    }
    
    /**
     * @return The URL for the Cumulus server.
     */
    public String getServerUrl() {
        return cumulusConf.getServerUrl();
    }
    
    /**
     * @return The username for login to Cumulus.
     */
    public String getUserName() {
        return cumulusConf.getUserName();
    }
    
    /**
     * This does not return the actual password, but instead an obscured string.
     * @return The obscured password for login to Cumulus.
     */
    public String getUserPassword() {
        return "***********";
    }
}
