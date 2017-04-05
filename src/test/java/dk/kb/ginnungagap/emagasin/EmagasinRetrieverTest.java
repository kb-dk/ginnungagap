package dk.kb.ginnungagap.emagasin;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;

public class EmagasinRetrieverTest extends ExtendedTestCase {

    File testScript;

    @BeforeClass
    public void setupClass() {
        TestFileUtils.setup();
        testScript = new File("src/test/resources/scripts/test_import_get_file.sh");
    }

    @BeforeMethod
    public void setup() {
        TestFileUtils.cleanUp();
    }

    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }

    @Test
    public void testExtractingOneTestArcFile() {
        EmagasinRetriever retriever = new EmagasinRetriever(testScript, TestFileUtils.getTempDir());
        String testArcFilename = UUID.randomUUID().toString();
        retriever.extractArcFile(testArcFilename);

        Assert.assertEquals(TestFileUtils.getTempDir().list().length, 1);
    }

    @Test
    public void testExtractingManyTestArcFile() {
        int max = 1000;
        EmagasinRetriever retriever = new EmagasinRetriever(testScript, TestFileUtils.getTempDir());
        for(int i = 0; i < max; i++) {
            String testArcFilename = UUID.randomUUID().toString();
            retriever.extractArcFile(testArcFilename);
        }

        Assert.assertEquals(TestFileUtils.getTempDir().list().length, max);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExtractingSameFileTwice() {
        String testArcFilename = UUID.randomUUID().toString();
        EmagasinRetriever retriever = new EmagasinRetriever(testScript, TestFileUtils.getTempDir());
        retriever.extractArcFile(testArcFilename);

        // Must break here!
        retriever.extractArcFile(testArcFilename);
    }
//
//    @Test(expectedExceptions = NotImplementedException.class)
//    public void testNotImplementedMethod() {
//        String testArcFilename = UUID.randomUUID().toString();
//        String uuid = UUID.randomUUID().toString();
//        EmagasinRetriever retriever = new EmagasinRetriever(testScript, TestFileUtils.getTempDir());
//        retriever.extractArcRecord(testArcFilename, uuid);
//    }
}
