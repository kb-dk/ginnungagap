package dk.kb.ginnungagap.utils;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;

public class FileUtilsTest extends ExtendedTestCase {

    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testConstructor() {
        addDescription("Test the File Utils constructor.");
        FileUtils fu = new FileUtils();
        assertNotNull(fu);
    }
    
    @Test
    public void testGetDirectoryOnExistingDirectory() {
        addDescription("Test getting a directory, which already exists");
        File dir = FileUtils.getDirectory(TestFileUtils.getTempDir().getAbsolutePath());
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }
    
    @Test
    public void testGetDirectoryOnDirectory() {
        addDescription("Test getting a directory");
        File f = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        assertFalse(f.exists());
        File dir = FileUtils.getDirectory(f.getAbsolutePath());
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetDirectoryOnNotWritableParentDirectory() {
        addDescription("Test getting a sub-directory from a non-writable directory");
        File parentDir = TestFileUtils.getTempDir();
        try {
            parentDir.setWritable(false);
            File f = new File(parentDir, UUID.randomUUID().toString());
            assertFalse(f.exists());
            FileUtils.getDirectory(f.getAbsolutePath());
        } finally {
            parentDir.setWritable(true);
        }
    }
    
    @Test
    public void testMovingFile() throws IOException, InterruptedException {
        addDescription("Try moving a file");
        File from = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        long size1 = from.length();
        File to = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        FileUtils.moveOrOverrideFile(from, to);
        
        assertEquals(to.length(), size1);
        
        from = TestFileUtils.createFileWithContent(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
        long size2 = from.length();
        assertNotEquals(size1, size2);
        
        FileUtils.moveOrOverrideFile(from, to);
        
        assertNotEquals(to.length(), size1);
        assertEquals(to.length(), size2);
    }
}
