package dk.kb.ginnungagap;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;

public class GinnungapTest extends ExtendedTestCase {

    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        FileUtils.copyDirectory(new File("src/main/resources/scripts/xslt"), new File(TestFileUtils.getTempDir(), "scripts/xslt"));
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test(enabled = false)
    public void testGinnungagap() {
        Ginnungagap.main("src/test/resources/conf/ginnungagap.yml", "bitmag");
    }
}
