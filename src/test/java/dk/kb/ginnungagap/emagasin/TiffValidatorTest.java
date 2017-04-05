package dk.kb.ginnungagap.emagasin;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.ginnungagap.utils.StreamUtils;
import dk.kb.metadata.utils.GuidExtrationUtils;
import junit.framework.Assert;

public class TiffValidatorTest extends ExtendedTestCase {

    File outputDir;
    File tiffFile;
    File scriptFile;
    File confFile;
    
    @BeforeClass
    public void setupClass() throws IOException {
        TestFileUtils.setup();
        tiffFile = TestFileUtils.copyFileToTemp(new File("src/test/resources/validation/minimal_valid_baseline.tiff"));
        scriptFile = TestFileUtils.copyFileToTemp(new File("src/main/resources/bin/run_checkit_tiff.sh"));
        confFile = TestFileUtils.copyFileToTemp(new File("src/main/resources/conf/cit_tiff.cfg"));
    }
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        outputDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
    }
    
//    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
//    @Test
    public void testTiffFile() throws IOException {
        Assert.assertTrue(tiffFile.isFile());
        Assert.assertTrue(scriptFile.isFile());
        Assert.assertTrue(confFile.isFile());
        
        TiffValidator validator = new TiffValidator(outputDir, scriptFile, confFile, false);
        validator.validateTiffFile(tiffFile);
    }
    
//    @Test
    public void testValidatingArcFile() throws IOException {
        File testFile = new File("/home/jolf/data/KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc");
        if(!testFile.exists()) {
            throw new SkipException("Ignore test, since file '" + testFile.getAbsolutePath() + "' does not exist");
        }
        
        TiffValidator validator = new TiffValidator(outputDir, scriptFile, confFile, false);
        validator.validateTiffRecordsInArcFile(testFile);
        
    }
    
    @Test
    public void testArcFile() throws Exception {
        File testFile = new File("/home/jolf/data/KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc");

        ArcReader ar = ArcReaderFactory.getReader(new FileInputStream(testFile));
        ArcRecordBase arcRecord;
        while((arcRecord = ar.getNextRecord()) != null) {
            String uid = GuidExtrationUtils.extractGuid(arcRecord.getUrlStr());
            File outputFile = new File(outputDir, uid);
            StreamUtils.copyInputStreamToOutputStream(arcRecord.getPayloadContent(), 
                    new FileOutputStream(outputFile));
        }
    }
}
