package dk.kb.ginnungagap.cumulus;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.config.Configuration;

/**
 * Wrapper for the Cumulus Server, to be instantiated by Spring-Boot.
 */
@Component
public class CumulusWrapper {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CumulusWrapper.class);

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
     * Teardown when this object is being destroyed.
     */
    @PreDestroy
    protected void tearDown() {
        try {
            this.server.close();
        } catch (IOException e) {
            log.warn("Issue occured while closing the Cumulus client.", e);
        }
    }
    
    /**
     * @return Retrieves the wrapped CumulusServer.
     */
    public CumulusServer getServer() {
        return server;
    }
}
