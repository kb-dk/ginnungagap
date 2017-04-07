package dk.kb.ginnungagap;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;

public class EmagTiffValidatorTest extends ExtendedTestCase {

    File testArcFileList;
    File testRetrievalScript;
    File testValidationScript;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        testArcFileList = TestFileUtils.createFileWithContent("CONTENT for testArcFileList");
        testRetrievalScript = TestFileUtils.createFileWithContent("CONTENT for testRetrievalScript");
        testValidationScript = TestFileUtils.createFileWithContent("CONTENT for testValidationScript");
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            EmagTiffValidator.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testOnlyOneArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            EmagTiffValidator.main(testArcFileList.getAbsolutePath());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testOnlyTwoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            EmagTiffValidator.main(testArcFileList.getAbsolutePath(), testRetrievalScript.getAbsolutePath());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNonExistingArcFileList() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            EmagTiffValidator.main(UUID.randomUUID().toString(), testRetrievalScript.getAbsolutePath(), testValidationScript.getAbsolutePath());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNonExistingRetrievalScript() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            EmagTiffValidator.main(testArcFileList.getAbsolutePath(), UUID.randomUUID().toString(), testValidationScript.getAbsolutePath());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNonExistingValidationScript() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            EmagTiffValidator.main(testArcFileList.getAbsolutePath(), testRetrievalScript.getAbsolutePath(), UUID.randomUUID().toString());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

}
