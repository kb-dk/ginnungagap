package dk.kb.ginnungagap.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;
import org.bitrepository.bitrepositoryelements.ChecksumDataForChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumType;
import org.bitrepository.bitrepositoryelements.ResultingChecksums;
import org.bitrepository.client.eventhandler.OperationEvent.OperationEventType;
import org.bitrepository.common.utils.Base16Utils;
import org.jaccept.structure.ExtendedTestCase;
import org.jwat.warc.WarcDigest;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class ChecksumUtilsTest extends ExtendedTestCase {

    String testFilePath = "src/test/resources/test-resource.txt";
    String expectedChecksum = "37e9a7db97d6050911038d72b0f0585c";
    String defaultFileID = "DEFAULT-FILE-ID-" + UUID.randomUUID().toString();
    
    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        ChecksumUtils c = new ChecksumUtils();
        Assert.assertNotNull(c);
    }
    
    @Test
    public void testCalculateChecksumSuccess() {
        addDescription("Test the success case for calculating the checksum.");
        File f = new File(testFilePath);
        Assert.assertTrue(f.isFile());
        WarcDigest digest = ChecksumUtils.calculateChecksum(f, "md5");
        
        Assert.assertEquals(expectedChecksum, digest.digestString);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testCalculateChecksumFailureChecksumAlgorith() {
        addDescription("Test calculating the checksum with an unknown algorithm.");
        File f = new File(testFilePath);
        Assert.assertTrue(f.isFile());
        ChecksumUtils.calculateChecksum(f, "md6");
    }
    
    @Test
    public void testGetAgreedChecksumSuccess() {
        addDescription("Test the GetAgreedChecksum method in the success scenario.");
        List<ChecksumsCompletePillarEvent> checksumEvents = new ArrayList<ChecksumsCompletePillarEvent>();
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, expectedChecksum, ChecksumType.MD5));
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, expectedChecksum, ChecksumType.MD5));
        
        String checksum = ChecksumUtils.getAgreedChecksum(checksumEvents);
        Assert.assertEquals(expectedChecksum, checksum);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgreedChecksumFailureEventFailure() {
        addDescription("Test the GetAgreedChecksum method in the failure scenario when one event has a failure event.");
        List<ChecksumsCompletePillarEvent> checksumEvents = new ArrayList<ChecksumsCompletePillarEvent>();
        ChecksumsCompletePillarEvent event = makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, expectedChecksum, ChecksumType.MD5);
        event.setEventType(OperationEventType.COMPONENT_FAILED);
        
        checksumEvents.add(event);
        
        ChecksumUtils.getAgreedChecksum(checksumEvents);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgreedChecksumFailureEmpty() {
        addDescription("Test the GetAgreedChecksum method in the failure scenario when no events are given.");
        List<ChecksumsCompletePillarEvent> checksumEvents = new ArrayList<ChecksumsCompletePillarEvent>();
        
        ChecksumUtils.getAgreedChecksum(checksumEvents);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgreedChecksumFailureDifferentChecksumSpecs() {
        addDescription("Test the GetAgreedChecksum method in the failure scenario when different checksum specification has been used.");
        List<ChecksumsCompletePillarEvent> checksumEvents = new ArrayList<ChecksumsCompletePillarEvent>();
 
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, expectedChecksum, ChecksumType.MD5));
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, expectedChecksum, ChecksumType.SHA1));
       
        ChecksumUtils.getAgreedChecksum(checksumEvents);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgreedChecksumFailureDifferentFileIDs() {
        addDescription("Test the GetAgreedChecksum method in the failure scenario when different file ids are returned");
        List<ChecksumsCompletePillarEvent> checksumEvents = new ArrayList<ChecksumsCompletePillarEvent>();
 
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID + "1", expectedChecksum, ChecksumType.MD5));
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID + "2", expectedChecksum, ChecksumType.MD5));
       
        ChecksumUtils.getAgreedChecksum(checksumEvents);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetAgreedChecksumFailureDifferentChecksums() {
        addDescription("Test the GetAgreedChecksum method in the failure scenario when different checksums are returned");
        List<ChecksumsCompletePillarEvent> checksumEvents = new ArrayList<ChecksumsCompletePillarEvent>();
 
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, UUID.randomUUID().toString(), ChecksumType.MD5));
        checksumEvents.add(makeEvent(UUID.randomUUID().toString(), UUID.randomUUID().toString(), defaultFileID, UUID.randomUUID().toString(), ChecksumType.MD5));
       
        ChecksumUtils.getAgreedChecksum(checksumEvents);
    }
    
    protected ChecksumsCompletePillarEvent makeEvent(String pillarID, String collectionID, String fileId, String checksum, ChecksumType csType) {
        ChecksumSpecTYPE checksumSpec = new ChecksumSpecTYPE();
        checksumSpec.setChecksumType(csType);
        
        ChecksumDataForChecksumSpecTYPE resultContent = new ChecksumDataForChecksumSpecTYPE();
        resultContent.setFileID(fileId);
        resultContent.setCalculationTimestamp(org.bitrepository.common.utils.CalendarUtils.getNow());
        resultContent.setChecksumValue(Base16Utils.encodeBase16(checksum));
        
        ResultingChecksums result = new ResultingChecksums();
        result.getChecksumDataItems().add(resultContent);
        ChecksumsCompletePillarEvent event = new ChecksumsCompletePillarEvent(pillarID, collectionID, result, checksumSpec, false);
        event.setFileID(fileId);
        return event;
    }
}