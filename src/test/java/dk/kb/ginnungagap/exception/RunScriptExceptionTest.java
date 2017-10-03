package dk.kb.ginnungagap.exception;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RunScriptExceptionTest extends ExtendedTestCase {

    @Test
    public void testDefaultConstructorWithoutCause() {
        String msg = "TEST MESSAGE: " + UUID.randomUUID().toString();
        
        try {
            throw new RunScriptException(msg);
        } catch (RunScriptException e) {
            Assert.assertEquals(e.getMessage(), msg);
            Assert.assertNull(e.getCause());
        }
    }
    
    @Test
    public void testDefaultConstructorWithCause() {
        String msg = "TEST MESSAGE: " + UUID.randomUUID().toString();
        Throwable cause = new Exception("This is a cause: " + UUID.randomUUID().toString());
        
        try {
            throw new RunScriptException(msg, cause);
        } catch (RunScriptException e) {
            Assert.assertEquals(e.getMessage(), msg);
            Assert.assertEquals(e.getCause(), cause);
        }
    }
}
