package dk.kb.ginnungagap.archive;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.File;

import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.TestBitmagConfiguration;
import dk.kb.ginnungagap.record.CumulusRecord;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class BitmagPreserverTest extends ExtendedTestCase {

    TestBitmagConfiguration bitmagConf;
    
    File metadataFile;
    File resourceFile;
    
    String collectionId = "Test-collection-id";
    String recordId = "TEST-RECORD-ID";
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        bitmagConf = new TestBitmagConfiguration(TestFileUtils.getTempDir(), null, 1, 100000, TestFileUtils.getTempDir());
        
        File origMetadataFile = new File("src/test/resources/test-mets.xml");
        metadataFile = new File(TestFileUtils.getTempDir(), origMetadataFile.getName());
        FileUtils.copyFile(origMetadataFile, metadataFile);
        
        File origResourceFile = new File("src/test/resources/test-resource.txt");
        resourceFile = new File(TestFileUtils.getTempDir(), origResourceFile.getName());
        FileUtils.copyFile(origResourceFile, resourceFile);
    }
    
    @AfterClass
    public void tearDown() throws Exception {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInitialConditions() throws Exception {
        addDescription("Test the initial conditions of the bitmag preserver.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        assertTrue(preserver.warcPackerForCollection.isEmpty());
        preserver.checkConditions();
    }
    
    @Test
    public void testPreservingRecord() throws Exception {
        addDescription("Test preserving a record, which is too small for automatic upload");
        Archive archive = mock(Archive.class);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(anyString())).thenReturn(collectionId);
        when(record.getFile()).thenReturn(resourceFile);
        when(record.getID()).thenReturn(recordId);

        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);

        addStep("Pack the record and its metadata", 
                "Should be placed in the WARC file, but the warc file should not be uploaded yet (not large enough)");
        preserver.packRecord(record, metadataFile);
        
        assertFalse(preserver.warcPackerForCollection.isEmpty());
        assertTrue(preserver.warcPackerForCollection.containsKey(collectionId));

        File warcFile = preserver.warcPackerForCollection.get(collectionId).getWarcFile();
        
        assertTrue(warcFile.exists());
        assertEquals(bitmagConf.getTempDir().getAbsolutePath(), warcFile.getParentFile().getAbsolutePath());
        
        verify(record, times(1)).getFile();
        verify(record, times(1)).getFieldValue(anyString());
        verify(record, times(1)).getID();
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(archive);
        
        addStep("Upload all warc files", 
                "The archive should receive the warc-file.");
        preserver.uploadAll();
        verify(archive).uploadFile(eq(warcFile), eq(collectionId));
        verifyNoMoreInteractions(archive);
    }
    
    @Test
    public void testPreservingRecordWithUpload() throws Exception {
        addDescription("Test preserving a record which is large enough for automatic upload.");
        Archive archive = mock(Archive.class);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(anyString())).thenReturn(collectionId);
        when(record.getFile()).thenReturn(resourceFile);
        when(record.getID()).thenReturn(recordId);
        
        bitmagConf.setWarcFileSizeLimit(3000);

        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);

        addStep("Pack the record and the default metadata", 
                "Should be placed in the WARC file, but the warc file should not be uploaded yet (not large enough)");
        preserver.packRecord(record, metadataFile);
        
        assertFalse(preserver.warcPackerForCollection.isEmpty());
        assertTrue(preserver.warcPackerForCollection.containsKey(collectionId));

        File warcFile = preserver.warcPackerForCollection.get(collectionId).getWarcFile();
        
        assertTrue(warcFile.exists());
        assertEquals(bitmagConf.getTempDir().getAbsolutePath(), warcFile.getParentFile().getAbsolutePath());
        
        verify(record, times(1)).getFile();
        verify(record, times(1)).getFieldValue(anyString());
        verify(record, times(1)).getID();
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(archive);

        addStep("Pack record again, now with larger metadata file.", "Should perform the upload");
        File newMetadataFile = TestFileUtils.createFileWithContent(createString(bitmagConf.getWarcFileSizeLimit()));
        preserver.packRecord(record, newMetadataFile);

        verify(archive).uploadFile(eq(warcFile), eq(collectionId));
        verifyNoMoreInteractions(archive);        
    }
    
    protected String createString(int minSize) {
        StringBuilder res = new StringBuilder();
        
        while(res.length() < minSize) {
            res.append("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZæøåÆØÅ");
        }
        
        return res.toString();
    }
}
