package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.utils.YamlTools;

/**
 * The configurations in YAML format.
 * It must be in the following format:
 * <ul>
 *   <li>ginnungagap</li>
 *   <ul>
 *     <li>bitrepository_dir: $bitrepository dir path</li>
 *     <li>cumulus</li>
 *      <ul>
 *        <li>server_url: $server url</li>
 *        <li>username: $username</li>
 *        <li>password: $password</li>
 *      </ul>
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
    /** The bitrepository directory leaf-element.*/
    private static final String CONF_BITREPOSITORY_DIR = "bitrepository_dir";
    /** Cumulus node-element.*/
    private static final String CONF_CUMULUS = "cumulus";
    /** The cumulus server url leaf-element.*/
    private static final String CONF_CUMULUS_SERVER = "server_url";
    /** The cumulus server username leaf-element.*/
    private static final String CONF_CUMULUS_USERNAME = "username";
    /** The cumulus server password leaf-element.*/
    private static final String CONF_CUMULUS_PASSWORD = "password";
    
    /** TODO: should be 'true', when used properly.*/
    private static final boolean CUMULUS_WRITE_ACCESS = false;
    
    BitmagConfiguration bitmagConf;
    CumulusConfiguration cumulusConf;
    TransformationConfiguration transformationConf;
    
    /**
     * 
     * @param confFile
     */
    public Configuration(File confFile) {
        ArgumentCheck.checkExistsNormalFile(confFile, "File confFile");
        try {
            LinkedHashMap<String, LinkedHashMap> map = YamlTools.loadYamlSettings(confFile);
            
            ArgumentCheck.checkTrue(map.containsKey(CONF_GINNUNGAGAP), 
                    "Configuration must contain the '" + CONF_GINNUNGAGAP + "' root element.");
            Map<String, Object> confMap = (Map<String, Object>) map.get(CONF_GINNUNGAGAP);

            ArgumentCheck.checkTrue(confMap.containsKey(CONF_CUMULUS), 
                    "Configuration must contain the '" + CONF_CUMULUS + "' root element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_BITREPOSITORY_DIR), 
                    "Configuration must contain the '" + CONF_BITREPOSITORY_DIR + "' root element.");

            
//            this.bitmagConf = loadBitmagConf(map)
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) map.get(CONF_CUMULUS));
        } catch (Exception e) {
            throw new ArgumentCheck("Issue loading the configurations from file '" + confFile.getAbsolutePath() + "'", 
                    e);
            
        }
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
}
