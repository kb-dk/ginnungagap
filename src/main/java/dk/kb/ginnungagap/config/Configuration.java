package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.utils.YamlTools;

/**
 * The configurations in YAML format.
 * It must be in the following format:
 * <ul>
 *   <li>ginnungagap</li>
 *   <ul>
 *     <li>bitrepository:</li>
 *     <ul>
 *       <li>settings_dir: $settings dir path</li>
 *       <li>keyfile: $keyfile path</li>
 *       <li>max_failing_pillars: $max_failing_pillars</li>
 *       <li>warc_size_limit: $warc_size_limit</li>
 *       <li>temp_dir: $temp_dir</li>
 *     </ul>
 *     <li>cumulus</li>
 *     <ul>
 *       <li>server_url: $server url</li>
 *       <li>username: $username</li>
 *       <li>password: $password</li>
 *     </ul>
 *     <li>transformation</li>
 *     <ul>
 *       <li>xsd_dir: $xsd_dir</li>
 *       <li>xslt_dir: $xslt_dir</li>
 *       <li>required_fields_file: $required_fields_file</li>
 *       <li>catalogs: <br/>- $catalog 1<br/>- $catalog 2<br/>- ...</li>
 *     </ul>
 *   </ul>
 * </ul>
 * 
 * Contains specific configurations for each part of the workflow.
 * 
 * Bitmag settings and cumulus settings.
 * 
 * The configuration file must be in the YAML format.
 */
@SuppressWarnings("unchecked")
public class Configuration {

    /** Ginnungagap root element.*/
    private static final String CONF_GINNUNGAGAP = "ginnungagap";
    
    /** The bitrepository node-element.*/
    private static final String CONF_BITREPOSITORY = "bitrepository";
    /** The bitrepository settings directory leaf-element.*/
    private static final String CONF_BITREPOSITORY_SETTINGS_DIR = "settings_dir";
    /** The bitrepository key file leaf-element.*/
    private static final String CONF_BITREPOSITORY_KEYFILE = "keyfile";
    /** The bitrepository max failing pillars leaf-element.*/
    private static final String CONF_BITREPOSITORY_MAX_FAILING_PILLARS = "max_failing_pillars";
    /** The bitrepository warc size limit leaf-element.*/
    private static final String CONF_BITREPOSITORY_WARC_SIZE_LIMIT = "warc_size_limit";
    /** The bitrepository warc size limit leaf-element.*/
    private static final String CONF_BITREPOSITORY_TEMP_DIR = "temp_dir";
    
    /** Cumulus node-element.*/
    private static final String CONF_CUMULUS = "cumulus";
    /** The cumulus server url leaf-element.*/
    private static final String CONF_CUMULUS_SERVER = "server_url";
    /** The cumulus server username leaf-element.*/
    private static final String CONF_CUMULUS_USERNAME = "username";
    /** The cumulus server password leaf-element.*/
    private static final String CONF_CUMULUS_PASSWORD = "password";
    
    /** Transformation node-element.*/
    private static final String CONF_TRANSFORMATION = "transformation";
    /** Transformation XSD directory leaf-element.*/
    private static final String CONF_TRANSFORMATION_XSD_DIR = "xsd_dir";
    /** Transformation XSLT directory leaf-element.*/
    private static final String CONF_TRANSFORMATION_XSLT_DIR = "xslt_dir";
    /** Transformation required fields file leaf-element.*/
    private static final String CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE = "required_fields_file";
    /** Transformation catalogs array leaf-element.*/
    private static final String CONF_TRANSFORMATION_CATALOGS = "catalogs";

    /** Whether Cumulus should have write access.
     * TODO: should be 'true', when used properly.*/
    private static final boolean CUMULUS_WRITE_ACCESS = true;
    
    /** The configuration for the bitrepository.*/
    private final BitmagConfiguration bitmagConf;
    /** The configruation for accessing Cumulus.*/
    private final CumulusConfiguration cumulusConf;
    /** The configuration for the transformation.*/
    private final TransformationConfiguration transformationConf;
    
