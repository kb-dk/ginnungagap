package dk.kb.ginnungagap.emagasin.importation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PathSubstituteConfigurationTest extends ExtendedTestCase {

    String prefix1From = UUID.randomUUID().toString();
    String prefix1To = UUID.randomUUID().toString();
    String prefix2From = UUID.randomUUID().toString();
    String prefix2To = UUID.randomUUID().toString();
    
    PathSubstituteConfiguration conf;
    
    @BeforeClass
    public void setup() {
        Map<String, String> s1 = new HashMap<String, String>();
        s1.put("from", prefix1From);
        s1.put("to", prefix1To);
        Map<String, String> s2 = new HashMap<String, String>();
        s2.put("from", prefix2From);
        s2.put("to", prefix2To);
        conf = new PathSubstituteConfiguration(Arrays.asList(s1, s2));
    }
    
    @Test
    public void testSubstitution() {
        String startPath = prefix1From + "-" + UUID.randomUUID().toString();
        String resPath = conf.substitute(startPath);
        
        Assert.assertNotEquals(startPath, resPath);
        Assert.assertTrue(startPath.startsWith(prefix1From));
        Assert.assertTrue(resPath.startsWith(prefix1To));
    }
    
    @Test
    public void testSubstitution2() {
        String startPath = prefix2From + "-" + prefix1From;
        String resPath = conf.substitute(startPath);
        
        Assert.assertNotEquals(startPath, resPath);
        Assert.assertTrue(startPath.startsWith(prefix2From));
        Assert.assertTrue(resPath.startsWith(prefix2To));
        
        Assert.assertTrue(startPath.contains(prefix1From));
        Assert.assertTrue(resPath.contains(prefix1From));
    }
    
    @Test
    public void testNoSubstitution() {
        String startPath = UUID.randomUUID().toString();
        String resPath = conf.substitute(startPath);
        
        Assert.assertEquals(startPath, resPath);
    }
}
