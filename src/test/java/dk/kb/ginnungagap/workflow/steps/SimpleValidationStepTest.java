package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;
import org.bitrepository.bitrepositoryelements.ChecksumDataForChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumType;
import org.bitrepository.bitrepositoryelements.ResultingChecksums;
import org.bitrepository.common.utils.Base16Utils;
import org.bitrepository.common.utils.CalendarUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.yggdrasil.bitmag.Bitrepository;

public class SimpleValidationStepTest extends ExtendedTestCase {

    String warcRecordId = "random-file-uuid";
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    
    String catalogName = "test-catalog-name";
    
    @Test
    public void testGetName() {
        addDescription("Test the GetName method.");
        CumulusServer server = mock(CumulusServer.class);
        Bitrepository bitmag = mock(Bitrepository.class);
        
        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, bitmag);

        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
    }
    
    @Test
    public void testValidateRecordSuccess() throws Exception {
        addDescription("Test the ValidateRecord method for the success scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Bitrepository bitmag = mock(Bitrepository.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();
        String pillarId = "TEST-PILLAR-ID-" + UUID.randomUUID().toString();
        ChecksumSpecTYPE checksumType = new ChecksumSpecTYPE();
        checksumType.setChecksumType(ChecksumType.MD5);

        when(record.getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5))).thenReturn(warcFileChecksum);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);
        
        ResultingChecksums checksumResults = new ResultingChecksums();
        ChecksumDataForChecksumSpecTYPE checksumResult = new ChecksumDataForChecksumSpecTYPE();
        checksumResult.setCalculationTimestamp(CalendarUtils.getNow());
        checksumResult.setFileID(warcId);
        checksumResult.setChecksumValue(Base16Utils.encodeBase16(warcFileChecksum));
        checksumResults.getChecksumDataItems().add(checksumResult);
        ChecksumsCompletePillarEvent checksumEvents = new ChecksumsCompletePillarEvent(pillarId, collectionId, checksumResults, checksumType, false);
        checksumEvents.setFileID(warcId);
        Map<String, ChecksumsCompletePillarEvent> resultMap = new HashMap<>();
        resultMap.put(pillarId, checksumEvents);
        
        when(bitmag.getChecksums(eq(warcId), eq(collectionId))).thenReturn(resultMap);

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, bitmag);
        step.validateRecord(record);

        verify(bitmag).getChecksums(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(bitmag);
        
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
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
        Bitrepository bitmag = mock(Bitrepository.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        String badChecksum = UUID.randomUUID().toString();
        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();
        String pillarId = "TEST-PILLAR-ID-" + UUID.randomUUID().toString();
        ChecksumSpecTYPE checksumType = new ChecksumSpecTYPE();
        checksumType.setChecksumType(ChecksumType.MD5);

        when(record.getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);
        
        ResultingChecksums checksumResults = new ResultingChecksums();
        ChecksumDataForChecksumSpecTYPE checksumResult = new ChecksumDataForChecksumSpecTYPE();
        checksumResult.setCalculationTimestamp(CalendarUtils.getNow());
        checksumResult.setFileID(warcId);
        checksumResult.setChecksumValue(Base16Utils.encodeBase16(badChecksum));
        checksumResults.getChecksumDataItems().add(checksumResult);
        ChecksumsCompletePillarEvent checksumEvents = new ChecksumsCompletePillarEvent(pillarId, collectionId, checksumResults, checksumType, false);
        checksumEvents.setFileID(warcId);
        Map<String, ChecksumsCompletePillarEvent> resultMap = new HashMap<>();
        resultMap.put(pillarId, checksumEvents);
        
        when(bitmag.getChecksums(eq(warcId), eq(collectionId))).thenReturn(resultMap);

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, bitmag);
        step.validateRecord(record);

        verify(bitmag).getChecksums(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(bitmag);
        
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
        verify(record).getFieldValue(eq(Constants.FieldNames.ARCHIVE_MD5));
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
        Bitrepository bitmag = mock(Bitrepository.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String warcId = "TEST-WARC-ID-" + UUID.randomUUID().toString();
        String collectionId = "TEST-COLLECTION-ID-" + UUID.randomUUID().toString();

        when(record.getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID))).thenReturn(warcId);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);
        
        when(bitmag.getChecksums(eq(warcId), eq(collectionId))).thenThrow(new RuntimeException("YOU MUST FAIL HERE!!!"));

        SimpleValidationStep step = new SimpleValidationStep(server, catalogName, bitmag);
        step.validateRecord(record);

        verify(bitmag).getChecksums(eq(warcId), eq(collectionId));
        verifyNoMoreInteractions(bitmag);
        
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK), 
                eq(Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_CHECK_STATUS), anyString());
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(server);
    }
}
