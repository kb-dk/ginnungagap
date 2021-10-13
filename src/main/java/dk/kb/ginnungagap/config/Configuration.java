package dk.kb.ginnungagap.config;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dk.kb.ginnungagap.utils.YamlTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dk.kb.cumulus.config.CumulusConfiguration;
import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.BooleanUtils;
import dk.kb.ginnungagap.utils.FileUtils;

/**
 * The configurations in YAML format.
 * It must be in the following format:
 * <ul>
 *   <li>ginnungagap:</li>
 *   <ul>
 *     <li>local:</li>
 *     <ul>
 *       <li>output_path: $output_path</li>
 *       <li>archive_path: $archive_path</li>
 *       <li>test: $TEST (optional - default no)</li>
 *     </ul>
 *     <li>bitrepository:</li>
 *     <ul>
 *       <li>settings_dir: $settings dir path</li>
 *       <li>keyfile: $keyfile path</li>
 *       <li>max_failing_pillars: $max_failing_pillars</li>
 *       <li>warc_size_limit: $warc_size_limit</li>
 *       <li>temp_dir: $temp_dir</li>
 *       <li>algorithm: $algorithm</li>
 *     </ul>
 *     <li>cumulus:</li>
 *     <ul>
 *       <li>server_url: $server url</li>
 *       <li>username: $username</li>
 *       <li>password: $password</li>
 *       <li>catalogs: <br/>- $catalog 1<br/>- $catalog 2<br/>- ...</li>
 *     </ul>
 *     <li>workflow:</li>
 *     <ul>
 *       <li>interval: $interval</li>
 *       <li>retain_dir: $retain_dir</li>
 *     </ul>
 *     <li>transformation:</li>
 *     <ul>
 *       <li>xsd_dir: $xsd_dir</li>
 *       <li>xslt_dir: $xslt_dir</li>
 *       <li>required_fields_file: $required_fields_file</li>
 *       <li>metadata_temp_dir: $metadata_temp_dir</li>
 *     </ul>
 *     <li>mail:</li>
 *     <ul>
 *       <li>sender: $sender</li>
 *       <li>receiver: <br/>- $receiver1<br/>- $receiver2<br/>- ...</li>
 *     </ul>
 *   </ul>
 * </ul>
 * 
 * This contains specific configurations for each part of the workflow.
 * 
 * Bitmag settings and cumulus settings.
 * 
 * The configuration file must be in the YAML format.
 */
@SuppressWarnings("unchecked")
@Component
public class Configuration {

    /** Ginnungagap root element.*/
    protected static final String CONF_GINNUNGAGAP = "ginnungagap";
    
    /** The bitrepository node-element.*/
    protected static final String CONF_BITREPOSITORY = "bitrepository";
    /** The bitrepository settings directory leaf-element.*/
    protected static final String CONF_BITREPOSITORY_SETTINGS_DIR = "settings_dir";
    /** The bitrepository key file leaf-element.*/
    protected static final String CONF_BITREPOSITORY_KEYFILE = "keyfile";
    /** The bitrepository max failing pillars leaf-element.*/
    protected static final String CONF_BITREPOSITORY_MAX_FAILING_PILLARS = "max_failing_pillars";
    /** The bitrepository warc size limit leaf-element.*/
    protected static final String CONF_BITREPOSITORY_WARC_SIZE_LIMIT = "warc_size_limit";
    /** The bitrepository temporary directory leaf-element.*/
    protected static final String CONF_BITREPOSITORY_TEMP_DIR = "temp_dir";
    /** The bitrepository algorithm leaf-element.*/
    protected static final String CONF_BITREPOSITORY_ALGORITHM = "algorithm";
    
    /** Cumulus node-element.*/
    protected static final String CONF_CUMULUS = "cumulus";
    /** The cumulus server url leaf-element.*/
    protected static final String CONF_CUMULUS_SERVER = "server_url";
    /** The cumulus server username leaf-element.*/
    protected static final String CONF_CUMULUS_USERNAME = "username";
    /** The cumulus server password leaf-element.*/
    protected static final String CONF_CUMULUS_PASSWORD = "password";
    /** The cumulus catalogs array leaf-element.*/
    protected static final String CONF_CUMULUS_CATALOGS = "catalogs";
    
    /** The Workflow node-element.*/
    protected static final String CONF_WORKFLOW = "workflow";
    /** The workflow interval leaf-element.*/
    protected static final String CONF_WORKFLOW_INTERVAL = "interval";
    /** The workflow retain directory path leaf-element.*/
    protected static final String CONF_WORKFLOW_RETAIN_DIR = "retain_dir";
    
