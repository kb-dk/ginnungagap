package dk.kb.ginnungagap.exception;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests of the WarcException class.
 */
public class WarcExceptionTest extends ExtendedTestCase {

    @Test
    public void testWithoutEmbeddedException() {
        String message = "reason";
        WarcException e = new WarcException(message);
        assertEquals(message, e.getMessage());
    }

    @Test
    public void testWithEmbeddedException() {
        String message = "reason";
        String exceptionMessage = "Some error occurred";
        Exception e = new IOException(exceptionMessage);
        WarcException e1 = new WarcException(message, e);
        assertEquals(message, e1.getMessage());
        assertEquals(exceptionMessage, e1.getCause().getMessage());
    }

    @Test
    public void testWithNullArgs() {
        String message = null;
        WarcException e = new WarcException(message);
        assertTrue(e.getMessage() == null);

        Exception anException = null;
        e = new WarcException(message, anException);
        assertTrue(e.getMessage() == null);
        assertTrue(e.getCause() == null);
    }

}
