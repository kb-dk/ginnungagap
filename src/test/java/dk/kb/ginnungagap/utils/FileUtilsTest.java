package dk.kb.ginnungagap.utils;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import static org.testng.Assert.assertFalse;

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
        addDescription("Test getting ");
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
}