    /** Transformation node-element.*/
    protected static final String CONF_TRANSFORMATION = "transformation";
    /** Transformation XSD directory leaf-element.*/
    protected static final String CONF_TRANSFORMATION_XSD_DIR = "xsd_dir";
    /** Transformation XSLT directory leaf-element.*/
    protected static final String CONF_TRANSFORMATION_XSLT_DIR = "xslt_dir";
    /** Transformation required fields file leaf-element.*/
    protected static final String CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE = "required_fields_file";
    /** Transformation metadata temp file leaf-element.*/
    protected static final String CONF_TRANSFORMATION_METADATA_TEMP_FILE= "metadata_temp_dir";
    
    /** Local node-element.*/
    protected static final String CONF_LOCAL = "local";
    /** Local output dir path leaf-element.*/
    protected static final String CONF_LOCAL_OUTPUT_PATH = "output_path";
    /** Local archive dir path leaf-element.*/
    protected static final String CONF_LOCAL_ARCHIVE_PATH = "archive_path";
    /** [OPTIONAL] Local test boolean leaf-element. Default not test.*/
    protected static final String CONF_LOCAL_TEST = "test";

    /** Mail node-element.*/
    protected static final String CONF_MAIL = "mail";
    /** The mail sender leaf-element.*/
    protected static final String CONF_MAIL_SENDER = "sender";
    /** The mail receivers array leaf-element.*/
    protected static final String CONF_MAIL_RECEIVERS = "receivers";
    
    /** Whether Cumulus should have write access. */
    protected static final boolean CUMULUS_WRITE_ACCESS = true;
    
    /** The configuration for the bitrepository.*/
    protected final BitmagConfiguration bitmagConf;
    /** The configruation for accessing Cumulus.*/
    protected final CumulusConfiguration cumulusConf;
    /** The configuration for the transformation.*/
    protected final TransformationConfiguration transformationConf;
    /** The configuration for the workflows*/
    protected final WorkflowConfiguration workflowConfiguration;
    /** The configuration for the local output folders and tests.*/
    protected final LocalConfiguration localConfiguration;
    /** The configuration for the mail.*/
    protected final MailConfiguration mailConfiguration;
    
