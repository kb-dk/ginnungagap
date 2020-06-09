package dk.kb.ginnungagap.exception;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Tests of the BitmagException class.
 */
public class BitmagExceptionTest extends ExtendedTestCase {

    @Test
    public void testWithoutEmbeddedException() {
        String message = "reason";
        BitmagException e = new BitmagException(message);
        assertEquals(message, e.getMessage());
    }

    @Test
    public void testWithEmbeddedException() {
        String message = "reason";
        String exceptionMessage = "Some error occurred";
        Exception e = new IOException(exceptionMessage);
        BitmagException e1 = new BitmagException(message, e);
        assertEquals(message, e1.getMessage());
        assertEquals(exceptionMessage, e1.getCause().getMessage());
    }

    @Test
    public void testWithNullArgs() {
        String message = null;
        BitmagException e = new BitmagException(message);
        assertTrue(e.getMessage() == null);

        Exception anException = null;
        e = new BitmagException(message, anException);
        assertTrue(e.getMessage() == null);
        assertTrue(e.getCause() == null);
    }

}
