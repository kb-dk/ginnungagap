package dk.kb.ginnungagap;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;

public class GinnungapTest extends ExtendedTestCase {

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
    
    @Test(enabled = false)
    public void testGinnungagap() {
        Ginnungagap.main("src/test/resources/conf/ginnungagap.yml", "bitmag");
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            Ginnungagap.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testInvalidArchiveType() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            Ginnungagap.main(testConf.getAbsolutePath(), "THIS_IS_NOT_A_VALID_ARCHIVE");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testMissingConfigurationFileFailure() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            Ginnungagap.main(UUID.randomUUID().toString());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
}
