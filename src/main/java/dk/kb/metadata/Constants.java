package dk.kb.metadata;

/**
 * The constants for the Metadata Matrjosjka.
 */
public class Constants {
    /** The current version of the Metadata Matrjosjka.*/
    public static final String VERSION = "1.3.3";
    /** The name of the Agent.*/
    public static final String AGENT_ID = "kbDkMdGen";
    
    /**
     * TODO: rename?
     * @return The name of the entire API.
     */
    public static String getAPI() {
        return "KB METADATA MATRJOSJKA v. " + VERSION;
    }
    
    /**
     * TODO rename?
     * @return The name of the agent for the API.
     */
    public static String getAPIAgent() {
        return AGENT_ID;
    }
    
    /**
     * TODO is this still our profile? Should be perhaps make a new one?
     * @return The current version METS profile.
     */
    public static String getProfileURL() {
        return "http://id.kb.dk/standards/mets/profiles/version_1/kbMetsProfile.xml";
    }
}
