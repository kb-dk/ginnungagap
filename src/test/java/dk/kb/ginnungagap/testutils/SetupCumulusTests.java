package dk.kb.ginnungagap.testutils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.testng.SkipException;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.CumulusConfiguration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.yggdrasil.utils.YamlTools;

public class SetupCumulusTests {

    protected static String passwordFilePath = System.getenv("HOME") + "/cumulus-password.yml";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Configuration getConfiguration(String ... catalogNames) throws Exception {
        File passwordFile = new File(passwordFilePath);
        if(!passwordFile.isFile()) {
            throw new SkipException("Cannot connect to Cumulus without the password-file: " + passwordFilePath);
        }
        TestFileUtils.setup();
        TestConfiguration conf =TestFileUtils.createTempConf();
        
        Map cumulusFileContent = YamlTools.loadYamlSettings(passwordFile);
        Map<String, String> cumulusLogin = (Map<String, String>) cumulusFileContent;

        String username = cumulusLogin.get("login");
        String password = cumulusLogin.get("password");
        
        CumulusConfiguration cConf = new CumulusConfiguration(true, "cumulus-core-test-01.kb.dk", username, password,
                Arrays.asList(catalogNames));
        conf.setCumulusConf(cConf);
        
        return conf;
    }
}
