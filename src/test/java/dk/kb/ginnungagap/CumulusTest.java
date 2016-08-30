package dk.kb.ginnungagap;

import static org.testng.Assert.assertTrue;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.config.CumulusConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusServer;

public class CumulusTest extends ExtendedTestCase {
//
//    @BeforeClass
//    public void setup() {
//        Cumulus.CumulusStart();
//    }
//    
//    @AfterClass
//    public void stop() {
//        Cumulus.CumulusStop();
//    }
//    
//    @Test
//    public void testRandomStuff() throws Exception {
//        CumulusConfiguration conf = new CumulusConfiguration(false, "cumulus-core-test-01.kb.dk", "audio-adm", "");
//        CumulusServer s = new CumulusServer(conf);
//        assertTrue(s.getServer().isAlive());
//        
//    }
    
    public static void main(String[] args) throws Exception {
        new CumulusTest().testFromKbDoms();
    }
    
    @Test
    public void testFromKbDoms() throws Exception {

        Cumulus.CumulusStart();
        Cumulus.CumulusStop();

        Cumulus.CumulusStart("no");
        Cumulus.CumulusStop();
        
        System.out.println("Cumulus Java SDK version: "
                + Cumulus.getVersion());
    }
}
