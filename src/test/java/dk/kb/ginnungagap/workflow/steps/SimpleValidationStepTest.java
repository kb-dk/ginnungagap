package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;

public class SimpleValidationStepTest extends ExtendedTestCase {

    String warcRecordId = "random-file-uuid";
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    
    String catalogName = "test-catalog-name";
    
    @Test
    public void testGetName() {
        addDescription("Test the GetName method.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        
        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, archive);

        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
    }
    
    @Test
    public void testValidateRecordSuccess() throws Exception {
        addDescription("Test the ValidateRecord method for the success scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(warcFileChecksum);
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        
        when(archive.getChecksum(eq(warcId), eq(collectionId))).thenReturn(warcFileChecksum);

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, archive);
        step.validateRecord(record);

        verify(archive).getChecksum(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5));
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
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();
        String notTheExpectedChecksum = UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(warcFileChecksum);
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);

        when(archive.getChecksum(eq(warcId), eq(collectionId))).thenReturn(notTheExpectedChecksum);

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, archive);
        step.validateRecord(record);

        verify(archive).getChecksum(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verify(record).getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5));
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
    
    @Test
    public void testValidateRecordFailureNoChecksum() throws Exception {
        addDescription("Test the ValidateRecord method for the scenario when it fails to get the checksum for the validation.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();
        
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);

        when(archive.getChecksum(eq(warcId), eq(collectionId))).thenThrow(new IllegalStateException("CHECKSUM FAILURE."));

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, archive);
        step.validateRecord(record);

        verify(archive).getChecksum(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
    
    @Test
    public void testValidateRecordFailureRetrievingFile() throws Exception {
        addDescription("Test the ValidateRecord method for the scenario when it throws an error during validation.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        
        when(archive.getChecksum(eq(warcId), eq(collectionId))).thenThrow(new RuntimeException("YOU MUST FAIL HERE!!!"));

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, archive);
        step.validateRecord(record);

        verify(archive).getChecksum(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(archive);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
    
    @Test
    public void testPerformStep() throws Exception {
        addDescription("Test the perform step method");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        CumulusRecord record = mock(CumulusRecord.class);
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(items);
        
        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, archive);

        step.performStep();
    }
}
