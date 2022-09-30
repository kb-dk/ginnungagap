package dk.kb.ginnungagap.transformation;

import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.utils.StreamUtils;

public class MetadataTransformationHandlerTest extends ExtendedTestCase {

    protected File xsltFile;
    
    protected boolean writeOutput = false;
    
    MetadataTransformationHandler transformationHandler;
    
    Configuration conf;

    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        xsltFile = new File("src/main/resources/scripts/xslt/transformToMets.xsl");
        assertTrue(xsltFile.isFile());
        transformationHandler = new MetadataTransformationHandler();
        transformationHandler.conf = conf;
        transformationHandler.initialize();
    }

    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }

    @Test
    public void testTransformationWithMix() throws Exception {
        addDescription("Test the transformation of an old Cumulus XML file with MIX metadata.");
        File xmlFile = new File("src/test/resources/Car_S-9090.tif.raw.xml");
        assertTrue(xmlFile.isFile());
        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        addStep("Transform the Cumulus XML", "METS");
        try {
            transformer.transformXmlMetadata(new FileInputStream(xmlFile), out);
            File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
            try (FileOutputStream fos = new FileOutputStream(metadataFile)) {
                fos.write(out.toByteArray());
                fos.flush();
            }

            addStep("Validate the METS", "");
            transformationHandler.validate(new FileInputStream(metadataFile));
        } finally {
            if(writeOutput) {
                System.out.println(out.toString());
            }
        }
    }

//    @Ignore
    @Test
    public void testTransformationWithTables() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file with Ophav-tabel and Person-tabel metadata.");
        File xmlFile = new File("src/test/resources/000395.tif.raw.xml");
        assertTrue(xmlFile.isFile());
        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        addStep("Transform the Cumulus XML", "METS");
        try {
            transformer.transformXmlMetadata(new FileInputStream(xmlFile), out);
            File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
            try (FileOutputStream fos = new FileOutputStream(metadataFile)) {
                fos.write(out.toByteArray());
                fos.flush();
            }

            addStep("Validate the METS", "");
            transformationHandler.validate(new FileInputStream(metadataFile));
        } finally {
            if(writeOutput) {
                System.out.println(out);
            }
        }
    }

    @Test
    public void testTransformationWithBext() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file with BEXT metadata.");
        File xmlFile = new File("src/test/resources/540.xml");
        assertTrue(xmlFile.isFile());
        MetadataTransformer transformer = new MetadataTransformer(xsltFile);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), out);

        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        try (FileOutputStream fos = new FileOutputStream(metadataFile)) {
            fos.write(out.toByteArray());
            fos.flush();
        }

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));
    }

    @Test
    public void testTransformationWithAudioTracks() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file with tracks and BEXT metadata.");
        File xmlFile = new File("src/test/resources/audio_example_1345.xml");
        assertTrue(xmlFile.isFile());

        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");

        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));

        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));

    }

    @Test
    public void testCatalogStructmapTransformation() throws Exception {
        addDescription("Test the catalog structmap transformation");
        File xmlFile = new File("src/test/resources/structMapTestInput.xml");

        assertTrue(xmlFile.isFile());
        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP);
        
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");

        addStep("Transform the Cumulus XML into a structmap", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }

    @Test
    public void testRepresentation() throws Exception {
        addDescription("Test the transformation for a representation");
        File xmlFile = new File("src/test/resources/test_representation.raw.xml");
        assertTrue(xmlFile.isFile());

        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION);
        
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");

        addStep("Transform the Cumulus XML into a structmap", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
    
    @Test
    public void testIntellectuelEntityXml() throws Exception {
        addDescription("Testing the creation of the intellectual entity xml element.");
        File xmlFile = new File("src/test/resources/test_kb_ids.raw.xml");
        assertTrue(xmlFile.isFile());
        
        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);

        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        
        addStep("Transform the metadata", "KB IDs element in the metadata file");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
    
    @Test
    public void testIntellectuelEntityXmlWithByteArray() throws Exception {
        addDescription("Testing the creation of the intellectual entity xml element.");
        String xml = "<record><ie_uuid>IE_UUID</ie_uuid><object_uuid>OBJECT_UUID</object_uuid></record>";

        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);

        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");
        
        addStep("Transform the metadata", "KB IDs element in the metadata file");
        transformer.transformXmlMetadata(bais, new FileOutputStream(metadataFile));

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }

    @Test
    public void testExtractingSchemaVersions() throws Exception {
        addDescription("Tests the extraction of schema versions from an XML document.");
        List<String> expectedNamespaces = Arrays.asList("http://www.loc.gov/premis/", 
                "http://www.loc.gov/mods/", 
                "http://www.loc.gov/mix/", 
                "http://www.loc.gov/METS/");
        File testMetsFile = new File("src/test/resources/test-mets.xml");

        Collection<String> schemaLocations =  transformationHandler.getMetadataStandards(new FileInputStream(testMetsFile));

        addStep("Find expected namespaces", "Must be present");
        for(String namespace : expectedNamespaces) {
            boolean found = false;
            for(String s : schemaLocations) {
                if(s.contains(namespace)) {
                    found = true;
                }
            }
            assertTrue(found, namespace);
        }
    }
    
    @Test
    public void testValidation() throws Exception {
        File modsFile = new File("src/test/resources/metadata/dsfl-mods.xml");
        try (InputStream in = new FileInputStream(modsFile)) {
            transformationHandler.validate(in);
        }
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(modsFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
    
    @Test
    public void testValidation2() throws Exception {
        File modsFile = new File("src/test/resources/metadata/crowd_mods.xml");
        try (InputStream in = new FileInputStream(modsFile)) {
            transformationHandler.validate(in);
        }
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(modsFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testValidationFailure() throws Exception {
        addDescription("Test validating an XML file, which does not have a schema.");
        File metadataFile = new File("src/test/resources/test_representation.raw.xml");
        try (InputStream metadata = new FileInputStream(metadataFile)) {
            transformationHandler.validate(metadata);
        }
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
    
    @Test
    public void testTransformationWithCompleteAudioMetadata() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file with all metadata fields for audio files.");
        File xmlFile = new File("src/test/resources/cumulus_extract_with_all_fields.xml");
        assertTrue(xmlFile.isFile());

        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");

        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));

        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));

        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
    
    @Test
    public void testTransformationOfUpdateMetadata() throws Exception {
        addDescription("Test the transformation of a Cumulus XML file when has been updated.");
        File xmlFile = new File("src/test/resources/metadata/update_raw.xml");
        assertTrue(xmlFile.isFile());
        
        MetadataTransformer transformer = transformationHandler.getTransformer(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);
        
        File metadataFile = new File(TestFileUtils.getTempDir(), "output-metadata-" + Math.random() + ".xml");

        addStep("Transform the Cumulus XML", "METS");
        transformer.transformXmlMetadata(new FileInputStream(xmlFile), new FileOutputStream(metadataFile));

        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }

        addStep("Validate the METS", "");
        transformationHandler.validate(new FileInputStream(metadataFile));
        
        if(writeOutput) {
            try (InputStream is = new FileInputStream(metadataFile)) {
                String text = StreamUtils.extractInputStreamAsString(is);
                System.out.println(text);
            }
        }
    }
}
