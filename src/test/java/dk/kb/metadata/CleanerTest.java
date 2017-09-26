package dk.kb.metadata;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CleanerTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        Cleaner c = new Cleaner();
        Assert.assertNotNull(c);
    }
    
    @Test
    public void testCleaning() {
        Cleaner.cleanStuff();
    }
}