    /**
     * Constructor.
     * @param confPath The file with the Ginnungagap configuration, in the described format.
     */
    @SuppressWarnings("rawtypes")
    @Autowired
    public Configuration(@Value("#{ @environment['GINNUNGAGAP_CONF'] ?: 'ginnungagap.yml'}") String confPath) {
        ArgumentCheck.checkNotNullOrEmpty(confPath, "String confPath");
        File confFile = new File(confPath);
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
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_WORKFLOW), 
                    "Configuration must contain the '" + CONF_WORKFLOW + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_LOCAL), 
                    "Configuration must contain the '" + CONF_LOCAL + "' element.");
            ArgumentCheck.checkTrue(confMap.containsKey(CONF_MAIL),
                    "Configuration must contain the '" + CONF_MAIL + "' element.");

            this.bitmagConf = loadBitmagConf((Map<String, Object>) confMap.get(CONF_BITREPOSITORY));
            this.cumulusConf = loadCumulusConfiguration((Map<String, Object>) confMap.get(CONF_CUMULUS));
            this.transformationConf = loadTransformationConfiguration(
                    (Map<String, Object>) confMap.get(CONF_TRANSFORMATION));
            this.workflowConfiguration = loadWorkflowConfiguration((Map<String, Object>) confMap.get(CONF_WORKFLOW));
            this.localConfiguration = loadLocalConfiguration((Map<String, Object>) confMap.get(CONF_LOCAL));
            this.mailConfiguration = loadMailConfiguration((Map<String, Object>) confMap.get(CONF_MAIL));
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
        ArgumentCheck.checkTrue(map.containsKey(CONF_BITREPOSITORY_ALGORITHM), 
                "Missing Bitrepository element '" + CONF_BITREPOSITORY_ALGORITHM + "'");
        
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
        String algorithm = (String) map.get(CONF_BITREPOSITORY_ALGORITHM);
        try {
            MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new ArgumentCheck("The algorithm '" + algorithm + "' is not supported.", e);
        }
        
        return new BitmagConfiguration(settingsDir, keyFile, maxFailingPillars, warcSizeLimit, tempDir, algorithm);
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
        ArgumentCheck.checkTrue(map.containsKey(CONF_CUMULUS_CATALOGS), 
                "Missing Cumulus element '" + CONF_CUMULUS_CATALOGS + "'");
        
        List<String> catalogs = (List<String>) map.get(CONF_CUMULUS_CATALOGS);
        
        return new CumulusConfiguration(CUMULUS_WRITE_ACCESS, (String) map.get(CONF_CUMULUS_SERVER), 
                (String) map.get(CONF_CUMULUS_USERNAME), (String) map.get(CONF_CUMULUS_PASSWORD), catalogs);
    }
    
    /**
     * Loads the Cumulus configuration from the 'cumulus' element in the configuration.
     * @param map The map with the Cumulus configuration.
     * @return The configuration for the Cumulus server.
     */
    protected WorkflowConfiguration loadWorkflowConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_WORKFLOW_INTERVAL), 
                "Missing workflow element '" + CONF_WORKFLOW_INTERVAL + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_WORKFLOW_RETAIN_DIR), 
                "Missing workflow element '" + CONF_WORKFLOW_RETAIN_DIR + "'");
        
        int interval = (int) map.get(CONF_WORKFLOW_INTERVAL);
        File retainDir = FileUtils.getDirectory((String) map.get(CONF_WORKFLOW_RETAIN_DIR));
        
        return new WorkflowConfiguration(interval, retainDir);
    }
    
    /**
     * Loads the Transformation configuration from the 'transformation' element in the configuration.
     * @param map The map with the transformation configuration.
     * @return The configuration for performing the transformation.
     */
    protected TransformationConfiguration loadTransformationConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_XSD_DIR), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_XSD_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_XSLT_DIR), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_XSLT_DIR + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_METADATA_TEMP_FILE), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_METADATA_TEMP_FILE + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE), 
                "Missing Transformation element '" + CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE + "'");
        
        File xsdDir = new File((String) map.get(CONF_TRANSFORMATION_XSD_DIR));
        File xsltDir = new File((String) map.get(CONF_TRANSFORMATION_XSLT_DIR));
        File metadataTempDir = FileUtils.getDirectory((String) map.get(CONF_TRANSFORMATION_METADATA_TEMP_FILE));
        File requiredFieldsFile = new File((String) map.get(CONF_TRANSFORMATION_REQUIRED_FIELDS_FILE));
        
        ArgumentCheck.checkExistsDirectory(xsdDir, "XSD dir");
        ArgumentCheck.checkExistsDirectory(xsltDir, "XSLT dir");
        ArgumentCheck.checkExistsDirectory(metadataTempDir, "Metadata temporary dir");
        ArgumentCheck.checkExistsNormalFile(requiredFieldsFile, "RequireFieldsFile");
        
        RequiredFields requiredFields = RequiredFields.loadRequiredFieldsFile(requiredFieldsFile);
        
        return new TransformationConfiguration(xsltDir, xsdDir, metadataTempDir, requiredFields);
    }
    
    /**
     * Retrieves the local configuration from the map.
     * @param map The local map.
     * @return The local configuration.
     */
    protected LocalConfiguration loadLocalConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_LOCAL_ARCHIVE_PATH), 
                "Missing Local element '" + CONF_LOCAL_ARCHIVE_PATH + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_LOCAL_OUTPUT_PATH), 
                "Missing Local element '" + CONF_LOCAL_OUTPUT_PATH + "'");

        File outputDir = FileUtils.getDirectory((String) map.get(CONF_LOCAL_OUTPUT_PATH));
        File archiveDir = FileUtils.getDirectory((String) map.get(CONF_LOCAL_ARCHIVE_PATH));
        boolean isTest = false;
        if(map.containsKey(CONF_LOCAL_TEST)) {
            isTest = BooleanUtils.extractBoolean(map.get(CONF_LOCAL_TEST));
        }
        
        return new LocalConfiguration(archiveDir, outputDir, isTest);
    }

    /**
     * Retrieves the mail configuration from the map.
     * @param map The mail map.
     * @return The map configuration.
     */
    protected MailConfiguration loadMailConfiguration(Map<String, Object> map) {
        ArgumentCheck.checkTrue(map.containsKey(CONF_MAIL_SENDER),
                "Missing Mail element '" + CONF_MAIL_SENDER + "'");
        ArgumentCheck.checkTrue(map.containsKey(CONF_MAIL_RECEIVERS),
                "Missing Mail element '" + CONF_MAIL_RECEIVERS + "'");

        List<String> receivers = (List<String>) map.get(CONF_MAIL_RECEIVERS);

        return new MailConfiguration((String) map.get(CONF_MAIL_SENDER), receivers);
    }
    
    /** @return The local configuration. */
    public LocalConfiguration getLocalConfiguration() {
        return localConfiguration;
    }
    
    /** @return The configuration for the bitrepository.*/
    public BitmagConfiguration getBitmagConf() {
        return bitmagConf;
    }
    
    /** @return The configuration for accessing Cumulus.*/
    public CumulusConfiguration getCumulusConf() {
        return cumulusConf;
    }
    
    /** @return The configuration for the workflows.*/
    public WorkflowConfiguration getWorkflowConf() {
        return workflowConfiguration;
    }
    
    /** @return The configuration for the transformation.*/
    public TransformationConfiguration getTransformationConf() {
        return transformationConf;
    }

    /** @return The configuration for the mail.*/
    public MailConfiguration getMailConfiguration() { return mailConfiguration; }
    
    /**
     * A version of Cumulus configuration without the password.
     * @return The viewable configuration for accessing Cumulus.
     */
    public ViewableCumulusConfiguration getViewableCumulusConfiguration() {
        return new ViewableCumulusConfiguration(cumulusConf);
    }
}
