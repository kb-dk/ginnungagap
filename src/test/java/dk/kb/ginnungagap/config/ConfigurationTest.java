package dk.kb.ginnungagap.config;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.yggdrasil.utils.YamlTools;

public class ConfigurationTest extends ExtendedTestCase {

    File confFile;
    File requiredFieldsFile;
    
    @BeforeClass
    public void setup() {
        confFile = new File("src/test/resources/conf/ginnungagap.yml");
        requiredFieldsFile = new File("src/test/resources/conf/required_fields.yml");
    }
    
    @Test
    public void testReadingConfigurationFile() throws Exception {
        assertTrue(confFile.isFile());
        assertTrue(requiredFieldsFile.isFile());
        LinkedHashMap<String, LinkedHashMap> confMap = YamlTools.loadYamlSettings(confFile);
        printMap((LinkedHashMap<String, Object>) confMap.get("ginnungagap"), "  ");
        
        LinkedHashMap<String, LinkedHashMap> rfMap = YamlTools.loadYamlSettings(requiredFieldsFile);
        printMap((LinkedHashMap<String, Object>) rfMap.get("required_fields"), "  ");
    }
    
    protected void printMap(LinkedHashMap<String, Object> map, String prefix) {
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            System.err.print(prefix);
            if(entry.getValue() instanceof LinkedHashMap) {
                System.err.println(entry.getKey() + ":");
                printMap((LinkedHashMap<String, Object>) entry.getValue(), prefix + "  ");
            } else {
                System.err.println(entry.getKey() + " : " + entry.getValue().getClass().getSimpleName() + " -> " + entry.getValue().toString());
            }
        }
    }
    
    @Test
    public void testConfiguration() throws Exception {
        addDescription("Load the configuration");
        
    }
}
