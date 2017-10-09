package dk.kb.metadata.selector;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.canto.cumulus.Cumulus;

public class AgentSelectorTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Tests the constructor.");
        AgentSelector as = new AgentSelector();
        Assert.assertNotNull(as);
    }
    
    @Test
    public void testGetKbAgent() {
        addDescription("Test that the getKbAgent method delivers the kb agent name");
        Assert.assertEquals(AgentSelector.getKbAgent(), AgentSelector.KB_AGENT);
    }

    @Test
    public void testGetApiAgent() {
        addDescription("Test that the getApiAgent method delivers the kb agent for Cumulus Bevarings Servicename");
        Assert.assertEquals(AgentSelector.getApiAgent(), AgentSelector.KB_AGENT_CBS);
    }

    @Test
    public void testGetAgentValueSuccessDefault() {
        addDescription("Test the getAgentValue method for retrieving agent names.");
        for(String s : AgentSelector.AGENT_NAMES) {
            Assert.assertEquals(AgentSelector.getAgentValue(s), s);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgentValueFailure() {
        addDescription("Test the getAgentValue method for retrieving agent names.");
        AgentSelector.getAgentValue("WRONG");
    }
    
    @Test
    public void testGetAgentTypeSuccessDefault() {
        addDescription("Test the method getAgentType for the default succes values");
        for(String s : AgentSelector.AGENT_TYPES) {
            Assert.assertEquals(AgentSelector.getAgentType(s), s);
        }
    }
    
    @Test
    public void testGetAgentTypeSuccessProgram() {
        addDescription("Test the method getAgentType for the value 'program'");
        Assert.assertEquals(AgentSelector.getAgentType("program"), AgentSelector.KB_TYPE_INTERNAL);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgentTypeFailure() {
        addDescription("Test the method getAgentType when given an incorrect value.");
        AgentSelector.getAgentType("THIS IS NOT A VALID AGENT TYPE");
    }

    @Test
    public void testGetKbAgentType() {
        addDescription("Test the getKbAgentType method");
        Assert.assertEquals(AgentSelector.getKbAgentType(), AgentSelector.KB_TYPE_INTERNAL);
    }
    
    @Test
    public void testGetKbAgentValue() {
        addDescription("Test the getKbAgentValue method");
        Assert.assertEquals(AgentSelector.getKbAgentValue(), AgentSelector.KB_AGENT);
    }
    
    @Test
    public void testGetApiAgentType() {
        addDescription("Test the getApiAgentType method");
        Assert.assertEquals(AgentSelector.getApiAgentType(), AgentSelector.KB_TYPE_INTERNAL);
    }
    
    @Test
    public void testGetApiAgentValue() {
        addDescription("Test the getApiAgentValue method");
        String agent = AgentSelector.getApiAgentValue();
        Assert.assertTrue(agent.contains(AgentSelector.KB_AGENT_CBS));
    }
    
    @Test
    public void testGetDepartmentAgentType() {
        addDescription("Test the getDepartmentAgentType method");
        Assert.assertEquals(AgentSelector.getDepartmentAgentType(), AgentSelector.KB_TYPE_DEPARTMENT);
    }
    
    @Test
    public void testGetCumulusVersion() {
        addDescription("Test the getCumulusVersion method");
        Assert.assertEquals(AgentSelector.getCumulusVersion(), Cumulus.getVersion());
    }
}
