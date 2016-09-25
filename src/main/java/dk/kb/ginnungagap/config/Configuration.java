package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.exception.ArgumentCheck;
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
    /** The logger.*/
    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    /** Ginnungagap root element.*/
    private static final String CONF_GINNUNGAGAP = "ginnungagap";
    
    /** The bitrepository node-element.*/
    private static final String CONF_BITREPOSITORY = "bitrepository";
    /** The bitrepository settings directory leaf-element.*/
    private static final String CONF_BITREPOSITORY_SETTINGS_DIR = "settings_dir";
    /** The bitrepository settings directory leaf-element.*/
    private static final String CONF_BITREPOSITORY_KEYFILE = "keyfile";
    /** The bitrepository settings directory leaf-element.*/
    private static final String CONF_BITREPOSITORY_MAX_FAILING_PILLARS = "max_failing_pillars";

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

    /** TODO: should be 'true', when used properly.*/
    private static final boolean CUMULUS_WRITE_ACCESS = false;
    
    /** The configuration for the bitrepository.*/
    BitmagConfiguration bitmagConf;
    /** The configruation for accessing Cumulus.*/
    CumulusConfiguration cumulusConf;
    /** The configuration for the transformation.*/
    TransformationConfiguration transformationConf;
    
    /**
     * Constructor.
     * @param confFile The file with the Ginnungagap configuration, in the described format.
     */
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
            
            this.bitmagConf = loadBitmagConf((Map<String, Object>) map.get(CONF_BITREPOSITORY));
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) map.get(CONF_CUMULUS));
//            this.transformationConf = loadTransformationConfiguration()
        } catch (Exception e) {
            throw new ArgumentCheck("Issue loading the configurations from file '" + confFile.getAbsolutePath() + "'", 
                    e);
            
        }
    }
    
    protected BitmagConfiguration loadBitmagConf(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_SETTINGS_DIR), 
                "Missing Cumulus element '" + CONF_BITREPOSITORY_SETTINGS_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_KEYFILE), 
                "Missing Cumulus element '" + CONF_BITREPOSITORY_KEYFILE + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_MAX_FAILING_PILLARS), 
                "Missing Cumulus element '" + CONF_BITREPOSITORY_MAX_FAILING_PILLARS + "'");
        
        File settingsDir = new File((String) map.get(CONF_BITREPOSITORY_SETTINGS_DIR));
        ArgumentCheck.checkExistsDirectory(settingsDir, "Directory " + settingsDir.getAbsolutePath());
        
        File keyFile = null;
        String keyfilePath = (String) map.get(CONF_BITREPOSITORY_KEYFILE);
        if(!keyfilePath.isEmpty()) {
            keyFile = new File(keyfilePath);
        }
        
        int maxFailingPillars = (int) map.get(CONF_BITREPOSITORY_KEYFILE);
        
        return new BitmagConfiguration(settingsDir, keyFile, maxFailingPillars);
    }
    
    /**
     * Loads the Cumulus configuration from the 'cumulus' element in the configuration map.
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
