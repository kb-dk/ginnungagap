package dk.kb.ginnungagap.cumulus;

import com.canto.cumulus.Server;

import dk.kb.ginnungagap.config.CumulusConfiguration;

/**
 * Wrapper for accessing the Cumulus server.
 */
public class CumulusServer {

    /** The configuraiton for the Cumulus server. */
    protected final CumulusConfiguration configuration;
    
    /** The cumulus server access point.*/
    protected Server server;
    
    /** 
     * Constructor.
     * @param configuration The configuration for Cumulus.
     */
    public CumulusServer(CumulusConfiguration configuration) {
        this.configuration = configuration;
        try {
            this.server = Server.openConnection(configuration.getWriteAccess(), configuration.getServerUrl(), 
                    configuration.getUserName(), configuration.getUserPassword());
        } catch (Exception e) {
            throw new IllegalStateException("", e);
        }
    }
    
    public Server getServer() {
        return server;
    }
}
