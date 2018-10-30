package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.workflow.schedule.WorkflowState;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;

public class WorkflowTest extends ExtendedTestCase {
    boolean calledInitStep;
    boolean calledName;
    boolean calledInterval;
    boolean calledDescription;
    
    @BeforeMethod
    public void setupMethod() {
        calledInitStep = false;
        calledName = false;
        calledInterval = false;
        calledDescription = false;
    }

    @Test
    public void testInit() {
        addDescription("The the init method.");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                calledInitStep = true;
                return new ArrayList<>();
            }
            
            @Override
            String getName() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                return -1L;
            }
            
            @Override
            String getDescription() {
                throw new RuntimeException("FAIL");
            }
        };
        
        workflow.init();
        
        Assert.assertTrue(calledInitStep);
        Assert.assertFalse(calledName);
        Assert.assertTrue(calledInterval);
        Assert.assertFalse(calledDescription);
    }
    
    @Test
    public void testRunSuccess() {
        addDescription("Test the run method for the success case.");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                calledInitStep = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                calledName = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                return -1L;
            }
            
            @Override
            String getDescription() {
                calledDescription = true;
                throw new RuntimeException("FAIL");
            }
        };
        
        Long dateTime = System.currentTimeMillis() - 3600000;
        workflow.state = WorkflowState.WAITING;
        workflow.nextRun = new Date(dateTime);
        Assert.assertEquals(workflow.getState(), WorkflowState.WAITING);
        workflow.run();
        Assert.assertEquals(workflow.getState(), WorkflowState.SUCCEEDED);
        Assert.assertEquals(workflow.nextRun.getTime(), dateTime.longValue(), "No new date when interval is -1");
        Assert.assertFalse(calledInitStep);
        Assert.assertFalse(calledName);
        Assert.assertTrue(calledInterval);
        Assert.assertFalse(calledDescription);
    }
    
    @Test
    public void testRunFailure() {
        addDescription("Test the run method for the success case.");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                calledInitStep = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                calledName = true;
                return "name";
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                return -1L;
            }
            
            @Override
            String getDescription() {
                calledDescription = true;
                throw new RuntimeException("FAIL");
            }
        };
        
        WorkflowStep step = Mockito.mock(WorkflowStep.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException("TEST");
            }
        }).when(step).run();
        workflow.steps.add(step);
        
        Long dateTime = System.currentTimeMillis() - 3600000;
        workflow.state = WorkflowState.WAITING;
        workflow.nextRun = new Date(dateTime);
        Assert.assertEquals(workflow.getState(), WorkflowState.WAITING);
        workflow.run();
        Assert.assertEquals(workflow.getState(), WorkflowState.ABORTED);
        Assert.assertEquals(workflow.nextRun.getTime(), dateTime.longValue(), "No new date when interval is -1");
        Assert.assertFalse(calledInitStep);
        Assert.assertTrue(calledName);
        Assert.assertTrue(calledInterval);
        Assert.assertFalse(calledDescription);
        
        Mockito.verify(step).run();
        Mockito.verify(step, Mockito.times(3)).getName();
        Mockito.verifyNoMoreInteractions(step);
    }

    @Test
    public void testRunWhenNotWaiting() {
        addDescription("Test the run method, when the workflow is not waiting to run.");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getDescription() {
                throw new RuntimeException("FAIL");
            }
        };
        
        Assert.assertEquals(workflow.getState(), WorkflowState.NOT_RUNNING);
        workflow.run();
        Assert.assertEquals(workflow.getState(), WorkflowState.NOT_RUNNING);
        Assert.assertFalse(calledInitStep);
        Assert.assertFalse(calledName);
        Assert.assertFalse(calledInterval);
        Assert.assertFalse(calledDescription);
    }
    
    @Test
    public void testRunWhenNextRunIsInTheFuture() {
        addDescription("Test the run method, when the next run time is in the future.");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getDescription() {
                throw new RuntimeException("FAIL");
            }
        };
        
        Long dateTime = System.currentTimeMillis() + 3600000;
        workflow.state = WorkflowState.WAITING;
        workflow.nextRun = new Date(dateTime);
        Assert.assertEquals(workflow.getState(), WorkflowState.WAITING);
        workflow.run();
        Assert.assertEquals(workflow.getState(), WorkflowState.WAITING);
        Assert.assertEquals(workflow.nextRun.getTime(), dateTime.longValue());
        Assert.assertFalse(calledInitStep);
        Assert.assertFalse(calledName);
        Assert.assertFalse(calledInterval);
        Assert.assertFalse(calledDescription);
    }
    
    @Test
    public void testPerformStepWhenAborted() {
        addDescription("Test that the workflow does not run a workflow step, when it is in state aborted.");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                calledName = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getDescription() {
                calledDescription = true;
                throw new RuntimeException("FAIL");
            }
        };
        
        WorkflowStep step = Mockito.mock(WorkflowStep.class);
        workflow.state = WorkflowState.ABORTED;
        workflow.performStep(step);
        
        Mockito.verifyZeroInteractions(step);
    }
    
    @Test
    public void testGetNextRunDate() {
        addDescription("Test the getNextRunDate method");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                calledName = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getDescription() {
                calledDescription = true;
                throw new RuntimeException("FAIL");
            }
        };
        
        Assert.assertNull(workflow.nextRun);
        Assert.assertEquals(workflow.getNextRunDate(), Workflow.WORKFLOW_MUST_BE_RUN_MANUALLY);
        
        workflow.nextRun = new Date();
        Assert.assertFalse(workflow.getNextRunDate().contains(Workflow.WORKFLOW_MUST_BE_RUN_MANUALLY));
    }

    @Test
    public void testGetSteps() {
        addDescription("Test the getSteps method");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                calledName = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getDescription() {
                calledDescription = true;
                throw new RuntimeException("FAIL");
            }
        };
        
        Assert.assertTrue(workflow.getSteps().isEmpty());
        
        WorkflowStep step = Mockito.mock(WorkflowStep.class);
        workflow.steps.add(step);
        
        Assert.assertFalse(workflow.getSteps().isEmpty());
        Assert.assertEquals(workflow.getSteps().size(), 1);
        Assert.assertEquals(workflow.getSteps().get(0), step);        
    }
    


    @Test
    public void testCurrentState() {
        addDescription("Test the currentState method");
        Workflow workflow = new Workflow() {
            @Override
            Collection<WorkflowStep> createSteps() {
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getName() {
                calledName = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            Long getInterval() {
                calledInterval = true;
                throw new RuntimeException("FAIL");
            }
            
            @Override
            String getDescription() {
                calledDescription = true;
                throw new RuntimeException("FAIL");
            }
        };
        
        Assert.assertTrue(workflow.getSteps().isEmpty());
        
        WorkflowStep step = Mockito.mock(WorkflowStep.class);
        workflow.steps.add(step);
        
        Assert.assertFalse(workflow.getSteps().isEmpty());
        Assert.assertEquals(workflow.getSteps().size(), 1);
        Assert.assertEquals(workflow.getSteps().get(0), step);        
    }
    
}
