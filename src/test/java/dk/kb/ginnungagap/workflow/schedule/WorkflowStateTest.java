package dk.kb.ginnungagap.workflow.schedule;

import java.util.Arrays;
import java.util.Collection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WorkflowStateTest extends ExtendedTestCase {

    @Test
    public void testWorkflowStateAborted() {
        WorkflowState s = WorkflowState.ABORTED;
        WorkflowState w = WorkflowState.valueOf(s.name());
        Assert.assertEquals(s.toString(), w.toString());
        Assert.assertEquals(w, s);
    }

    @Test
    public void testWorkflowStateNotRunning() {
        WorkflowState s = WorkflowState.NOT_RUNNING;
        WorkflowState w = WorkflowState.valueOf(s.name());
        Assert.assertEquals(s.toString(), w.toString());
        Assert.assertEquals(w, s);
    }

    @Test
    public void testWorkflowStateRunning() {
        WorkflowState s = WorkflowState.RUNNING;
        WorkflowState w = WorkflowState.valueOf(s.name());
        Assert.assertEquals(s.toString(), w.toString());
        Assert.assertEquals(w, s);
    }

    @Test
    public void testWorkflowStateSucceeded() {
        WorkflowState s = WorkflowState.SUCCEEDED;
        WorkflowState w = WorkflowState.valueOf(s.name());
        Assert.assertEquals(s.toString(), w.toString());
        Assert.assertEquals(w, s);
    }

    @Test
    public void testWorkflowStateWaiting() {
        WorkflowState s = WorkflowState.WAITING;
        WorkflowState w = WorkflowState.valueOf(s.name());
        Assert.assertEquals(s.toString(), w.toString());
        Assert.assertEquals(w, s);
    }
    
    @Test
    public void testValues() {
        Collection<WorkflowState> states = Arrays.asList(WorkflowState.values());
        for(WorkflowState s : WorkflowState.values()) {
            Assert.assertTrue(states.contains(s));
        }
    }
}
