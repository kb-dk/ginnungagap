package dk.kb.metadata.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.canto.cumulus.Cumulus;

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
    /** The metadata generator.*/
    private static final String KB_AGENT_CBS = "kbDkCumulusBevaringsService";

    /** The list of all possible agent names currently defined.*/
    public static final Set<String> AGENT_NAMES = new HashSet<String>(Arrays.asList(
            KB_AGENT,
            KB_AGENT_CBS));

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
     * @return The id for the API agent.
     */
    public static String getApiAgent() {
        return KB_AGENT_CBS;
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
    private static final String KB_API_VERSION = AgentSelector.class.getPackage().getImplementationVersion();

    /** @return Agent type for the KB agent.*/
    public static String getKbAgentType() {
        return KB_TYPE_INTERNAL;
    }

    /** @return The KB agent name.*/
    public static String getKbAgentValue() {
        return getKbAgent();
    }

    /** @return Agent type for the metadata generator agent.*/
    public static String getApiAgentType() {
        return KB_TYPE_INTERNAL;
    }

    /** @return The metadata generator agent.*/
    public static String getApiAgentValue() {
        if(KB_API_VERSION != null) {
            return KB_AGENT_CBS + " (v. " + KB_API_VERSION + ") ";
        } else {
            return KB_AGENT_CBS + " (v. Undetermined) ";
        }
    }

    /** @return Agent type for the department agent.*/
    public static String getDepartmentAgentType() {
        return KB_TYPE_DEPARTMENT;
    }
    
    /** @return The version of the Cumulus client.*/
    public static String getCumulusVersion() {
        return Cumulus.getVersion();
    }
}
