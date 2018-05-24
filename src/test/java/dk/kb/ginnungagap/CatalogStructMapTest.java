package dk.kb.ginnungagap;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;

public class CatalogStructMapTest extends ExtendedTestCase {

    File testConf;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        FileUtils.copyDirectory(new File("src/main/resources/scripts/xslt"), new File(TestFileUtils.getTempDir(), "scripts/xslt"));
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testOnlyOneArgument() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(testConf.getAbsolutePath());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testOnlyTwoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(testConf.getAbsolutePath(), "Audio");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNullArgumentForConfiguration() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(null, "Audio", "G");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testInvalidArchiveType() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(testConf.getAbsolutePath(), "THIS_IS_NOT_A_VALID_ARCHIVE");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test
    public void testCheckConfigurationSuccess() {
        Configuration conf = AbstractMain.instantiateConfiguration(testConf.getAbsolutePath());
        CatalogStructmap.checkCatalogInConfiguration(conf, "Audio OM");
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCheckConfigurationFailure() {
        Configuration conf = AbstractMain.instantiateConfiguration(testConf.getAbsolutePath());
        CatalogStructmap.checkCatalogInConfiguration(conf, "THIS IS NOT A CUMULUS CATALOG");
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailingCumulusAccess() {
        CatalogStructmap.main(testConf.getAbsolutePath(), "Audio OM", "G", "local", "intellectual-entity");
    }
    
    @Test
    public void testInstantiation() {
        CatalogStructmap catalogStructmap = new CatalogStructmap();
        Assert.assertNotNull(catalogStructmap);
        Assert.assertTrue(catalogStructmap instanceof CatalogStructmap);
    }
}
