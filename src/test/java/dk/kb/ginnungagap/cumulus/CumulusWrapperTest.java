package dk.kb.ginnungagap.cumulus;

import java.io.IOException;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.SetupCumulusTests;

public class CumulusWrapperTest extends ExtendedTestCase {

    @Test
    public void testAll() throws Exception {
        addDescription("Tests all the methods for the CumulusWrapper");
        String catalog = "Audio";
        CumulusWrapper cumulus = new CumulusWrapper();
        Assert.assertNull(cumulus.server);
        Assert.assertNull(cumulus.conf);
        
        addStep("Set configuration and run initialize method", "Instantiates the CumulusServer");
        Configuration conf = SetupCumulusTests.getConfiguration(catalog);
        cumulus.conf = conf;
        cumulus.initialize();
        Assert.assertNotNull(cumulus.server);
        Assert.assertNotNull(cumulus.conf);
        
        addStep("Make mock server and verify that it is returned.", "");
        CumulusServer mockServer = Mockito.mock(CumulusServer.class);
        cumulus.server = mockServer;
        
        Assert.assertEquals(cumulus.getServer(), mockServer);
        
        addStep("Tear down", "The mock server is closed.");
        cumulus.tearDown();
        Mockito.verify(mockServer).close();
        Mockito.verifyNoMoreInteractions(mockServer);
        
        addStep("Failure when closing", "No exception thrown");
        Mockito.doAnswer((Answer<Void>) invocation -> {
            throw new IOException("Failure");
        }).when(mockServer).close();
        cumulus.tearDown();
    }
}
