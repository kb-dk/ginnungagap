package dk.kb.ginnungagap.testutils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.testng.SkipException;

import dk.kb.cumulus.config.CumulusConfiguration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.yggdrasil.utils.YamlTools;

public class SetupCumulusTests {

    protected static String passwordFilePath = System.getenv("HOME") + "/cumulus-password.yml";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static TestConfiguration getConfiguration(String ... catalogNames) throws Exception {
        File passwordFile = new File(passwordFilePath);
        if(!passwordFile.isFile()) {
            throw new SkipException("Cannot connect to Cumulus without the password-file: " + passwordFilePath);
        }
        TestFileUtils.setup();
        TestConfiguration conf = TestFileUtils.createTempConf();
        
        Map cumulusFileContent = YamlTools.loadYamlSettings(passwordFile);
        Map<String, String> cumulusLogin = (Map<String, String>) cumulusFileContent;

        String username = cumulusLogin.get("login");
        String password = cumulusLogin.get("password");
        String serverurl = cumulusLogin.get("serverurl");
        
        CumulusConfiguration cConf = new CumulusConfiguration(true, serverurl, username, password,
                Arrays.asList(catalogNames));
        conf.setCumulusConf(cConf);
        
        return conf;
    }
}
