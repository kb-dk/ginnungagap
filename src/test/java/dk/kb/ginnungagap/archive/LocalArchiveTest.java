package dk.kb.ginnungagap.archive;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;

public class LocalArchiveTest extends ExtendedTestCase {
    
    String collectionId = UUID.randomUUID().toString();
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();

    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testShutdown() {
        File archiveBaseDir = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        Archive archive = new LocalArchive(archiveBaseDir.getAbsolutePath());
        archive.shutdown();
    }

    @Test
    public void testUploadFile() throws IOException {
        File archiveBaseDir = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        Archive archive = new LocalArchive(archiveBaseDir.getAbsolutePath());

        File testFile = TestFileUtils.copyFileToTemp(new File("src/test/resources/warc/warcexample.warc"));
        
        Assert.assertTrue(testFile.exists());
        archive.uploadFile(testFile, collectionId);
        Assert.assertFalse(testFile.exists());
        Assert.assertTrue(new File(new File(archiveBaseDir, collectionId), testFile.getName()).exists()); 
    }
    
    @Test
    public void testGetFile() {
        File archiveBaseDir = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        Archive archive = new LocalArchive(archiveBaseDir.getAbsolutePath());

        archive.getFile(UUID.randomUUID().toString(), collectionId);
    }
    
    @Test
    public void testGetChecksum() throws Exception {
        File archiveBaseDir = FileUtils.retrieveSubDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        File collectionDir = FileUtils.retrieveSubDirectory(archiveBaseDir, collectionId);
        Archive archive = new LocalArchive(archiveBaseDir.getAbsolutePath());
        
        String id = UUID.randomUUID().toString();
        String expectedChecksum = "37e9a7db97d6050911038d72b0f0585c";
        
        File src = new File("src/test/resources/test-resource.txt");
        File dest = new File(collectionDir, id);
        FileUtils.copyFile(src, dest);
        
        Assert.assertTrue(dest.isFile());
        
        String checksum = archive.getChecksum(id, collectionId);
        Assert.assertEquals(checksum, expectedChecksum);
    }
}
