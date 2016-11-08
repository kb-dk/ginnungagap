package dk.kb.metadata.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dk.kb.metadata.Constants;
import dk.kb.metadata.utils.ExceptionUtils;
import dk.kb.metadata.utils.StringUtils;

/**
 * The selector for agents.
 * Contains all the currently defined agents.
 */
public final class AgentSelector {
    /** Private constructor for this Utility class.*/
    private AgentSelector() {}

    /** The KB agent.*/
    private static final String KB_AGENT = "kbDk";
    /** The ingest agent.*/
    private static final String KB_INGEST = "kbDkDomsBmIngest";
    /** The metadata generator.*/
    private static final String KB_METADATA_GENERATOR = "kbDkMdGen";

    // The different names for the agents for the departments.
    /** Deprecated department abbr. */
    private static final String KB_DEP_DFS = "kbDkDfs";
    /** Deprecated department abbr. */
    private static final String KB_DEP_KULT = "kbDkKult";
    /** Deprecated department abbr. */
    private static final String KB_DEP_DB = "kbDkDb";
    /** Deprecated department abbr. */
    private static final String KB_DEP_PLG = "kbDkPlg";
    /** Deprecated department abbr. */
    private static final String KB_DEP_KOB = "kbDkKob";
    /** Deprecated department abbr. */
    private static final String KB_DEP_BEV = "kbDkBev";
    /** Deprecated department abbr. */
    private static final String KB_DEP_KAT = "kbDkKat";
    /** Deprecated department abbr. */
    private static final String KB_DEP_HA = "kbDkHa";
    /** Deprecated department abbr. */
    private static final String KB_DEP_OJA = "kbDkOja";
    /** Deprecated department abbr. */
    private static final String KB_DEP_MTA = "kbDkMta";
    /** Deprecated department abbr. */
    private static final String KB_DEP_DCM = "kbDkDcm";
    /** Deprecated department abbr. */
    private static final String KB_DEP_FRSK = "kbDkFrsk";

    /** Current department abbr.*/
    private static final String KB_DEP_NSA = "kbDkNSA";
    /** Current department abbr.*/
    private static final String KB_DEP_NSA1 = "kbDkNSA1";
    /** Current department abbr.*/
    private static final String KB_DEP_NSA2 = "kbDkNSA2";

    /** The list of all possible agent names currently defined.*/
    public static final Set<String> AGENT_NAMES = new HashSet<String>(Arrays.asList(
            KB_AGENT,
            KB_INGEST,
            KB_METADATA_GENERATOR,
            KB_DEP_NSA,
            KB_DEP_NSA1,
            KB_DEP_NSA2,
            KB_DEP_DFS,
            KB_DEP_KULT,
            KB_DEP_DB,
            KB_DEP_PLG,
            KB_DEP_KOB,
            KB_DEP_BEV,
            KB_DEP_KAT,
            KB_DEP_HA,
            KB_DEP_OJA,
            KB_DEP_MTA,
            KB_DEP_DCM,
            KB_DEP_FRSK));

    // The different types of agents.
    /** Internal agent. */
    private static final String KB_TYPE_INTERNAL = "kbDkInternal";
    /** Personal agent.*/
    private static final String KB_TYPE_PERSONEL = "kbDkPersonel";
    /** Department agent.*/
    private static final String KB_TYPE_DEPARTMENT = "kbDkDepartment";

    /** The list of all possible agent types currently defined.*/
    public static final Set<String> AGENT_TYPES = new HashSet<String>(Arrays.asList(
            KB_TYPE_INTERNAL,
            KB_TYPE_PERSONEL,
            KB_TYPE_DEPARTMENT));

    /**
     * @return The id for the agent for kb.dk.
     */
    public static String getKbAgent() {
        return KB_AGENT;
    }

    /**
     * @return The id for the ingest agent.
     */
    public static String getIngestAgent() {
        return KB_INGEST;
    }

    /**
     * @return The id for the metadata agent.
     */
    public static String getMdGen() {
        return KB_METADATA_GENERATOR;
    }

    /**
     * Validates whether a name of a given agent is valid. Throws an exception otherwise.
     * It starts with the initials, then it is attempted to locate the corresponding agent.
     * @param agentName The name of the agent to validate (or translate into the corresponding agent).
     * @return The agent corresponding to the given name.
     */
    public static String getAgentValue(String agentName) {
        if(AGENT_NAMES.contains(agentName)) {
            return agentName;
        }

        if(agentName.contains(":")) {
            String[] split = agentName.split("[:]");
            String agent = "kbDk" + StringUtils.encodeAsUpperCamelCase(split[0]);
            if(AGENT_NAMES.contains(agent)) {
                return agent;
            }
        }

        // 'gatekeeper' is an alias for 'kbDkIngest'
        if(agentName.equals("gatekeeper")) {
            return KB_INGEST;
        }

        IllegalStateException res = new IllegalStateException("The agent named '" + agentName + "' does not exist.");
        ExceptionUtils.insertException(res);
        throw res;
    }

    /**
     * Validate and returns the given agent type, or the corresponding agent type.
     * @param agentType The type of agent to validate.
     * @return The given agent type or the corresponding one.
     */
    public static String getAgentType(String agentType) {
        if(AGENT_TYPES.contains(agentType)) {
            return agentType;
        }

        // If it is 'program', then it is an internal agent
        if(agentType.equals("program")) {
            return KB_TYPE_INTERNAL;
        }

        IllegalStateException res = new IllegalStateException("Unknown agent type:" + agentType);
        ExceptionUtils.insertException(res);
        throw res;
    }

    /**
     * The version for the ingest agent.
     */
    private static String KB_INGEST_VERSION = "";

    /**
     * @param s The version for the metadata generator.
     */
    public static void setKbDkDomsBmIngestVersion(String s) {
        KB_INGEST_VERSION = "(" + s + ")";
    }

    /** @return Agent type for the KB agent.*/
    public static String getKbAgentType() {
        return KB_TYPE_INTERNAL;
    }

    /** @return The KB agent name.*/
    public static String getKbAgentValue() {
        return getKbAgent();
    }

    /** @return Agent type for the metadata generator agent.*/
    public static String getMdGenAgentType() {
        return KB_TYPE_INTERNAL;
    }

    /** @return The metadata generator agent.*/
    public static String getMdGenAgentValue() {
        return getMdGen() + " (v. " + Constants.VERSION + ") ";
    }

    /** @return Agent type for the ingest agent.*/
    public static String getIngestAgentType() {
        return KB_TYPE_INTERNAL;
    }

    /** @return The ingest agent.*/
    public static String getIngestAgentValue() {
        return getIngestAgent() + " " + KB_INGEST_VERSION;
    }

    /** @return Agent type for the department agent.*/
    public static String getDepartmentAgentType() {
        return KB_TYPE_DEPARTMENT;
    }
}
