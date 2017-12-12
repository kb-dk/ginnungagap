package dk.kb.ginnungagap;

import java.io.File;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.yggdrasil.exceptions.ArgumentCheck;

public class AbstractMainTest extends ExtendedTestCase {

    TestConfiguration conf;
    
    @BeforeClass
    public void setupClass() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }

    @AfterClass
    public void teardownClass() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testIsYesSuccessCasesNull() {
        Assert.assertFalse(AbstractMain.isYes(null));
    }
    
    @Test
    public void testIsYesSuccessCasesEmpty() {
        Assert.assertFalse(AbstractMain.isYes(""));
    }
    
    @Test
    public void testIsYesSuccessCasesYLowercase() {
        Assert.assertTrue(AbstractMain.isYes("yas"));
    }
    
    @Test
    public void testIsYesSuccessCasesYUppercase() {
        Assert.assertTrue(AbstractMain.isYes("Yep"));
    }
    
    @Test
    public void testIsYesSuccessCasesNLowercase() {
        Assert.assertFalse(AbstractMain.isYes("nein"));
    }
    
    @Test
    public void testIsYesSuccessCasesNUppercase() {
        Assert.assertFalse(AbstractMain.isYes("Nope"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testIsYesFailure() {
        Assert.assertFalse(AbstractMain.isYes("THIS IS NOT A YES OR NO"));
    }
    
    @Test
    public void testInstantiateTransformationFileSuccess() {
        AbstractMain.instantiateTransformationHandler(conf, AbstractMain.TRANSFORMATION_SCRIPT_FOR_METS);
        AbstractMain.instantiateTransformationHandler(conf, AbstractMain.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION);
        AbstractMain.instantiateTransformationHandler(conf, AbstractMain.TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInstantiateTransformationFileFailure() {
        AbstractMain.instantiateTransformationHandler(conf, "THIS IS NOT A VALID SCRIPT NAME");
    }
    
    @Test
    public void testInstantiateConfigurationSuccess() {
        File conf = new File(TestFileUtils.getTempDir(), "conf/ginnungagap.yml");
        AbstractMain.instantiateConfiguration(conf.getAbsolutePath());
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInstantiateConfigurationFailureNull() {
        AbstractMain.instantiateConfiguration(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInstantiateConfigurationFailureEmpty() {
        AbstractMain.instantiateConfiguration("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInstantiateConfigurationFailureMissing() {
        AbstractMain.instantiateConfiguration("tempDir/NOT_A_FILE" + UUID.randomUUID().toString());
    }
    
    @Test
    public void testInstantiateArchiveTypeLocal() {
        AbstractMain.instantiateArchive("local", conf);
        try {
            new File("archive").delete();
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testInstantiateArchiveTypeBitmag() {
        // Use '-1' as number of failing pillars to trigger 'Yggdrasil argument check' error
        BitmagConfiguration bmConf = new BitmagConfiguration(TestFileUtils.getTempDir(), null, -1, 0, TestFileUtils.getTempDir(), "MD5");
        conf.setBitmagConfiguration(bmConf);
        AbstractMain.instantiateArchive("bitmag", conf);
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testInstantiateArchiveTypeNull() {
        // Use '-1' as number of failing pillars to trigger 'Yggdrasil argument check' error
        BitmagConfiguration bmConf = new BitmagConfiguration(TestFileUtils.getTempDir(), null, -1, 0, TestFileUtils.getTempDir(), "MD5");
        conf.setBitmagConfiguration(bmConf);
        AbstractMain.instantiateArchive(null, conf);
    }

    @Test(expectedExceptions = ArgumentCheck.class)
    public void testInstantiateArchiveTypeEmpty() {
        // Use '-1' as number of failing pillars to trigger 'Yggdrasil argument check' error
        BitmagConfiguration bmConf = new BitmagConfiguration(TestFileUtils.getTempDir(), null, -1, 0, TestFileUtils.getTempDir(), "MD5");
        conf.setBitmagConfiguration(bmConf);
        AbstractMain.instantiateArchive("", conf);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInstantiateArchiveFailure() {
        AbstractMain.instantiateArchive("THIS IS NOT A VALID ARCHIVE TYPE", conf);
    }
    
    @Test
    public void testInstantiation() {
        AbstractMain main = new AbstractMain() {};
        Assert.assertNotNull(main);
        Assert.assertTrue(main instanceof AbstractMain);
    }
}
