package dk.kb.ginnungagap.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.yggdrasil.utils.YamlTools;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfigurationTest extends ExtendedTestCase {

    File confFileWithoutImport;
    File confFileWithImport;
    File requiredFieldsFile;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        
        File origConfFileWithoutImport = new File("src/test/resources/conf/ginnungagap_without_import.yml");
        File origConfFileWithImport = new File("src/test/resources/conf/ginnungagap.yml");
        requiredFieldsFile = new File("src/test/resources/conf/required_fields.yml");

        confFileWithoutImport = new File(TestFileUtils.getTempDir(), origConfFileWithoutImport.getName());
        FileUtils.copyFile(origConfFileWithoutImport, confFileWithoutImport);
        
        confFileWithImport = new File(TestFileUtils.getTempDir(), origConfFileWithImport.getName());
        FileUtils.copyFile(origConfFileWithImport, confFileWithImport);
        
        File bitmagConfDir = new File(TestFileUtils.getTempDir(), "conf/bitrepository");
        bitmagConfDir.mkdirs();
        assertTrue(bitmagConfDir.isDirectory());
        
        File xsltConfDir = new File(TestFileUtils.getTempDir(), "scripts/xslt");
        xsltConfDir.mkdirs();
        assertTrue(xsltConfDir.isDirectory());
        
        File xsdConfDir = new File(TestFileUtils.getTempDir(), "scripts/xsd");
        xsdConfDir.mkdirs();
        assertTrue(xsdConfDir.isDirectory());
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test//(enabled = false)
    public void testReadingConfigurationFile() throws Exception {
        assertTrue(confFileWithoutImport.isFile());
        assertTrue(requiredFieldsFile.isFile());
        LinkedHashMap<String, LinkedHashMap> confMap = YamlTools.loadYamlSettings(confFileWithImport);
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
    public void testConfigurationWithoutImport() throws Exception {
        addDescription("Load the configuration");
        Configuration conf = new Configuration(confFileWithoutImport);
        assertNotNull(conf.getBitmagConf());
        assertNotNull(conf.getBitmagConf().getComponentId());
        assertNotNull(conf.getBitmagConf().getMaxNumberOfFailingPillars());
        assertNull(conf.getBitmagConf().getPrivateKeyFile());
        assertNotNull(conf.getBitmagConf().getSettingsDir());
        assertNotNull(conf.getBitmagConf().getTempDir());
        assertNotNull(conf.getBitmagConf().getWarcFileSizeLimit());

        assertNotNull(conf.getCumulusConf());
        assertNotNull(conf.getCumulusConf().getServerUrl());
        assertNotNull(conf.getCumulusConf().getUserName());
        assertNotNull(conf.getCumulusConf().getUserPassword());
        assertNotNull(conf.getCumulusConf().getWriteAccess());
        assertNotNull(conf.getCumulusConf().getCatalogs());

        assertNotNull(conf.getTransformationConf());
        assertNotNull(conf.getTransformationConf().getXsdDir());
        assertNotNull(conf.getTransformationConf().getXsltDir());
        assertNotNull(conf.getTransformationConf().getRequiredFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getBaseFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getWritableFields());
        
        assertNull(conf.getImportationConfiguration());
    }
    
    @Test
    public void testConfigurationWithImport() throws Exception {
        addDescription("Load the configuration");
        Configuration conf = new Configuration(confFileWithImport);
        assertNotNull(conf.getBitmagConf());
        assertNotNull(conf.getBitmagConf().getComponentId());
        assertNotNull(conf.getBitmagConf().getMaxNumberOfFailingPillars());
        assertNull(conf.getBitmagConf().getPrivateKeyFile());
        assertNotNull(conf.getBitmagConf().getSettingsDir());
        assertNotNull(conf.getBitmagConf().getTempDir());
        assertNotNull(conf.getBitmagConf().getWarcFileSizeLimit());

        assertNotNull(conf.getCumulusConf());
        assertNotNull(conf.getCumulusConf().getServerUrl());
        assertNotNull(conf.getCumulusConf().getUserName());
        assertNotNull(conf.getCumulusConf().getUserPassword());
        assertNotNull(conf.getCumulusConf().getWriteAccess());
        assertNotNull(conf.getCumulusConf().getCatalogs());

        assertNotNull(conf.getTransformationConf());
        assertNotNull(conf.getTransformationConf().getXsdDir());
        assertNotNull(conf.getTransformationConf().getXsltDir());
        assertNotNull(conf.getTransformationConf().getRequiredFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getBaseFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getWritableFields());
        
        assertNotNull(conf.getImportationConfiguration());
        assertNotNull(conf.getImportationConfiguration().getScriptFile());
        assertNotNull(conf.getImportationConfiguration().getTempDir());
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testConfigurationFailure() throws Exception {
        addDescription("Load a missing file as configuration.");
        new Configuration(new File("src/test/resources/test-resource.txt"));
    }
    
    @Test
    public void testLoadingBitmagConfigurationWithKeyFile() throws Exception {
        addDescription("Test loading the bitmag configuration with the key file.");
        Configuration conf = new Configuration(confFileWithoutImport);
        
        Map<String, Object> map = (Map<String, Object>) ((Map<String, Map>) YamlTools.loadYamlSettings(confFileWithoutImport).get(Configuration.CONF_GINNUNGAGAP)).get(Configuration.CONF_BITREPOSITORY);
        map.put(Configuration.CONF_BITREPOSITORY_KEYFILE, requiredFieldsFile.getPath());
        BitmagConfiguration bc = conf.loadBitmagConf(map);
        assertEquals(bc.getPrivateKeyFile(), requiredFieldsFile);
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testImproperBitrepositoryAlgorithm() throws Exception {
        addDescription("Test loading the bitmag configuration with the key file.");
        Configuration conf = new Configuration(confFileWithoutImport);
        
        Map<String, Object> map = (Map<String, Object>) ((Map<String, Map>) YamlTools.loadYamlSettings(confFileWithoutImport).get(Configuration.CONF_GINNUNGAGAP)).get(Configuration.CONF_BITREPOSITORY);
        map.put(Configuration.CONF_BITREPOSITORY_ALGORITHM, "THIS IS NOT A VALID ALGORITHM");
        conf.loadBitmagConf(map);
    }
}
