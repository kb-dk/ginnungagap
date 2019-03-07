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
import java.util.ArrayList;
import java.util.UUID;

import dk.kb.ginnungagap.MailDispatcher;
import org.bitrepository.common.utils.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.config.TestBitmagConfiguration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class BitmagPreserverTest extends ExtendedTestCase {

    TestConfiguration conf;
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
        
        conf = TestFileUtils.createTempConf();
    }
    
    @BeforeMethod
    public void setupMethod() throws Exception {
        bitmagConf = new TestBitmagConfiguration(TestFileUtils.getTempDir(), null, 1, 1000000, TestFileUtils.getTempDir(), "SHA-1");
        conf.setBitmagConfiguration(bitmagConf);
    }
    
    @AfterClass
    public void tearDown() throws Exception {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInitialConditions() throws Exception {
        addDescription("Test the initial conditions of the bitmag preserver.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;

        assertTrue(preserver.warcPackerForCollection.isEmpty());
        preserver.checkConditions();

        verifyZeroInteractions(mailer);
    }
    
    @Test
    public void testGetWarcPacker() {
        addDescription("Test that a Warc packer will be reused");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker packer = preserver.getWarcPacker(collectionId);
        Assert.assertEquals(packer, preserver.getWarcPacker(collectionId));

        verifyZeroInteractions(mailer);
    }
    
    @Test
    public void testPackRecordResource() {
        addDescription("Test packing a Cumulus record resource");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getFile()).thenReturn(resourceFile);

        preserver.packRecordResource(record);
        
        verifyZeroInteractions(archive);
        
        verify(wp).packRecordAssetFile(eq(record), eq(resourceFile));
        verify(wp).addRecordToPackagedList(eq(record));
        verifyNoMoreInteractions(wp);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).getFile();
        verifyNoMoreInteractions(record);

        verifyZeroInteractions(mailer);
    }
    
    @Test
    public void testPackRecordMetadataSuccess() {
        addDescription("Test packing a Cumulus record metadata");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.GUID))).thenReturn(UUID.randomUUID().toString());
        
        preserver.packRecordMetadata(record, metadataFile);
        
        verifyZeroInteractions(archive);
        
        verify(wp).packMetadata(eq(metadataFile), any(Uri.class), anyString());
        verify(wp).addRecordToMetadataPackagedList(eq(record));
        when(wp.getPackagedCompleteRecords()).thenReturn(new ArrayList<>());
        when(wp.getPackagedMetadataRecords()).thenReturn(new ArrayList<>());
        verifyNoMoreInteractions(wp);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.GUID));
        verifyNoMoreInteractions(record);

        verifyZeroInteractions(mailer);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testPackRecordMetadataFailure() {
        addDescription("Test packing a Cumulus record metadata, when the GUID cannot be put into a URI.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.GUID))).thenReturn("THIS IS NOT POSSIBLE TO PUT INTO A URI #\\/");
        
        preserver.packRecordMetadata(record, metadataFile);

        verifyZeroInteractions(mailer);
    }
    
    @Test
    public void testPackRepresentationMetadata() {
        addDescription("Test packing the representaiton metadata.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        String recordId = UUID.randomUUID().toString();
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        
        preserver.packRepresentationMetadata(metadataFile, collectionId, recordId);
        
        verifyZeroInteractions(archive);
        
        verify(wp).packMetadata(eq(metadataFile), eq(null), eq(recordId));
        verifyNoMoreInteractions(wp);

        verifyZeroInteractions(mailer);
    }
    
    @Test
    public void testCheckConditionsWhenNotReady() {
        addDescription("Test the method for checking the conditions on the WarcPacker, when it is not ready.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getSize()).thenReturn(0L);
        when(wp.getPackagedCompleteRecords()).thenReturn(new ArrayList<>());
        when(wp.getPackagedMetadataRecords()).thenReturn(new ArrayList<>());

        preserver.checkConditions();
        
        verifyZeroInteractions(archive);
        
        verify(wp).getSize();
        verifyNoMoreInteractions(wp);

        verifyZeroInteractions(mailer);
    }
    
    @Test
    public void testCheckConditionsWhenReady() {
        addDescription("Test the method for checking the conditions on the WarcPacker, when it is ready and successfully uploaded to the archive.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getSize()).thenReturn(Long.MAX_VALUE);
        when(wp.getWarcFile()).thenReturn(warcFile);
        when(wp.hasContent()).thenReturn(true);
        when(wp.getPackagedCompleteRecords()).thenReturn(new ArrayList<>());
        when(wp.getPackagedMetadataRecords()).thenReturn(new ArrayList<>());
        when(archive.uploadFile(any(File.class), anyString())).thenReturn(true);
        
        preserver.checkConditions();
        
        verify(archive).uploadFile(any(File.class), anyString());
        verifyNoMoreInteractions(archive);
        
        verify(wp).getSize();
        verify(wp, times(5)).getWarcFile();
        verify(wp).close();
        verify(wp).reportSucces(any(WarcDigest.class));
        verify(wp).hasContent();
        verify(wp).getPackagedCompleteRecords();
        verify(wp).getPackagedMetadataRecords();
        verifyNoMoreInteractions(wp);

        verify(mailer).sendReport(anyString(), anyString());
        verifyNoMoreInteractions(mailer);
    }
    
    @Test
    public void testUploadAllWhenItHasContent() {
        addDescription("Test the uploadAll method, when the warc-packer has content.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getWarcFile()).thenReturn(warcFile);
        when(wp.hasContent()).thenReturn(true);
        when(archive.uploadFile(any(File.class), anyString())).thenReturn(false);
        
        preserver.uploadAll();
        
        verify(archive).uploadFile(any(File.class), anyString());
        verifyNoMoreInteractions(archive);
        
        verify(wp, times(5)).getWarcFile();
        verify(wp).close();
        verify(wp).reportFailure(anyString());
        verify(wp).hasContent();
        verify(wp).getPackagedCompleteRecords();
        verify(wp).getPackagedMetadataRecords();
        verifyNoMoreInteractions(wp);

        verify(mailer).sendReport(anyString(), anyString());
        verifyNoMoreInteractions(mailer);
    }   
    
    @Test
    public void testUploadAllWhenItHasNoContent() {
        addDescription("Test the uploadAll method, when the warc-packer does not have content.");
        ArchiveWrapper archive = mock(ArchiveWrapper.class);
        MailDispatcher mailer = mock(MailDispatcher.class);
        BitmagPreserver preserver = new BitmagPreserver();
        preserver.archive = archive;
        preserver.conf = conf;
        preserver.mailer = mailer;
        
        WarcPacker wp = mock(WarcPacker.class);
        preserver.warcPackerForCollection.put(collectionId, wp);
        when(wp.getWarcFile()).thenReturn(warcFile);
        when(wp.hasContent()).thenReturn(false);
        when(wp.getPackagedCompleteRecords()).thenReturn(new ArrayList<>());
        when(wp.getPackagedMetadataRecords()).thenReturn(new ArrayList<>());
        when(archive.uploadFile(any(File.class), anyString())).thenReturn(false);
        
        preserver.uploadAll();
        
        verifyZeroInteractions(archive);
        
        verify(wp).getWarcFile();
        verify(wp).close();
        verify(wp).hasContent();
        verifyNoMoreInteractions(wp);

        verifyZeroInteractions(mailer);
    }
}
