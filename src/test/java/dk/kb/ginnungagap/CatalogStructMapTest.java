package dk.kb.ginnungagap;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.SetupCumulusTests;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;

public class CatalogStructMapTest extends ExtendedTestCase {

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
//        TestFileUtils.tearDown();
        Cumulus.CumulusStop();
    }
    
    @Test//(enabled = false)
    public void testPerformingWorkflow() {
        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
//        MetadataTransformer transformer = Mockito.mock(MetadataTransformer.class); 
        MetadataTransformer transformer = new XsltMetadataTransformer(xsltFile); 
        BitmagPreserver preserver = Mockito.mock(BitmagPreserver.class);

        CatalogStructmap.createCatalogStructmap(cumulusServer, transformer, preserver, conf, catalogName, collectionID, intellectualEntityID);
    }
    
//    @Test(enabled = false)
//    public void testGinnungagap() {
//        Ginnungagap.main("src/test/resources/conf/ginnungagap.yml", "bitmag");
//    }
//
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testNoArguments() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            Ginnungagap.main();
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//    
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testInvalidArchiveType() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            Ginnungagap.main(testConf.getAbsolutePath(), "THIS_IS_NOT_A_VALID_ARCHIVE");
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//    
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testMissingConfigurationFileFailure() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            Ginnungagap.main(UUID.randomUUID().toString());
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
}
