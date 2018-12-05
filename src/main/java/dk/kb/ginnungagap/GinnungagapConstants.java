package dk.kb.ginnungagap;

import org.springframework.beans.factory.annotation.Value;

/**
 * The constants for the Ginnungap application.
 * Automatically extracted values for the setup.
 */
public class GinnungagapConstants {

    /** The name of the application, automatically extracted from setup.*/
    @Value("${application.name}")
    protected static String applicationName;

    /** The build version of the application, automatically extracted.*/
    @Value("${build.version}")
    protected static String buildVersion;
    
    /**
     * @return The automatically extracted name of the application.
     */
    public static String getApplicationName() {
        return applicationName;
    }
    
    /**
     * @return The build version of the application.
     */
    public static String getBuildVersion() {
        if(buildVersion == null) {
            return GinnungagapConstants.class.getPackage().getImplementationVersion();
        }
        return buildVersion;
    }
}
