package dk.kb.ginnungagap.workflow.schedule;

import dk.kb.ginnungagap.workflow.*;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Arrays;

public class WorkflowSchedulerTest extends ExtendedTestCase {

    @Test
    public void testScheduler() {
        WorkflowScheduler scheduler = new WorkflowScheduler();
        
        PreservationWorkflow preservationWorkflow = Mockito.mock(PreservationWorkflow.class);
        UpdatePreservationWorkflow updateWorkflow = Mockito.mock(UpdatePreservationWorkflow.class);
        ValidationWorkflow validationWorkflow = Mockito.mock(ValidationWorkflow.class);
        ImportWorkflow importWorkflow = Mockito.mock(ImportWorkflow.class);

        scheduler.importWorkflow = importWorkflow;
        scheduler.preservationWorkflow = preservationWorkflow;
        scheduler.updateWorkflow = updateWorkflow;
        scheduler.validationWorkflow = validationWorkflow;

        scheduler.scheduleWorkflows();
        
        Mockito.verifyZeroInteractions(preservationWorkflow);
        Mockito.verifyZeroInteractions(updateWorkflow);
        Mockito.verifyZeroInteractions(validationWorkflow);
        Mockito.verifyZeroInteractions(importWorkflow);
        
        scheduler.shutDown();
        
        Mockito.verify(preservationWorkflow).cancel();
        Mockito.verifyNoMoreInteractions(preservationWorkflow);
        Mockito.verify(updateWorkflow).cancel();
        Mockito.verifyNoMoreInteractions(updateWorkflow);
        Mockito.verify(validationWorkflow).cancel();
        Mockito.verifyNoMoreInteractions(validationWorkflow);
        Mockito.verify(importWorkflow).cancel();
        Mockito.verifyNoMoreInteractions(importWorkflow);        
    }
}
