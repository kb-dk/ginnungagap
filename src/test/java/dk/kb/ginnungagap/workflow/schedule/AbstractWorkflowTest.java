package dk.kb.ginnungagap.workflow.schedule;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AbstractWorkflowTest extends ExtendedTestCase {

    @Test
    public void testStartWithNullSteps() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        Assert.assertNull(workflow.steps);
        workflow.start();
    }

    @Test
    public void testStartWithNoSteps() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        workflow.setWorkflowSteps(new ArrayList<WorkflowStep>());
        Assert.assertTrue(workflow.steps.isEmpty());
        workflow.start();
    }

    @Test
    public void testStartStep() throws Exception {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        WorkflowStep step = mock(WorkflowStep.class);
        workflow.setWorkflowSteps(Arrays.asList(step));
        Assert.assertFalse(workflow.steps.isEmpty());
        workflow.start();
        
        verify(step).performStep();
        verify(step).getName();
        verifyNoMoreInteractions(step);
    }
    
    @Test
    public void testPerformStepSuccess() throws Exception {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        WorkflowStep step = mock(WorkflowStep.class);
        workflow.performStep(step);
        
        verify(step).performStep();
        verify(step).getName();
        verifyNoMoreInteractions(step);
    }
    
    @Test
    public void testPerformStepWhenAborted() throws Exception {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        WorkflowStep step = mock(WorkflowStep.class);
        workflow.setCurrentState(WorkflowState.ABORTED);
        workflow.performStep(step);
        
        verifyZeroInteractions(step);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPerformStepWhenFailing() throws Exception {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        WorkflowStep step = mock(WorkflowStep.class);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                throw new RuntimeException("This Must Fail");
            }
        }).when(step).performStep();
        workflow.performStep(step);
    }
    
    @Test
    public void testFinish() throws Exception {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        WorkflowStep step = mock(WorkflowStep.class);
        
        workflow.setCurrentState(WorkflowState.RUNNING);
        workflow.currentStep = step;
        
        workflow.finish();
        Assert.assertEquals(workflow.currentState(), WorkflowState.NOT_RUNNING);
        Assert.assertNull(workflow.currentStep);
    }
    
    @Test
    public void testHashCode() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        Assert.assertNotNull(workflow.hashCode());
        Assert.assertEquals(workflow.hashCode(), workflow.hashCode());
    }
    
    @Test
    public void testEqualsNull() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        Assert.assertFalse(workflow.equals(null));
    }
    
    @Test
    public void testEqualsWhenSame() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        Assert.assertTrue(workflow.equals(workflow));
    }
    
    @Test
    public void testEqualsOtherObject() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        Assert.assertFalse(workflow.equals(new Integer(1)));
    }
    
    @Test
    public void testEqualsOtherAbstractWorkflow() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        AbstractWorkflow workflow2 = new AbstractWorkflow("TEST2") {
            @Override
            public String getDescription() {
                return "TEST2";
            }
        };
        Assert.assertFalse(workflow.equals(workflow2));
    }
    
    @Test
    public void testGetHumanReadableStateFromState() {
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        
        Assert.assertEquals(workflow.getHumanReadableState(), WorkflowState.NOT_RUNNING.name());
    }
    
    @Test
    public void testGetHumanReadableStateFromStep() {
        String stepValue = "THIS IS THE VALUE FROM THE STEP: " + UUID.randomUUID().toString();
        AbstractWorkflow workflow = new AbstractWorkflow("TEST") {
            @Override
            public String getDescription() {
                return "TEST";
            }
        };
        WorkflowStep step = mock(WorkflowStep.class);
        when(step.getName()).thenReturn(stepValue);
        
        workflow.setWorkflowSteps(Arrays.asList(step));
        workflow.currentStep = step;
        
        Assert.assertEquals(workflow.getHumanReadableState(), stepValue);
    }
}
