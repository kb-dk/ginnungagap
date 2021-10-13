package dk.kb.ginnungagap.config;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.utils.YamlTools;
import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.*;

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
    
    @Test
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
        Configuration conf = new Configuration(confFileWithoutImport.getAbsolutePath());
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
        assertFalse(conf.getCumulusConf().getCatalogs().isEmpty());

        assertNotNull(conf.getWorkflowConf());
        assertNotNull(conf.getWorkflowConf().getInterval());
        
        assertNotNull(conf.getTransformationConf());
        assertNotNull(conf.getTransformationConf().getXsdDir());
        assertNotNull(conf.getTransformationConf().getXsltDir());
        assertNotNull(conf.getTransformationConf().getRequiredFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getBaseFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getWritableFields());
        assertNotNull(conf.getTransformationConf().getMetadataTempDir());
        assertTrue(conf.getTransformationConf().getMetadataTempDir().isDirectory());
        
        assertNotNull(conf.getLocalConfiguration());
        assertNotNull(conf.getLocalConfiguration().getLocalOutputDir());
        assertNotNull(conf.getLocalConfiguration().getLocalArchiveDir());
        assertTrue(conf.getLocalConfiguration().getIsTest());
    }
    
    @Test
    public void testConfigurationWithImport() throws Exception {
        addDescription("Load the configuration");
        Configuration conf = new Configuration(confFileWithImport.getAbsolutePath());
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
        assertFalse(conf.getCumulusConf().getCatalogs().isEmpty());

        assertNotNull(conf.getWorkflowConf());
        assertNotNull(conf.getWorkflowConf().getInterval());
        
        assertNotNull(conf.getTransformationConf());
        assertNotNull(conf.getTransformationConf().getXsdDir());
        assertNotNull(conf.getTransformationConf().getXsltDir());
        assertNotNull(conf.getTransformationConf().getRequiredFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getBaseFields());
        assertNotNull(conf.getTransformationConf().getRequiredFields().getWritableFields());

        assertNotNull(conf.getLocalConfiguration());
        assertNotNull(conf.getLocalConfiguration().getLocalOutputDir());
        assertNotNull(conf.getLocalConfiguration().getLocalArchiveDir());
        assertTrue(conf.getLocalConfiguration().getIsTest());
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testConfigurationFailure() throws Exception {
        addDescription("Load a missing file as configuration.");
        new Configuration("src/test/resources/test-resource.txt");
    }
    
    @Test
    public void testLoadingBitmagConfigurationWithKeyFile() throws Exception {
        addDescription("Test loading the bitmag configuration with the key file.");
        Configuration conf = new Configuration(confFileWithoutImport.getAbsolutePath());
        
        Map<String, Object> map = (Map<String, Object>) ((Map<String, Map>) YamlTools.loadYamlSettings(confFileWithoutImport).get(Configuration.CONF_GINNUNGAGAP)).get(Configuration.CONF_BITREPOSITORY);
        map.put(Configuration.CONF_BITREPOSITORY_KEYFILE, requiredFieldsFile.getPath());
        BitmagConfiguration bc = conf.loadBitmagConf(map);
        assertEquals(bc.getPrivateKeyFile(), requiredFieldsFile);
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testImproperBitrepositoryAlgorithm() throws Exception {
        addDescription("Test loading the bitmag configuration with the key file.");
        Configuration conf = new Configuration(confFileWithoutImport.getAbsolutePath());
        
        Map<String, Object> map = (Map<String, Object>) ((Map<String, Map>) YamlTools.loadYamlSettings(confFileWithoutImport).get(Configuration.CONF_GINNUNGAGAP)).get(Configuration.CONF_BITREPOSITORY);
        map.put(Configuration.CONF_BITREPOSITORY_ALGORITHM, "THIS IS NOT A VALID ALGORITHM");
        conf.loadBitmagConf(map);
    }
    
    @Test
    public void testLoadLocalConfiguration() throws Exception {
        addDescription("Test the loadLocalConfiguration method");
        Configuration conf = new Configuration(confFileWithoutImport.getAbsolutePath());
        
        addStep("Have the test field and set to true", "isTest is true");
        Map<String, Object> localMap = new HashMap<String, Object>();
        localMap.put(Configuration.CONF_LOCAL_ARCHIVE_PATH, conf.getLocalConfiguration().getLocalArchiveDir().getAbsolutePath());
        localMap.put(Configuration.CONF_LOCAL_OUTPUT_PATH, conf.getLocalConfiguration().getLocalOutputDir().getAbsolutePath());
        localMap.put(Configuration.CONF_LOCAL_TEST, "true");
        
        LocalConfiguration localConf = conf.loadLocalConfiguration(localMap);
        Assert.assertTrue(localConf.getIsTest());

        
        addStep("Have the test field and set to false", "isTest is false");
        localMap.put(Configuration.CONF_LOCAL_TEST, "false");
        
        localConf = conf.loadLocalConfiguration(localMap);
        Assert.assertFalse(localConf.getIsTest());
        
        
        addStep("Test without the test field", "isTest should default to false");
        localMap.remove(Configuration.CONF_LOCAL_TEST);
        
        localConf = conf.loadLocalConfiguration(localMap);
        Assert.assertFalse(localConf.getIsTest());
    }
    
    @Test
    public void testViewableCumulusConfiguration() {
        addDescription("Test the ViewableCumulusConfiguration. Should not be able to retrieve the password");
        Configuration conf = new Configuration(confFileWithoutImport.getAbsolutePath());
        
        ViewableCumulusConfiguration cumulusConf = conf.getViewableCumulusConfiguration();
        Assert.assertEquals(cumulusConf.getServerUrl(), conf.getCumulusConf().getServerUrl());
        Assert.assertEquals(cumulusConf.getUserName(), conf.getCumulusConf().getUserName());
        Assert.assertEquals(cumulusConf.getCatalogs(), conf.getCumulusConf().getCatalogs());
        
        Assert.assertFalse(cumulusConf.getUserPassword().equalsIgnoreCase(conf.getCumulusConf().getUserPassword()));
        Assert.assertEquals(cumulusConf.getUserPassword(), ViewableCumulusConfiguration.VIEWABLE_PASSWORD);
    }
}
