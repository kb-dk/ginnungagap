package dk.kb.ginnungagap.config;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.File;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class RequiredFieldsTest extends ExtendedTestCase {

    File requiredFieldsFile;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        requiredFieldsFile = new File("src/test/resources/conf/required_fields.yml");
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testLoadingFile() throws Exception {
        RequiredFields rf = RequiredFields.loadRequiredFieldsFile(requiredFieldsFile);
        assertNotNull(rf.getBaseFields());
        assertFalse(rf.getBaseFields().isEmpty());
        assertNotNull(rf.getWritableFields());
        assertFalse(rf.getWritableFields().isEmpty());
    }
    
    @Test(expectedExceptions = ArgumentCheck.class)
    public void testLoadingBadFile() throws Exception {
        File badRequiredFieldsFile = TestFileUtils.createFileWithContent("THIS MUST FAIL");
        RequiredFields.loadRequiredFieldsFile(badRequiredFieldsFile);
    }
}
