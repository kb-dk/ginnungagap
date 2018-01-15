package dk.kb.ginnungagap.workflow.schedule;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WorkflowSchedulerTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Testing the default values of the scheduler, when it is created.");
        WorkflowScheduler scheduler = new WorkflowScheduler();
        
        Assert.assertNotNull(scheduler);
        Assert.assertNotNull(scheduler.timer);
        Assert.assertNotNull(scheduler.intervalTasks);
        Assert.assertTrue(scheduler.intervalTasks.isEmpty());
    }
    
    @Test
    public void testScheduleWithPositiveInterval() throws InterruptedException {
        addDescription("Testing that the scheduler starts a workflow immediately, when it has a positive scheduling interval.");
        WorkflowScheduler scheduler = new WorkflowScheduler();

        Workflow workflow = mock(Workflow.class);
        when(workflow.getJobID()).thenReturn(UUID.randomUUID().toString());
        when(workflow.currentState()).thenReturn(WorkflowState.NOT_RUNNING);
        
        Assert.assertTrue(scheduler.intervalTasks.isEmpty());
        scheduler.schedule(workflow, 3600000L);
        Assert.assertFalse(scheduler.intervalTasks.isEmpty());
        Assert.assertEquals(scheduler.intervalTasks.size(), 1);
        
        SchedulableWorkflowTimerTask timerTask = scheduler.intervalTasks.values().iterator().next();
        Assert.assertEquals(timerTask.getWorkflowID(), workflow.getJobID());
        
        synchronized(scheduler) {
            scheduler.wait(100);
        }
        
        verify(workflow).currentState();
        verify(workflow).setCurrentState(eq(WorkflowState.WAITING));
        verify(workflow, times(5)).getJobID();
        verify(workflow).start();
        verifyNoMoreInteractions(workflow);
    }
    
    @Test
    public void testScheduleWithNegativeInterval() throws InterruptedException {
        addDescription("Test that the scheduler does not start a workflow immediately, when it has a negative scheudling interval.");
        WorkflowScheduler scheduler = new WorkflowScheduler();

        Workflow workflow = mock(Workflow.class);
        when(workflow.getJobID()).thenReturn(UUID.randomUUID().toString());
        when(workflow.currentState()).thenReturn(WorkflowState.NOT_RUNNING);
        
        Assert.assertTrue(scheduler.intervalTasks.isEmpty());
        scheduler.schedule(workflow, -3600000L);
        Assert.assertFalse(scheduler.intervalTasks.isEmpty());
        Assert.assertEquals(scheduler.intervalTasks.size(), 1);
        
        SchedulableWorkflowTimerTask timerTask = scheduler.intervalTasks.values().iterator().next();
        Assert.assertEquals(timerTask.getWorkflowID(), workflow.getJobID());
        
        synchronized(scheduler) {
            scheduler.wait(100);
        }
        
        verify(workflow, times(4)).getJobID();
        verifyNoMoreInteractions(workflow);
    }
}
