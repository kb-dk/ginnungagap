package dk.kb.ginnungagap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dk.kb.metadata.selector.AgentSelector;

/**
 * The constants for the Ginnungap application.
 * Automatically extracted values for the setup.
 */
@Component
public class GinnungagapConstants {

    /** The name of the application, automatically extracted from setup.*/
    @Value("${application.name}")
    protected String applicationName;

    /** The build version of the application, automatically extracted.*/
    @Value("${build.version}")
    protected String buildVersion;
    
    /**
     * Setup the constants on the static 
     */
    @PostConstruct
    public void setup() {
        AgentSelector.setApiVersion(buildVersion);
    }
    
    /**
     * @return The automatically extracted name of the application.
     */
    public String getApplicationName() {
        return applicationName;
    }
    
    /**
     * @return The build version of the application.
     */
    public String getBuildVersion() {
        if(buildVersion == null) {
            return GinnungagapConstants.class.getPackage().getImplementationVersion();
        }
        return buildVersion;
    }
}
