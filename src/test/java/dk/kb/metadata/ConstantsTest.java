package dk.kb.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConstantsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        Constants c = new Constants();
        Assert.assertNotNull(c);
    }
    
    @Test
    public void testKbMetsProfile() throws IOException {
        String profile = Constants.getProfileURL();
        Assert.assertNotNull(profile);
        Assert.assertFalse(profile.isEmpty());
        
        URL profileUrl = new URL(profile);
        
        URLConnection con = profileUrl.openConnection();
        try (InputStream is = con.getInputStream()) {
            byte[] b = new byte[1024];
            int currentSize = 0;
            int totalSize = 0;
            while((currentSize = is.read(b)) > 0) {
                totalSize += currentSize;
            }
            
            Assert.assertTrue(totalSize > 0);
        }
    }
}
