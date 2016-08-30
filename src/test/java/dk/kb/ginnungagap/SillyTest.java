package dk.kb.ginnungagap;

import static org.testng.Assert.assertEquals;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class SillyTest extends ExtendedTestCase {

    @Test
    public void testTrivia() {
        assertEquals(2+2, 4);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailure() throws Exception {
        throw new IllegalStateException("My hovercraft is full of eels");
    }
}
