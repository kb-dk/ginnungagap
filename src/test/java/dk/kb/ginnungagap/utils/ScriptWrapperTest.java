package dk.kb.ginnungagap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.exception.RunScriptException;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class ScriptWrapperTest extends ExtendedTestCase {

    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testCallVoidScriptSuccess() throws Exception {
        addDescription("Test the successful run of a bash script");
        File scriptFile = new File("src/test/resources/scripts/test_echo.sh");
        ScriptWrapper script = new ScriptWrapper(scriptFile);

        String arg1 = UUID.randomUUID().toString();
        String arg2 = UUID.randomUUID().toString();

        File outputFile = new File(arg2);
        try {
            script.callVoidScript(arg1, arg2);

            Assert.assertTrue(outputFile.isFile());
            String content = StreamUtils.extractInputStreamAsString(new FileInputStream(outputFile));
            Assert.assertTrue(content.contains(arg1));
            Assert.assertTrue(content.contains(arg2));
        } finally {
            if(outputFile.exists()) {
                outputFile.delete();
            }
        }
    }
    
    @Test(expectedExceptions = RunScriptException.class)
    public void testCallVoidScriptFailure() throws Exception {
        addDescription("Test what happens when the script fails (e.g. the test script only getting one argument)");
        File scriptFile = new File("src/test/resources/scripts/test_echo.sh");
        ScriptWrapper script = new ScriptWrapper(scriptFile);

        String arg1 = UUID.randomUUID().toString();

        script.callVoidScript(arg1);
    }
}
