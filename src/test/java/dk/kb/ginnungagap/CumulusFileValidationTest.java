package dk.kb.ginnungagap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.yggdrasil.exceptions.ArgumentCheck;
import junit.framework.Assert;

public class CumulusFileValidationTest extends ExtendedTestCase {

    File testConf;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInstantiation() {
        CumulusFileValidation cumulusFileValidation = new CumulusFileValidation();
        Assert.assertNotNull(cumulusFileValidation);
        Assert.assertTrue(cumulusFileValidation instanceof CumulusFileValidation);
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testMissingArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CumulusFileValidation.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNonExistingConfigurationFile() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CumulusFileValidation.main(UUID.randomUUID().toString());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnableToConnect() throws Exception {
        CumulusFileValidation.main(testConf.getAbsolutePath(), UUID.randomUUID().toString());
    }
    
    @Test
    public void testGetOutputFileNonExistingFile() {
        File testDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        File outputFile = new File(testDir, UUID.randomUUID().toString());
        Assert.assertFalse(outputFile.exists());
        Assert.assertEquals(0, testDir.listFiles().length);
        CumulusFileValidation.getOutputFile(outputFile.getAbsolutePath());
        Assert.assertFalse(outputFile.exists());
    }
    
    @Test
    public void testGetOutputFileExistingFile() throws IOException {
        File outputFile = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        Assert.assertTrue(outputFile.exists());
        CumulusFileValidation.getOutputFile(outputFile.getAbsolutePath());
        Assert.assertFalse(outputFile.exists());
        Assert.assertTrue(new File(outputFile.getAbsolutePath() + ".old").exists());
    }
    
    @Test
    public void testCheckRecordWithFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CumulusRecord record = mock(CumulusRecord.class);
        String catalogName = UUID.randomUUID().toString();
        
        when(record.getFile()).thenReturn(testConf);
        CumulusFileValidation.checkRecord(record, catalogName, baos);
        
        String res = baos.toString();
        Assert.assertTrue(res.contains(catalogName));
        Assert.assertTrue(res.contains(CumulusFileValidation.OUTPUT_RES_FOUND));
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    

    @Test
    public void testCheckRecordWithMissingFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CumulusRecord record = mock(CumulusRecord.class);
        String catalogName = UUID.randomUUID().toString();
        
        when(record.getFile()).thenReturn(new File(UUID.randomUUID().toString()));
        CumulusFileValidation.checkRecord(record, catalogName, baos);
        
        String res = baos.toString();
        Assert.assertTrue(res.contains(catalogName));
        Assert.assertTrue(res.contains(CumulusFileValidation.OUTPUT_RES_MISSING));
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testCheckRecordWithError() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CumulusRecord record = mock(CumulusRecord.class);
        String catalogName = UUID.randomUUID().toString();
        
        when(record.getFile()).thenThrow(new RuntimeException("TEST FAILURE"));
        CumulusFileValidation.checkRecord(record, catalogName, baos);
        
        String res = baos.toString();
        Assert.assertTrue(res.contains(catalogName));
        Assert.assertTrue(res.contains(CumulusFileValidation.OUTPUT_RES_ERROR));
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testValidateCumulusRecordFiles() throws Exception {
        CumulusServer server = mock(CumulusServer.class);
        TestConfiguration conf = TestFileUtils.createTempConf();
        File outputFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()); 
        File resourceFile = new File("src/test/resources/test-resource.txt");
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(items);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFile()).thenReturn(resourceFile);
        when(record.getUUID()).thenReturn(UUID.randomUUID().toString());
        
        CumulusFileValidation.validateCumulusRecordFiles(server, conf, outputFile);
        
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testValidateCumulusRecordFilesFailure() throws Exception {
        CumulusServer server = mock(CumulusServer.class);
        TestConfiguration conf = TestFileUtils.createTempConf();
        File outputFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()); 
        
        try {
            TestFileUtils.getTempDir().setWritable(false);
            CumulusFileValidation.validateCumulusRecordFiles(server, conf, outputFile);            
        } finally {
            TestFileUtils.getTempDir().setWritable(true);
        }
    }    
}
