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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.exception.ArgumentCheck;
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
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testMovingFileFailureNoFromFile() throws IOException, InterruptedException {
        addDescription("Try moving a file when the from-file does not exist.");
        File from = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        File to = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        FileUtils.moveOrOverrideFile(from, to);        
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMovingFileFailureCannotWriteToFile() throws IOException, InterruptedException {
        addDescription("Try moving a file when the to-file destination cannot be written to.");
        File from = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        
        File toDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        try {
            toDir.setWritable(false);
            File to = new File(toDir, UUID.randomUUID().toString());
            FileUtils.moveOrOverrideFile(from, to);        
        } finally {
            toDir.setWritable(true);
        }
    }
    
    @Test
    public void testGetNewFile() {
        addDescription("Test the getNewFile method when failure.");
        File newFile = FileUtils.getNewFile(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        Assert.assertNotNull(newFile);
        Assert.assertTrue(newFile.isFile());
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetNewFileFailureFileAlreadyExists() throws IOException {
        addDescription("Test the getNewFile method when the file already exists");
        File newFile = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        FileUtils.getNewFile(newFile.getParentFile(), newFile.getName());
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetNewFileFailureCannotWriteToDir() throws IOException {
        addDescription("Test the getNewFile method when the file already exists");
        File dir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        try {
            dir.setWritable(false);
            FileUtils.getNewFile(dir, UUID.randomUUID().toString());
        } finally {
            dir.setWritable(true);
        }
    }
    
    @Test
    public void testDeleteFileSuccess() throws Exception {
        addDescription("Test deleting a file successfully.");
        File f = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        Assert.assertTrue(f.exists());
        Assert.assertTrue(f.isFile());
        
        FileUtils.deleteFile(f);

        Assert.assertFalse(f.exists());
        Assert.assertFalse(f.isFile());
    }

    @Test
    public void testDeleteFileOnNonExistingFile() throws Exception {
        addDescription("Test deleting a file which does not exist.");
        File f = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        Assert.assertFalse(f.exists());
        Assert.assertFalse(f.isFile());
        
        FileUtils.deleteFile(f);

        Assert.assertFalse(f.exists());
        Assert.assertFalse(f.isFile());
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testDeleteFileFailure() throws Exception {
        addDescription("Test deleting a file which are not allowed to be deleted.");
        File f = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        Assert.assertTrue(f.exists());
        Assert.assertTrue(f.isFile());
        
        try {
            f.getParentFile().setWritable(false);
            FileUtils.deleteFile(f);
        } finally {
            f.getParentFile().setWritable(true);
            Assert.assertTrue(f.delete());
        }
    }
}
