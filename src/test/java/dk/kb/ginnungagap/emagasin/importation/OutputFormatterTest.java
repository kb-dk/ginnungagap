package dk.kb.ginnungagap.emagasin.importation;

import java.io.IOException;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import junit.framework.Assert;

public class OutputFormatterTest extends ExtendedTestCase {

    String CATALOG_NAME = "catalog";
    String ARC_FILENAME = "ArcFileName";
    String ARC_RECORD_UUID = "ArcRecordUuid";
    String CUMULUS_RECORD_UUID = "CumulusRecordUuid";
    RecordUUIDs ruuid;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        ruuid = new RecordUUIDs(CATALOG_NAME, ARC_FILENAME, ARC_RECORD_UUID, CUMULUS_RECORD_UUID);
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testOutputFormatter() throws IOException {
        OutputFormatter of = new OutputFormatter(TestFileUtils.getTempDir());
        
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.succesFile), 1);
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.failureFile), 1);
        
        of.writeSucces(ruuid);
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.succesFile), 2);
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.failureFile), 1);
        
        of.writeFailure(ruuid, "Testing");
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.succesFile), 2);
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.failureFile), 2);
        
        of.writeFailure(ARC_FILENAME, ARC_RECORD_UUID, "This must be a test");
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.succesFile), 2);
        Assert.assertEquals(TestFileUtils.numberOfLinesInFile(of.failureFile), 3);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testWritingSuccessWhenFileIsNotWritable() {
        OutputFormatter of = new OutputFormatter(TestFileUtils.getTempDir());
        try {
            Assert.assertTrue(of.succesFile.setWritable(false));
            of.writeSucces(ruuid);
        } finally {
            of.succesFile.setExecutable(true);
            of.succesFile.setWritable(true);
            of.succesFile.setReadable(true);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testWritingRecordFailureWhenFileIsNotWritable() {
        OutputFormatter of = new OutputFormatter(TestFileUtils.getTempDir());
        try {
            Assert.assertTrue(of.failureFile.setWritable(false));
            of.writeFailure(ruuid, "Testing failure");
        } finally {
            of.failureFile.setExecutable(true);
            of.failureFile.setWritable(true);
            of.failureFile.setReadable(true);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testWritingGenericFailureWhenFileIsNotWritable() {
        OutputFormatter of = new OutputFormatter(TestFileUtils.getTempDir());
        try {
            Assert.assertTrue(of.failureFile.setWritable(false));
            of.writeFailure(ARC_FILENAME, ARC_RECORD_UUID, "Testing failure");
        } finally {
            of.failureFile.setExecutable(true);
            of.failureFile.setWritable(true);
            of.failureFile.setReadable(true);
        }
    }
}
