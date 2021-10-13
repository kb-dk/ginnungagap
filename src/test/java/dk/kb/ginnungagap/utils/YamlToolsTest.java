package dk.kb.ginnungagap.utils;

import dk.kb.ginnungagap.exception.YamlException;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Tests for the methods in the YamlTools class.
 *
 */
public class YamlToolsTest extends ExtendedTestCase {

    public static String YAML_TEST_FILE = "src/test/resources/conf/ginnungagap.yml";
    public static String NOT_YAML_TEST_FILE = "src/test/resources/conf/does_not_exist.yml";
    public static String NOT_YAML_TEST_FILE2 = "src/test/resources/test-resource.txt";

    @Test(expectedExceptions = YamlException.class)
    public void testReadYamlFailed() throws Exception {
        File f = new File(NOT_YAML_TEST_FILE);
        YamlTools.loadYamlSettings(f);
    }

    @Test(expectedExceptions = YamlException.class)
    public void testReadNonYamlFile() throws Exception {
        File f = new File(NOT_YAML_TEST_FILE2);
        YamlTools.loadYamlSettings(f);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testReadYamlFile() throws Exception {
        File f = new File(YAML_TEST_FILE);

        LinkedHashMap m = YamlTools.loadYamlSettings(f);
        Assert.assertNotNull(m);
    }

}
