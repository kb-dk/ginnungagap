package dk.kb.ginnungagap.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for the methods in the HostName class.
 *
 */
public class HostNameTest extends ExtendedTestCase {

    @Test
    public void testHostNamePresent() throws Exception {
        HostName hostname = new HostName();
        String hn =hostname.getHostName();
        Assert.assertNotNull(hn);
    }
}
