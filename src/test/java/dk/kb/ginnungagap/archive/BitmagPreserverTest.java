package dk.kb.ginnungagap.archive;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.TestBitmagConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class BitmagPreserverTest extends ExtendedTestCase {

    TestBitmagConfiguration bitmagConf;
    
    File metadataFile;
    File resourceFile;
    File warcFile;
    
    String collectionId = "Test-collection-id";
    String recordId = "TEST-RECORD-ID";
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        
        File origMetadataFile = new File("src/test/resources/test-mets.xml");
        metadataFile = new File(TestFileUtils.getTempDir(), origMetadataFile.getName());
        FileUtils.copyFile(origMetadataFile, metadataFile);
        
        File origResourceFile = new File("src/test/resources/test-resource.txt");
        resourceFile = new File(TestFileUtils.getTempDir(), origResourceFile.getName());
        FileUtils.copyFile(origResourceFile, resourceFile);
        
        File origWarcFile = new File("src/test/resources/warc/warcexample.warc");
        warcFile = new File(TestFileUtils.getTempDir(), origWarcFile.getName());
        FileUtils.copyFile(origWarcFile, warcFile);
    }
    
    @BeforeMethod
    public void setupMethod() throws Exception {
        bitmagConf = new TestBitmagConfiguration(TestFileUtils.getTempDir(), null, 1, 1000000, TestFileUtils.getTempDir(), "SHA-1");        
    }
    
    @AfterClass
    public void tearDown() throws Exception {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInitialConditions() throws Exception {
        addDescription("Test the initial conditions of the bitmag preserver.");
        BitmagArchive archive = mock(BitmagArchive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        assertTrue(preserver.warcPackerForCollection.isEmpty());
        preserver.checkConditions();
    }
    
    @Test
    public void testGetWarcPacker() {
        addDescription("Test that a Warc packer will be reused");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker packer = preserver.getWarcPacker(collectionId);
        Assert.assertEquals(packer, preserver.getWarcPacker(collectionId));
    }
    
    @Test
    public void testPackRecordResource() {
        addDescription("Test packing a Cumulus record resource");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getPreservationCollectionID()).thenReturn(collectionId);
        when(record.getFile()).thenReturn(resourceFile);
        
        preserver.packRecordResource(record);
        
        verifyZeroInteractions(archive);
        
        verify(wp).packRecordAssetFile(eq(record), eq(resourceFile));
        verify(wp).addRecordToPackagedList(eq(record));
        verifyNoMoreInteractions(wp);
        
        verify(record).getPreservationCollectionID();
        verify(record).getFile();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testPackRecordMetadataSuccess() {
        addDescription("Test packing a Cumulus record metadata");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getPreservationCollectionID()).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.GUID))).thenReturn(UUID.randomUUID().toString());
        
        preserver.packRecordMetadata(record, metadataFile);
        
        verifyZeroInteractions(archive);
        
        verify(wp).packMetadata(eq(metadataFile), any(Uri.class));
        verify(wp).addRecordToPackagedList(eq(record));
        verifyNoMoreInteractions(wp);
        
        verify(record).getPreservationCollectionID();
        verify(record).getFieldValue(eq(Constants.FieldNames.GUID));
        verifyNoMoreInteractions(record);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPackRecordMetadataFailure() {
        addDescription("Test packing a Cumulus record metadata, when the GUID cannot be put into a URI.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getPreservationCollectionID()).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.GUID))).thenReturn("THIS IS NOT POSSIBLE TO PUT INTO A URI #\\/");
        
        preserver.packRecordMetadata(record, metadataFile);
    }
    
    @Test
    public void testPackRepresentationMetadata() {
        addDescription("Test packing the representaiton metadata.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        preserver.packRepresentationMetadata(metadataFile, collectionId);
        
        verifyZeroInteractions(archive);
        
        verify(wp).packMetadata(eq(metadataFile), eq(null));
        verifyNoMoreInteractions(wp);
    }
    
    @Test
    public void testCheckConditionsWhenNotReady() {
        addDescription("Test the method for checking the conditions on the WarcPacker, when it is not ready.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getSize()).thenReturn(0L);
        
        preserver.checkConditions();
        
        verifyZeroInteractions(archive);
        
        verify(wp).getSize();
        verifyNoMoreInteractions(wp);
    }
    
    @Test
    public void testCheckConditionsWhenReady() {
        addDescription("Test the method for checking the conditions on the WarcPacker, when it is ready and successfully uploaded to the archive.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getSize()).thenReturn(Long.MAX_VALUE);
        when(wp.getWarcFile()).thenReturn(warcFile);
        when(wp.hasContent()).thenReturn(true);
        when(archive.uploadFile(any(File.class), anyString())).thenReturn(true);
        
        preserver.checkConditions();
        
        verify(archive).uploadFile(any(File.class), anyString());
        verifyNoMoreInteractions(archive);
        
        verify(wp).getSize();
        verify(wp, times(3)).getWarcFile();
        verify(wp).close();
        verify(wp).reportSucces(any(WarcDigest.class));
        verify(wp).hasContent();
        verifyNoMoreInteractions(wp);
    }
    
    @Test
    public void testUploadAllWhenItHasContent() {
        addDescription("Test the uploadAll method, when the warc-packer has content.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getWarcFile()).thenReturn(warcFile);
        when(wp.hasContent()).thenReturn(true);
        when(archive.uploadFile(any(File.class), anyString())).thenReturn(false);
        
        preserver.uploadAll();
        
        verify(archive).uploadFile(any(File.class), anyString());
        verifyNoMoreInteractions(archive);
        
        verify(wp, times(3)).getWarcFile();
        verify(wp).close();
        verify(wp).reportFailure(anyString());
        verify(wp).hasContent();
        verifyNoMoreInteractions(wp);
    }   
    
    @Test
    public void testUploadAllWhenItHasNoContent() {
        addDescription("Test the uploadAll method, when the warc-packer does not have content.");
        Archive archive = mock(Archive.class);
        BitmagPreserver preserver = new BitmagPreserver(archive, bitmagConf);
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getWarcFile()).thenReturn(warcFile);
        when(wp.hasContent()).thenReturn(false);
        when(archive.uploadFile(any(File.class), anyString())).thenReturn(false);
        
        preserver.uploadAll();
        
        verifyZeroInteractions(archive);
        
        verify(wp).getWarcFile();
        verify(wp).close();
        verify(wp).hasContent();
        verifyNoMoreInteractions(wp);
    }   
}
