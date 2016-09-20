package dk.kb.ginnungagap.testutils;

import java.io.File;

import org.bitrepository.common.utils.FileUtils;

public class TestFileUtils {

    protected static File tempDir = new File("temp-dir");
    
    public static void setup() {
        if(tempDir.exists()) {
            FileUtils.delete(tempDir);
        }
        tempDir.mkdirs();
    }
    
    public static void tearDown() {
        if(tempDir.exists()) {
            FileUtils.delete(tempDir);
        }
    }
    
    public static File getTempDir() {
        return tempDir;
    }
}
