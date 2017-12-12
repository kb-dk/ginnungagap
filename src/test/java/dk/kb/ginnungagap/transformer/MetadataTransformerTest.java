package dk.kb.ginnungagap.transformer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

public class MetadataTransformerTest extends ExtendedTestCase {

    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testConstructorFailureBadXmlFile() throws IOException {
        addDescription("Test that it fails, when instantiating it with a non-xml file");
        File nonXmlFile = TestFileUtils.createFileWithContent("THIS IS NOT EVEN XML");
        
        new MetadataTransformer(nonXmlFile);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransformationFailureBadXmlFile() throws IOException {
        addDescription("Test that it cannot transform a non-xml file");
        File nonXmlFile = TestFileUtils.createFileWithContent("THIS IS NOT EVEN XML");
        File xsltFile = new File("src/main/resources/scripts/xslt/transformToMets.xsl");
        
        MetadataTransformer transformer = new MetadataTransformer(xsltFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transformXmlMetadata(new FileInputStream(nonXmlFile), out);
    }
    
    // REMAINING TESTS PLACED IN MetadataTransformationHandlerTest
}
