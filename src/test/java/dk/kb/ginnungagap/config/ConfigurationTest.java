package dk.kb.ginnungagap.config;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import dk.kb.yggdrasil.utils.YamlTools;

public class ConfigurationTest extends ExtendedTestCase {

    @Test
    public void testConfiguration() throws Exception {
        File confFile = new File("src/main/conf/ginnungagap.yml");
        assertTrue(confFile.isFile());
        LinkedHashMap<String, LinkedHashMap> map = YamlTools.loadYamlSettings(confFile);
        printMap((LinkedHashMap<String, Object>) map.get("ginnungagap"), "  ");
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
}