    /**
     * Constructor.
     * @param confFile The file with the Ginnungagap configuration, in the described format.
     */
    @SuppressWarnings("rawtypes")
    public Configuration(File confFile) {
        ArgumentCheck.checkExistsNormalFile(confFile, "File confFile");
        try {
            LinkedHashMap<String, LinkedHashMap> map = YamlTools.loadYamlSettings(confFile);
            
            ArgumentCheck.checkTrue(map.containsKey(CONF_GINNUNGAGAP), 
                    "Configuration must contain the '" + CONF_GINNUNGAGAP + "' root element.");
            Map<String, Object> confMap = (Map<String, Object>) map.get(CONF_GINNUNGAGAP);

            ArgumentCheck.checkTrue(confMap.containsKey(CONF_CUMULUS), 
                    "Configuration must contain the '" + CONF_CUMULUS + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_BITREPOSITORY), 
                    "Configuration must contain the '" + CONF_BITREPOSITORY+ "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_TRANSFORMATION), 
                    "Configuration must contain the '" + CONF_TRANSFORMATION + "' element.");
            
            System.err.println(confMap.containsKey(CONF_BITREPOSITORY) + " -> " + confMap.get(CONF_BITREPOSITORY));
            this.bitmagConf = loadBitmagConf((Map<String, Object>) confMap.get(CONF_BITREPOSITORY));
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) confMap.get(CONF_CUMULUS));
            this.transformationConf = loadTransformationConfiguration((Map<String, Object>) confMap.get(CONF_TRANSFORMATION));
        } catch (Exception e) {
            throw new ArgumentCheck("Issue loading the configurations from file '" + confFile.getAbsolutePath() + "'", 
                    e);
            
        }
    }
    
    /**
     * Loads the Bitmag configuration for the bitrepository.
     * The elements are taken from the 'bitrepository' element in the configuration.
     * @param map The map of the Bitrepository configuration.
     * @return The configuration for the bitrepository.
     */
    protected BitmagConfiguration loadBitmagConf(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_SETTINGS_DIR), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_SETTINGS_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_KEYFILE), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_KEYFILE + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_MAX_FAILING_PILLARS), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_MAX_FAILING_PILLARS + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_WARC_SIZE_LIMIT), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_WARC_SIZE_LIMIT + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_TEMP_DIR), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_TEMP_DIR + "'");
        
        
        File settingsDir = new File((String) map.get(CONF_BITREPOSITORY_SETTINGS_DIR));
        ArgumentCheck.checkExistsDirectory(settingsDir, "Directory " + settingsDir.getAbsolutePath());
        
        File keyFile = null;
        String keyfilePath = (String) map.get(CONF_BITREPOSITORY_KEYFILE);
        if(!keyfilePath.isEmpty()) {
            keyFile = new File(keyfilePath);
        }
        
        int maxFailingPillars = (int) map.get(CONF_BITREPOSITORY_MAX_FAILING_PILLARS);
        int warcSizeLimit = (int) map.get(CONF_BITREPOSITORY_WARC_SIZE_LIMIT);
        
        String tempDirPath = (String) map.get(CONF_BITREPOSITORY_TEMP_DIR);
        File tempDir = FileUtils.getDirectory(tempDirPath);
        
        return new BitmagConfiguration(settingsDir, keyFile, maxFailingPillars, warcSizeLimit, tempDir);
    }
    
    /**
     * Loads the Cumulus configuration from the 'cumulus' element in the configuration.
     * @param map The map with the Cumulus configuration.
     * @return The configuration for the Cumulus server.
     */
    protected CumulusConfiguration loadCumulusConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_SERVER), 
                "Missing Cumulus element '" + CONF_CUMULUS_SERVER + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_USERNAME), 
                "Missing Cumulus element '" + CONF_CUMULUS_USERNAME + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_PASSWORD), 
                "Missing Cumulus element '" + CONF_CUMULUS_PASSWORD + "'");
        
        return new CumulusConfiguration(CUMULUS_WRITE_ACCESS, (String) map.get(CONF_CUMULUS_SERVER), 
                (String) map.get(CONF_CUMULUS_USERNAME), (String) map.get(CONF_CUMULUS_PASSWORD));
    }
    
    /**
     * Loads the Transformation configuration from the 'transformation' element in the configuration.
     * @param map The map with the transformation configuration.
     * @return The configuration for performing the transformation.
     * @throws YggdrasilException If loading the required fields file fails.
     */
    protected TransformationConfiguration loadTransformationConfiguration(Map<String, Object> map) throws YggdrasilException {
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_XSD_DIR), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_XSD_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_XSLT_DIR), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_XSLT_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_CATALOGS), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_CATALOGS + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE + "'");
        
        File xsdDir = new File((String) map.get(CONF_TRANSFORMATION_XSD_DIR));
        File xsltDir = new File((String) map.get(CONF_TRANSFORMATION_XSLT_DIR));
        File requiredFieldsFile = new File((String) map.get(CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE));
        
        ArgumentCheck.checkExistsDirectory(xsdDir, "XSD dir");
        ArgumentCheck.checkExistsDirectory(xsltDir, "XSLT dir");
        ArgumentCheck.checkExistsNormalFile(requiredFieldsFile, "RequireFieldsFile");
        
        List<String> catalogs = (List<String>) map.get(CONF_TRANSFORMATION_CATALOGS);
        
        RequiredFields requiredFields = RequiredFields.loadRequiredFieldsFile(requiredFieldsFile);
        
        return new TransformationConfiguration(xsltDir, xsdDir, catalogs, requiredFields);
    }
    
    /** @return The configuration for the bitrepository.*/
    public BitmagConfiguration getBitmagConf() {
        return bitmagConf;
    }
    
    /** @return The configruation for accessing Cumulus.*/
    public CumulusConfiguration getCumulusConf() {
        return cumulusConf;
    }
    
    /** @return The configuration for the transformation.*/
    public TransformationConfiguration getTransformationConf() {
        return transformationConf;
    }
}
