package dk.kb.ginnungagap.exception;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests of the YamlException class.
 */
public class YamlExceptionTest extends ExtendedTestCase {

    @Test
    public void testWithoutEmbeddedException() {
        String message = "reason";
        YamlException e = new YamlException(message);
        assertEquals(message, e.getMessage());
    }

    @Test
    public void testWithEmbeddedException() {
        String message = "reason";
        String exceptionMessage = "Some error occurred";
        Exception e = new IOException(exceptionMessage);
        YamlException e1 = new YamlException(message, e);
        assertEquals(message, e1.getMessage());
        assertEquals(exceptionMessage, e1.getCause().getMessage());
    }

    @Test
    public void testWithNullArgs() {
        String message = null;
        YamlException e = new YamlException(message);
        assertTrue(e.getMessage() == null);

        Exception anException = null;
        e = new YamlException(message, anException);
        assertTrue(e.getMessage() == null);
        assertTrue(e.getCause() == null);
    }

}
