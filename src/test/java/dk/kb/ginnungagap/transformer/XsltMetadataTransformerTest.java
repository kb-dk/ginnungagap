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
        xsltFile = new File("src/main/resources/scripts/xslt/transformToMets.xsl");
        assertTrue(xsltFile.isFile());
    }
    
    @AfterClass
    public void tearDown() {
//        TestFileUtils.tearDown();
    }
    
    @Test
    public void testTransformationWithMix() throws Exception {
        addDescription("Test the transformation of an old Cumulus XML file with MIX metadata.");
//        File xmlFile = new File("src/test/resources/540.xml");
        File xmlFile = new File("src/test/resources/Car_S-9090.tif.raw.xml");
        assertTrue(xmlFile.isFile());
        XsltMetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        addStep("Transform the Cumulus XML", "METS");
        try {
            transformer.transformXmlMetadata(new FileInputStream(xmlFile), out);
            File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
            try (FileOutputStream fos = new FileOutputStream(metadataFile);) {
                fos.write(out.toByteArray());
                fos.flush();
            }

            addStep("Validate the METS", "");
            transformer.validate(new FileInputStream(metadataFile));
        } finally {
            System.out.println(out.toString());
        }
    }

    @Test
    public void testTransformationWithBext() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file with BEXT metadata.");
        File xmlFile = new File("src/test/resources/540.xml");
//        File xmlFile = new File("src/test/resources/Car_S-9090.tif.raw.xml");
        assertTrue(xmlFile.isFile());
        XsltMetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), out);
        
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        try (FileOutputStream fos = new FileOutputStream(metadataFile);) {
            fos.write(out.toByteArray());
            fos.flush();
        }
        
        addStep("Validate the METS", "");
        transformer.validate(new FileInputStream(metadataFile));
    }
    
    @Test
    public void testTransformationWithAudioTracks() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file with tracks and BEXT metadata.");
        File xmlFile = new File("src/test/resources/audio_example_1345.xml");
//        File tracksXsltFile = new File("src/test/resources/scripts/standaloneTracks.xsl");
//        File xmlFile = new File("src/test/resources/Car_S-9090.tif.raw.xml");
        assertTrue(xmlFile.isFile());
        XsltMetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
        
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        
        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));
        
//        try (FileOutputStream fos = new FileOutputStream(metadataFile);) {
//            fos.write(out.toByteArray());
//            fos.flush();
//        }
        
        addStep("Validate the METS", "");
        transformer.validate(new FileInputStream(metadataFile));
    }
    
    @Test
    public void testCatalogStructmapTransformation() throws Exception {
        addDescription("Test the catalog structmap transformation");
        File xmlFile = new File("src/test/resources/structMapTestInput.xml");
        File xsltFile = new File("src/main/resources/scripts/xslt/transformCatalogStructmap.xsl");

        assertTrue(xmlFile.isFile());
        XsltMetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
        
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        
        addStep("Transform the Cumulus XML into a structmap", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));
        
//        try (FileOutputStream fos = new FileOutputStream(metadataFile);) {
//            fos.write(out.toByteArray());
//            fos.flush();
//        }
        
        addStep("Validate the METS", "");
        transformer.validate(new FileInputStream(metadataFile));
    }
}
