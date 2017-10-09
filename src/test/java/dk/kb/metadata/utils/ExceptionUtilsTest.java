package dk.kb.metadata.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExceptionUtilsTest extends ExtendedTestCase {

    @BeforeMethod
    public void setupMethod() {
        ExceptionUtils.clean();
    }
    
    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        ExceptionUtils eu = new ExceptionUtils();
        Assert.assertNotNull(eu);
    }
    
    @Test
    public void testRetriveFailureWhenNoExceptionsHaveBeenReported() {
        addDescription("The the retrieveFailure method when no exceptions have been reported.");
        Assert.assertNull(ExceptionUtils.retrieveFailure());
        Assert.assertFalse(ExceptionUtils.hasFailure());
    }
    
    @Test
    public void testRetrieveFailureWhenOneExceptionHaveBeenReported() {
        addDescription("The the retrieveFailure method when one exception have been reported.");
        RuntimeException e = new RuntimeException("THIS IS THE EXCEPTION FOR THIS TEST!!!");
        ExceptionUtils.insertException(e);
        
        Assert.assertEquals(ExceptionUtils.retrieveFailure(), e);
        Assert.assertTrue(ExceptionUtils.hasFailure());
    }
    
    @Test
    public void testRetrieveFailureWhenMultipleExceptionsHaveBeenReported() {
        addDescription("The the retrieveFailure method when multiple exceptions have been reported.");
        RuntimeException e1 = new RuntimeException("Exception number 1");
        RuntimeException e2 = new RuntimeException("Exception number 2");
        RuntimeException e3 = new RuntimeException("Exception 3");
        ExceptionUtils.insertException(e1);
        ExceptionUtils.insertException(e2);
        ExceptionUtils.insertException(e3);
        
        Exception res = ExceptionUtils.retrieveFailure();
        Assert.assertNotEquals(res, e1);
        Assert.assertNotEquals(res, e2);
        Assert.assertNotEquals(res, e3);
    }
}
