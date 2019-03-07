package dk.kb.ginnungagap.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import org.bitrepository.common.utils.FileUtils;

import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.utils.StreamUtils;

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
    
    public static void cleanUp() {
        tearDown();
        setup();
    }
    
    public static void delete(File fileToDelete) {
        if(fileToDelete.exists()) {
            FileUtils.delete(fileToDelete);
        }
    }
    
    public static File getTempDir() {
        return tempDir;
    }
    
    public static File createFileWithContent(String content) throws IOException {
        File res = new File(getTempDir(), UUID.randomUUID().toString());
        try (OutputStream os = new FileOutputStream(res)) {
            os.write(content.getBytes());
            os.flush();
        }
        
        return res;
    }
    
    public static File copyFileToTemp(File file) throws IOException {
        File res = new File(getTempDir(), file.getName());
        StreamUtils.copyInputStreamToOutputStream(new FileInputStream(file), new FileOutputStream(res));
        return res;
    }
    
    public static TestConfiguration createTempConf() {
        File confDir = FileUtils.retrieveSubDirectory(tempDir, "conf");
        for(File f : new File("src/test/resources/conf/").listFiles()) {
            FileUtils.copyFile(f, new File(confDir, f.getName()));
        }
        
        FileUtils.retrieveSubDirectory(confDir, "bitrepository");
        File scriptDir = FileUtils.retrieveSubDirectory(tempDir, "scripts");
        File xsdDir = FileUtils.retrieveSubDirectory(scriptDir, "xsd");
        for(File f : new File("src/main/resources/scripts/xsd/").listFiles()) {
            FileUtils.copyFile(f, new File(xsdDir, f.getName()));
        }
        File xsltDir = FileUtils.retrieveSubDirectory(scriptDir, "xslt");
        for(File f : new File("src/main/resources/scripts/xslt/").listFiles()) {
            FileUtils.copyFile(f, new File(xsltDir, f.getName()));
        }

        return new TestConfiguration(new File(confDir, "ginnungagap.yml").getAbsolutePath());
    }
    
    public static int numberOfLinesInFile(File f) throws IOException {
        int res = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
            String line;
            while((line = br.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    res++;
                }
            }
        }
        return res;
    }
}
