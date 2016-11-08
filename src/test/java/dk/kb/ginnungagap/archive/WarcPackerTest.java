package dk.kb.ginnungagap.archive;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.jwat.common.ContentType;
import org.jwat.common.Uri;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class WarcPackerTest extends ExtendedTestCase {

    File testFile;
    BitmagConfiguration conf;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        
        File origTestFile = new File("src/test/resources/test-resource.txt");
        testFile = new File(TestFileUtils.getTempDir(), origTestFile.getName());
        FileUtils.copyFile(origTestFile, testFile);
        
        conf = new BitmagConfiguration(TestFileUtils.getTempDir(), null, 1, 10000000, TestFileUtils.getTempDir(), "SHA-1");
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testPackaging() throws Exception {
        addDescription("Test succes case");
        
        WarcPacker wp = new WarcPacker(conf);
        Uri uri = wp.packResource(testFile, ContentType.parseContentType("application/octetstream"), UUID.randomUUID().toString());
        wp.packMetadata(testFile, uri);
        
        assertTrue(wp.getSize() > 0);
        assertTrue(wp.getSize() > 2 * testFile.length()); 

    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailedInstantiationDueToNoWriteAccess() throws Exception {
        addDescription("Test failure to instantiate the warc packer, due to missing write access to the folder.");
        
        try {
            TestFileUtils.getTempDir().setWritable(false);
            new WarcPacker(conf);
        } finally {
            TestFileUtils.getTempDir().setWritable(true);            
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailureToWriteMissingFileAsMetadata() throws Exception {
        addDescription("Test failure to write a missing file as metadata");
        
        WarcPacker wp = new WarcPacker(conf);
        wp.packMetadata(new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()), new Uri("urn:uuid:" + UUID.randomUUID().toString()));
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailureToWriteMissingFileAsResource() throws Exception {
        addDescription("Test failure to write a missing file as resource");
        
        WarcPacker wp = new WarcPacker(conf);
        wp.packResource(new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()), ContentType.parseContentType("application/octetstream"), "THIS IS THE UUID");
    }
    
    @Test(expectedExceptions = IllegalStateException.class, enabled = false)
    public void testFailureToClose() throws Exception {
        WarcPacker wp = new WarcPacker(conf);
        assertTrue(wp.getWarcFile().delete());
        wp.close();
    }
}
