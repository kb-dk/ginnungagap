package dk.kb.ginnungagap.transformer;

import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;

public class XsltMetadataTransformerTest extends ExtendedTestCase {

    protected File xsltFile;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
//        xsltFile = new File("src/main/resources/scripts/xslt/transformToMets.xsl");
//        xsltFile = new File("src/main/resources/scripts/xslt/standaloneMods.xsl");
        xsltFile = new File("src/main/resources/scripts/xslt/standalonePremis.xsl");
        assertTrue(xsltFile.isFile());
    }
    
    @AfterClass
    public void tearDown() {
//        TestFileUtils.tearDown();
    }
    
    @Test
    public void testTransformation() throws Exception {
        addDescription("Test the transformation of an Cumulus XML file.");
//        File xmlFile = new File("src/test/resources/540.xml");
        File xmlFile = new File("src/test/resources/Car_S-9090.tif.raw.xml");
        assertTrue(xmlFile.isFile());
        XsltMetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), out);
        
//        System.err.println(out.toString());
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        try (FileOutputStream fos = new FileOutputStream(metadataFile);) {
            fos.write(out.toByteArray());
            fos.flush();
        }
        
        addStep("Validate the METS", "");
        transformer.validate(new FileInputStream(metadataFile));
    }
}
