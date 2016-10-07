package dk.kb.ginnungagap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class SillyTest extends ExtendedTestCase {

    @Test
    public void testTrivia() {
        addDescription("Test math");
        assertEquals(2+2, 4);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailure() throws Exception {
        throw new IllegalStateException("My hovercraft is full of eels");
    }
    
    @Test
    public void testNull() {
        addDescription("Test that casting a null does not give a null-pointer exception");
        String s = (String) null;
        assertNull(s);
    }
}
