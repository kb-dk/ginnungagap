package dk.kb.ginnungagap.workflow.schedule;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SchedulableWorkflowTimerTaskTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Testing the default values, when the schedulable workflow timer task is created.");
        Workflow workflow = mock(Workflow.class);
        long interval = 1L;
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);
        
        Assert.assertNotNull(timerTask.nextRun);
        Assert.assertNotNull(timerTask.workflow);
        
        Assert.assertEquals(timerTask.interval, interval);
        Assert.assertEquals(timerTask.workflow, workflow);
    }
    
    @Test
    public void testGetNextRunWhenPositiveInterval() {
        addDescription("Testing that the schedulable workflow timer task has a next run date, when it has a positive interval");
        Workflow workflow = mock(Workflow.class);
        long interval = 1L;
        Long beforeDate = System.currentTimeMillis();
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        Assert.assertNotNull(timerTask.getNextRun());
        Assert.assertTrue(timerTask.getNextRun().getTime() <= beforeDate);
    }

    @Test
    public void testGetNextRunWhenNegativeInterval() {
        addDescription("Testing that the schedulable workflow timer task does not have a next run date, when it has a negative interval");
        Workflow workflow = mock(Workflow.class);
        long interval = -1L;
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        Assert.assertNull(timerTask.getNextRun());
    }

    @Test
    public void testGetIntervalBetweenRuns() {
        addDescription("Testing the getIntervalBetweenRuns method");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        Assert.assertEquals(timerTask.getIntervalBetweenRuns(), interval);
    }

    @Test
    public void testGetDescription() {
        addDescription("Testing the getDescription method");
        Workflow workflow = mock(Workflow.class);
        
        long interval = Math.abs(new Random().nextLong());
        String description = "DESCRIPTION: " + UUID.randomUUID().toString();
        when(workflow.getDescription()).thenReturn(description);
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        Assert.assertEquals(timerTask.getDescription(), description);        
    }

    @Test
    public void testGetName() {
        addDescription("Testing the getName method");
        Workflow workflow = mock(Workflow.class);
        
        long interval = Math.abs(new Random().nextLong());
        String jobID = "JOBID: " + UUID.randomUUID().toString();
        when(workflow.getJobID()).thenReturn(jobID);
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        Assert.assertEquals(timerTask.getName(), jobID);        
    }

    @Test
    public void testGetWorkflowID() {
        addDescription("Testing the getWorkflowID method");
        Workflow workflow = mock(Workflow.class);
        
        long interval = Math.abs(new Random().nextLong());
        String jobID = "JOBID: " + UUID.randomUUID().toString();
        when(workflow.getJobID()).thenReturn(jobID);
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        Assert.assertEquals(timerTask.getWorkflowID(), jobID);        
    }
    
    @Test
    public void testRunWithNoNextRun() {
        addDescription("Testing the run method when there is no next run");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);
        timerTask.nextRun = null;
        
        timerTask.run();
        
        verifyZeroInteractions(workflow);
    }
    
    @Test
    public void testRunWithLateNextRun() {
        addDescription("Testing the run method when the next run is in the future");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);
        timerTask.nextRun = new Date(System.currentTimeMillis() + 3600000);
        
        timerTask.run();
        
        verifyZeroInteractions(workflow);
    }
    
    @Test
    public void testRunWithNegativeInterval() {
        addDescription("Testing the run method when it has a negative interval, and thus should be run");
        Workflow workflow = mock(Workflow.class);
        long interval = -1;
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);
        
        when(workflow.currentState()).thenReturn(WorkflowState.SUCCEEDED);
        when(workflow.getJobID()).thenReturn("JOB ID: " + UUID.randomUUID().toString());
        
        timerTask.run();
        
        verify(workflow).currentState();
        verify(workflow).getJobID();
        verifyNoMoreInteractions(workflow);
    }
    
    @Test
    public void testRunWithNextRunIsInThePast() {
        addDescription("Testing the run method when the next run is in the past, and it thus should be run");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);
        timerTask.nextRun = new Date(System.currentTimeMillis() - 3600000);
        when(workflow.currentState()).thenReturn(WorkflowState.SUCCEEDED);
        when(workflow.getJobID()).thenReturn("JOB ID: " + UUID.randomUUID().toString());
        
        timerTask.run();
        
        verify(workflow).currentState();
        verify(workflow).getJobID();
        verifyNoMoreInteractions(workflow);
    }
    
    @Test
    public void testRunJobWhenNotInProperState() {
        addDescription("Testing the runJob method when the workflow is not in 'not_running' state.");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        when(workflow.currentState()).thenReturn(WorkflowState.SUCCEEDED);
        when(workflow.getJobID()).thenReturn("JOB ID: " + UUID.randomUUID().toString());

        timerTask.runJob();
        
        verify(workflow).currentState();
        verify(workflow).getJobID();
        verifyNoMoreInteractions(workflow);
    }
    
    @Test
    public void testRunJobWhenInProperStateAndPositiveInterval() {
        addDescription("Testing the runJob method when workflow is ready to run and have a positive interval");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        when(workflow.currentState()).thenReturn(WorkflowState.NOT_RUNNING);
        when(workflow.getJobID()).thenReturn("JOB ID: " + UUID.randomUUID().toString());

        Date nextRun = timerTask.nextRun;
        
        timerTask.runJob();
        
        Assert.assertNotNull(nextRun);
        Assert.assertNotNull(timerTask.nextRun);
        Assert.assertFalse(nextRun.getTime() == timerTask.nextRun.getTime());
        
        verify(workflow).currentState();
        verify(workflow).getJobID();
        verify(workflow).setCurrentState(eq(WorkflowState.WAITING));
        verify(workflow).start();
        verifyNoMoreInteractions(workflow);
    }

    @Test
    public void testRunJobWhenInProperStateAndNegativeInterval() {
        addDescription("Testing the runJob method when workflow is ready to run and have a negative interval");
        Workflow workflow = mock(Workflow.class);
        long interval = -1 * Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        when(workflow.currentState()).thenReturn(WorkflowState.NOT_RUNNING);
        when(workflow.getJobID()).thenReturn("JOB ID: " + UUID.randomUUID().toString());

        Date nextRun = timerTask.nextRun;
        
        timerTask.runJob();
        
        Assert.assertNotNull(nextRun);
        Assert.assertNull(timerTask.getNextRun());
        Assert.assertEquals(nextRun.getTime(), timerTask.nextRun.getTime());
        
        verify(workflow).currentState();
        verify(workflow).getJobID();
        verify(workflow).setCurrentState(eq(WorkflowState.WAITING));
        verify(workflow).start();
        verifyNoMoreInteractions(workflow);
    }
    
    @Test
    public void testRunJobWhenFailure() {
        addDescription("Testing the runJob method when workflow is ready to run, but fails when running");
        Workflow workflow = mock(Workflow.class);
        long interval = Math.abs(new Random().nextLong());
        SchedulableWorkflowTimerTask timerTask = new SchedulableWorkflowTimerTask(interval, workflow);

        when(workflow.currentState()).thenReturn(WorkflowState.NOT_RUNNING);
        when(workflow.getJobID()).thenReturn("JOB ID: " + UUID.randomUUID().toString());

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException("This must fail");
            }
        }).when(workflow).start();
        
        timerTask.runJob();

        verify(workflow).currentState();
        verify(workflow, times(2)).getJobID();
        verify(workflow).setCurrentState(eq(WorkflowState.WAITING));
        verify(workflow).setCurrentState(eq(WorkflowState.ABORTED));
        verify(workflow).start();
        verifyNoMoreInteractions(workflow);
    }
}
