package dk.kb.metadata.utils;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IdentifierManagerUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        IdentifierManager im = new IdentifierManager();
        Assert.assertNotNull(im);
    }
    
    @Test
    public void testGetEventIdentifierEmpty() {
        addDescription("Test the getEventIdentifier method ");
        addStep("Retrieve uuid for a file", "Shoud be a new uuid");
        String file = UUID.randomUUID().toString();
        String uuid = IdentifierManager.getEventIdentifier(file);
        Assert.assertNotNull(uuid);
        Assert.assertFalse(uuid.isEmpty());
        
        addStep("Retrieve uuid for another file", "Should be different uuid");
        String file2 = UUID.randomUUID().toString();
        String uuid2 = IdentifierManager.getEventIdentifier(file2);
        Assert.assertNotNull(uuid2);
        Assert.assertFalse(uuid2.isEmpty());
        Assert.assertNotEquals(uuid2, uuid);
        
        addStep("Retrieve uuid for the same file as first inserted", "should be same uuid");
        String uuid3 = IdentifierManager.getEventIdentifier(file);
        Assert.assertNotNull(uuid3);
        Assert.assertEquals(uuid3, uuid);
        Assert.assertNotEquals(uuid3, uuid2);
    }

    @Test
    public void testCleaning() {
        addDescription("Test cleaning up the idenfier manager");
        String file = UUID.randomUUID().toString();
        String uuid = IdentifierManager.getEventIdentifier(file);
        
        IdentifierManager.clean();
        String uuid2 = IdentifierManager.getEventIdentifier(file);
        Assert.assertNotEquals(uuid, uuid2);        
    }
}
