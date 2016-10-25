package dk.kb.ginnungagap.testutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.bitrepository.common.utils.FileUtils;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.TestConfiguration;

public class TestFileUtils {

    protected static File tempDir = new File("tempDir");
    
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
    
    public static File createFileWithContent(String content) throws IOException {
        File res = new File(getTempDir(), UUID.randomUUID().toString());
        try (OutputStream os = new FileOutputStream(res);) {
            os.write(content.getBytes());
            os.flush();
        }
        
        return res;
    }
    
    public static TestConfiguration createTempConf() {
        File confDir = FileUtils.retrieveSubDirectory(tempDir, "conf");
        for(File f : new File("src/test/resources/conf/").listFiles()) {
            FileUtils.copyFile(f, new File(confDir, f.getName()));
        }
        
        FileUtils.retrieveSubDirectory(confDir, "bitrepository");
        File scriptDir = FileUtils.retrieveSubDirectory(tempDir, "scripts");
        FileUtils.retrieveSubDirectory(scriptDir, "xsd");
        FileUtils.retrieveSubDirectory(scriptDir, "xslt");

        return new TestConfiguration(new File(confDir, "ginnungagap.yml"));
    }
}
