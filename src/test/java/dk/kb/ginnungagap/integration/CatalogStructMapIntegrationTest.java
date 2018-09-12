package dk.kb.ginnungagap.integration;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.SetupCumulusTests;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class CatalogStructMapIntegrationTest extends ExtendedTestCase {

    Configuration conf;
    File xsltFile;
    String catalogName = "Conservation";
    String collectionID = "TestCollectionID";
    String intellectualEntityID = "intellectuel_entity_id";
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        conf = SetupCumulusTests.getConfiguration(catalogName);
        FileUtils.copyDirectory(new File("src/main/resources/scripts/xslt"), new File(TestFileUtils.getTempDir(), "scripts/xslt"));
        xsltFile = new File(TestFileUtils.getTempDir().getAbsolutePath() + "/scripts/xslt/transformCatalogStructmap.xsl");
        
        Cumulus.CumulusStart();
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
        Cumulus.CumulusStop();
    }
    
//    @Test(enabled = false)
//    public void testPerformingWorkflow() {
//        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
//        MetadataTransformer transformer = new MetadataTransformer(xsltFile); 
//        BitmagPreserver preserver = Mockito.mock(BitmagPreserver.class);
//
//        CatalogStructmap.createCatalogStructmap(cumulusServer, transformer, preserver, conf, catalogName, collectionID, intellectualEntityID);
//    }
    
}
