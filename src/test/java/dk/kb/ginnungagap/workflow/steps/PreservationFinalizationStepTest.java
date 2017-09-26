package dk.kb.ginnungagap.workflow.steps;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;

public class PreservationFinalizationStepTest extends ExtendedTestCase {

    @Test
    public void testStep() throws Exception {
        addDescription("Test the running the preservation finalization step.");
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        PreservationFinalizationStep step = new PreservationFinalizationStep(preserver);
        
        step.performStep();
        
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
}
