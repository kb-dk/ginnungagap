package dk.kb.ginnungagap;

import dk.kb.ginnungagap.transformation.xml.XmlEntityResolver;
import dk.kb.ginnungagap.transformation.xml.XmlErrorHandler;
import dk.kb.ginnungagap.transformation.xml.XmlValidationResult;
import dk.kb.ginnungagap.transformation.xml.XmlValidator;
import dk.kb.ginnungagap.utils.StringUtils;
import junit.framework.Assert;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;

public class IdKbDkValidationTest extends ExtendedTestCase {

    @Test
    public void testAgent() throws Exception {
        addDescription("Validates all the agents.");
        XmlValidator validator = new XmlValidator();
        File agentDir = new File("src/main/resources/id/agents");
        Assert.assertNotNull(agentDir);
        Assert.assertTrue(agentDir.isDirectory());
        for(File f : agentDir.listFiles()) {
            if(f.getName().endsWith(".xml")) {
                XmlEntityResolver entityResolver = null;
                XmlErrorHandler errorHandler = new XmlErrorHandler();
                XmlValidationResult validationResult = new XmlValidationResult();

                boolean res = validator.testDefinedValidity(new FileInputStream(f), entityResolver, errorHandler, validationResult);
                String fatalErrors = StringUtils.listToString(errorHandler.fatalErrors, "\n");
                String errors = StringUtils.listToString(errorHandler.errors, "\n");
                String warnings = StringUtils.listToString(errorHandler.warnings, "\n");

                Assert.assertTrue("Failed validation of '" + f.getName() + ": \nfatal errors: " + fatalErrors
                        + "\n other errors: " + errors + " \nwarnings: " + warnings, res);
            }
        }
    }

    @Test
    public void testProfiles() throws Exception {
        addDescription("Validates the current METS profile");
        XmlValidator validator = new XmlValidator();
        File kbMetsProfile = new File("src/main/resources/id/mets_profile/version_2/kbMetsProfile.xml");
        Assert.assertNotNull(kbMetsProfile);
        Assert.assertTrue(kbMetsProfile.isFile());
        
        XmlEntityResolver entityResolver = null;
        XmlErrorHandler errorHandler = new XmlErrorHandler();
        XmlValidationResult validationResult = new XmlValidationResult();

        boolean res = validator.testDefinedValidity(new FileInputStream(kbMetsProfile), entityResolver, errorHandler, validationResult);
        String fatalErrors = StringUtils.listToString(errorHandler.fatalErrors, "\n");
        String errors = StringUtils.listToString(errorHandler.errors, "\n");
        String warnings = StringUtils.listToString(errorHandler.warnings, "\n");

        Assert.assertTrue("Failed validation of '" + kbMetsProfile.getName() + ": \nfatal errors: " + fatalErrors
                + "\n other errors: " + errors + " \nwarnings: " + warnings, res);
    }
}
