package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollectionTest;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class FullValidationStepTest extends ExtendedTestCase {

    String warcPath = "src/test/resources/warc/warcexample.warc";
    String warcRecordId = "random-file-uuid";
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    String warcRecordChecksum = "a2919627d81e5e53bf9e2bce13fa44ae";
    Long warcRecordSize = 36L;
    
    Configuration conf;
    String catalogName = "test-catalog-name";
    
    @BeforeClass
    public void setupClass() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }
    
    @AfterClass
    public void tearDownClass() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testGetName() {
        addDescription("Test the GetName method.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(archive);
    }
    
    @Test
    public void testGetWarcRecordSuccess() throws IOException {
        addDescription("Test the GetWarcRecord method for the success scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        
        File exampleWarc = new File(warcPath);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            WarcRecord record = step.getWarcRecord(reader, warcRecordId);
            Assert.assertNotNull(record);
            Assert.assertEquals(record.header.contentLength.longValue(), 36L);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetWarcRecordFailureBadRecordId() throws IOException {
        addDescription("Test the GetWarcRecord method when given a id, which does not match a record in the warc file.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        String badUuid = UUID.randomUUID().toString();
        File exampleWarc = new File(warcPath);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            step.getWarcRecord(reader, badUuid);
        }
    }
    
    @Test(expectedExceptions = IOException.class)
    public void testGetWarcRecordFailureMissingFile() throws IOException {
        addDescription("Test the GetWarcRecord method when the file is missing.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        File exampleWarc = new File(UUID.randomUUID().toString());
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            step.getWarcRecord(reader, warcRecordId);
        }
    }
    
    @Test
    public void testValidateWarcFileChecksumSuccess() {
        addDescription("Test the ValidateWarcFileChecksum method for success scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        
        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(warcFileChecksum);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        step.validateWarcFileChecksum(record, exampleWarc);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testValidateWarcFileChecksumFailureChecksumMismatch() {
        addDescription("Test the ValidateWarcFileChecksum method when the checksums does not match.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        String badChecksum = UUID.randomUUID().toString();
        
        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(badChecksum);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        step.validateWarcFileChecksum(record, exampleWarc);
    }
    
    @Test
    public void testValidateSizeSuccess() throws IOException {
        addDescription("Test the ValidateSize method when it has expected size.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);

        when(record.getFieldLongValue(eq(Constants.FieldNames.FILE_DATA_SIZE))).thenReturn(warcRecordSize);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            WarcRecord warcRecord = step.getWarcRecord(reader, warcRecordId);
            step.validateSize(warcRecord, record);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testValidateSizeFailure() throws IOException {
        addDescription("Test the ValidateSize method when it does not have the expected size.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        Long badSize = -42L;

        when(record.getFieldLongValue(eq(Constants.FieldNames.FILE_DATA_SIZE))).thenReturn(badSize);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            WarcRecord warcRecord = step.getWarcRecord(reader, warcRecordId);
        
            step.validateSize(warcRecord, record);
        }
    }
    
    @Test
    public void testValidateRecordChecksumSuccess() throws IOException {
        addDescription("Test the ValidateRecordChecksum method when it has the expected checksum.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        
        when(record.getFieldValue(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER))).thenReturn(warcRecordChecksum);        
        when(record.getUUID()).thenReturn(warcRecordId);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            WarcRecord warcRecord = step.getWarcRecord(reader, warcRecordId);
            
            step.validateRecordChecksum(warcRecord, record);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testValidateRecordChecksumFailure() throws IOException {
        addDescription("Test the ValidateRecordChecksum method when it does not have the expected checksum.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        
        when(record.getFieldValue(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER))).thenReturn(warcFileChecksum);        
        when(record.getUUID()).thenReturn(warcRecordId);
        
        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);

        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(exampleWarc))) {
            WarcRecord warcRecord = step.getWarcRecord(reader, warcRecordId);
            
            step.validateRecordChecksum(warcRecord, record);
        }
    }
    
    @Test
    public void testValidateRecordSuccess() throws Exception {
        addDescription("Test the ValidateRecord method for the success scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER))).thenReturn(warcRecordChecksum);        
        when(record.getFieldLongValue(eq(Constants.FieldNames.FILE_DATA_SIZE))).thenReturn(warcRecordSize);
        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(warcFileChecksum);
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(warcRecordId);
        
        when(archive.getFile(eq(warcId), eq(collectionId))).thenReturn(exampleWarc);

        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
        step.validateRecord(record);

        verify(archive).getFile(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER));
        verify(record).getFieldLongValue(eq(Constants.FieldNames.FILE_DATA_SIZE));
        verify(record).getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5));
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
        verify(record, times(2)).getUUID();
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_OK));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
    
    @Test
    public void testValidateRecordFailureValidation() throws Exception {
        addDescription("Test the ValidateRecord method for the scenario when it fails the validation.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(warcPath);
        
        String badChecksum = UUID.randomUUID().toString();
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(badChecksum);
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(warcRecordId);
        
        when(archive.getFile(eq(warcId), eq(collectionId))).thenReturn(exampleWarc);

        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
        step.validateRecord(record);

        verify(archive).getFile(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5));
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
        verify(record).getUUID();
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
    
    @Test
    public void testValidateRecordFailureRetrievingFile() throws Exception {
        addDescription("Test the ValidateRecord method for the scenario when it fails the validation.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        File exampleWarc = new File(UUID.randomUUID().toString());
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(warcRecordId);
        
        when(archive.getFile(eq(warcId), eq(collectionId))).thenReturn(exampleWarc);

        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
        step.validateRecord(record);

        verify(archive).getFile(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
        verify(record).getUUID();
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
//    
//    @Test
//    public void testPerformStep() throws Exception {
//        addDescription("Test the performStep method in the parent class");
//        CumulusServer server = mock(CumulusServer.class);
//        Archive archive = mock(Archive.class);
//        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
//        CumulusRecord record = mock(CumulusRecord.class);
//        File exampleWarc = new File(warcPath);
//        
//        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
//        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();
//
//        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
//        when(archive.getFile(eq(warcId), eq(collectionId))).thenReturn(exampleWarc);
//        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
//        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
//        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
//        when(record.getUUID()).thenReturn(warcRecordId);
//        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(warcFileChecksum);
//        when(record.getFieldLongValue(eq(Constants.FieldNames.FILE_DATA_SIZE))).thenReturn(warcRecordSize);
//        when(record.getFieldValue(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER))).thenReturn(warcRecordChecksum);
//
//        FullValidationStep step = new FullValidationStep(server, catalogName, archive, conf);
//
//        step.performStep();
//        
//        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(archive).getFile(eq(warcId), eq(collectionId));
//        verifyNoMoreInteractions(archive);
//        
//        verify(items).iterator();
//        verifyNoMoreInteractions(items);
//        
//        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
//        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
//        verify(record, times(2)).getUUID();
//        verify(record).getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5));
//        verify(record).getFieldLongValue(eq(Constants.FieldNames.FILE_DATA_SIZE));
//        verify(record).getFieldValue(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER));
//        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
//                eq(Constants.FieldValues.PRESERVATION_VALIDATION_OK));
//        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
//        verifyNoMoreInteractions(record);
//    }
}
