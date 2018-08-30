package dk.kb.ginnungagap.cumulus;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.config.Configuration;

/**
 * Wrapper for the Cumulus Server, to be instantiated by Spring-Boot.
 */
@Component
public class CumulusWrapper {

    /** The Cumulus server.*/
    protected CumulusServer server;
    
    /** The configuration. Auto-wired.*/
    @Autowired
    protected Configuration conf;
    
    /**
     * Initializes this component.
     */
    @PostConstruct
    protected void initialize() {
        this.server = new CumulusServer(conf.getCumulusConf());
    }
    
    /**
     * @return Retrieves the wrapped CumulusServer.
     */
    public CumulusServer getServer() {
        return server;
    }
}
