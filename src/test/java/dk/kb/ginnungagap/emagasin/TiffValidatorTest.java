package dk.kb.ginnungagap.emagasin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.utils.FileUtils;
import junit.framework.Assert;

public class TiffValidatorTest extends ExtendedTestCase {

    File outputDir;
    File tiffFile;
    File scriptFile;
    File confFile;

    @BeforeClass
    public void setupClass() throws IOException {
        TestFileUtils.setup();
        tiffFile = TestFileUtils.copyFileToTemp(new File("src/test/resources/validation/minimal_valid_baseline.tiff"));
        scriptFile = TestFileUtils.copyFileToTemp(new File("src/main/resources/bin/run_checkit_tiff.sh"));
        confFile = TestFileUtils.copyFileToTemp(new File("src/main/resources/conf/cit_tiff.cfg"));
    }

    @BeforeMethod
    public void setupMethod() throws IOException {
        outputDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
    }

//    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }

    @Test
    public void testTiffFile() throws IOException {
        Assert.assertTrue(tiffFile.isFile());
        Assert.assertTrue(scriptFile.isFile());
        Assert.assertTrue(confFile.isFile());

        TiffValidator validator = new TiffValidator(outputDir, scriptFile, confFile, false);
        validator.validateTiffFile(tiffFile);
    }

    @Test
    public void testValidatingArcFile() throws IOException {
        File testFile = new File("/home/jolf/data/KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc");
        if(!testFile.exists()) {
            throw new SkipException("Ignore test, since file '" + testFile.getAbsolutePath() + "' does not exist");
        }

        TiffValidator validator = new TiffValidator(outputDir, scriptFile, confFile, false);
        validator.validateTiffRecordsInArcFile(testFile);
    }
}
