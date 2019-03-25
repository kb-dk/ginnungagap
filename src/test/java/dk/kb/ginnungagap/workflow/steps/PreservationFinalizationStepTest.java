package dk.kb.ginnungagap.workflow.steps;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import dk.kb.ginnungagap.workflow.reporting.WorkflowReport;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;

public class PreservationFinalizationStepTest extends ExtendedTestCase {

    @Test
    public void testStep() throws Exception {
        addDescription("Test the running the preservation finalization step.");
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        PreservationFinalizationStep step = new PreservationFinalizationStep(preserver);
        WorkflowReport report = mock(WorkflowReport.class);

        step.performStep(report);
        
        verify(preserver).uploadAll();
        verifyNoMoreInteractions(preserver);
    }
    
    @Test
    public void testGetName() {
        addDescription("Test retrieving the name of the step.");
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        PreservationFinalizationStep step = new PreservationFinalizationStep(preserver);
        
        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPerformStepFailure() throws Exception {
        addDescription("Test the performStep method when it fails");
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        PreservationFinalizationStep step = new PreservationFinalizationStep(preserver);
        WorkflowReport report = mock(WorkflowReport.class);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException("This must fail");
            }
        }).when(preserver).uploadAll();
        step.performStep(report);
    }
}
