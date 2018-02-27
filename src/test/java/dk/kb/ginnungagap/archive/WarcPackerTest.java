package dk.kb.ginnungagap.archive;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.jwat.common.ContentType;
import org.jwat.common.Uri;
import org.jwat.warc.WarcDigest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.yggdrasil.warc.Digest;

public class WarcPackerTest extends ExtendedTestCase {

    File testFile;
    BitmagConfiguration conf;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        
        File origTestFile = new File("src/test/resources/test-resource.txt");
        testFile = new File(TestFileUtils.getTempDir(), origTestFile.getName());
        FileUtils.copyFile(origTestFile, testFile);
        
        conf = new BitmagConfiguration(TestFileUtils.getTempDir(), null, 1, 10000000, TestFileUtils.getTempDir(), "SHA-1");
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testPackaging() throws Exception {
        addDescription("Test succes case");
        
        WarcPacker wp = new WarcPacker(conf);
        Digest digestor = new Digest(conf.getAlgorithm());
        WarcDigest wd = digestor.getDigestOfFile(testFile);
        wp.packResource(testFile, wd, ContentType.parseContentType("application/octetstream"), UUID.randomUUID().toString());
        wp.packMetadata(testFile, new Uri("urn:uuid:" + UUID.randomUUID().toString()));
        
        assertTrue(wp.getSize() > 0);
        assertTrue(wp.getSize() > 2 * testFile.length()); 
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailedInstantiationDueToNoWriteAccess() throws Exception {
        addDescription("Test failure to instantiate the warc packer, due to missing write access to the folder.");
        
        try {
            TestFileUtils.getTempDir().setWritable(false);
            new WarcPacker(conf);
        } finally {
            TestFileUtils.getTempDir().setWritable(true);            
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailureToWriteMissingFileAsMetadata() throws Exception {
        addDescription("Test failure to write a missing file as metadata");
        
        WarcPacker wp = new WarcPacker(conf);
        wp.packMetadata(new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()), new Uri("urn:uuid:" + UUID.randomUUID().toString()));
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailureToWriteMissingFileAsResource() throws Exception {
        addDescription("Test failure to write a missing file as resource");
        
        WarcPacker wp = new WarcPacker(conf);
        Digest digestor = new Digest(conf.getAlgorithm());
        WarcDigest wd = digestor.getDigestOfFile(testFile);
        wp.packResource(new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()), wd, ContentType.parseContentType("application/octetstream"), "THIS IS THE UUID");
    }
    
    @Test(expectedExceptions = IllegalStateException.class, enabled = false)
    public void testFailureToClose() throws Exception {
        WarcPacker wp = new WarcPacker(conf);
        assertTrue(wp.getWarcFile().delete());
        wp.close();
    }
    
    @Test
    public void testPackRecordAssetFile() {
        addDescription("Test the packRecordAssetFile method");
        WarcPacker wp = new WarcPacker(conf);
        
        long origSize = wp.getWarcFile().length();
        
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getUUID()).thenReturn(UUID.randomUUID().toString());
        
        wp.packRecordAssetFile(record, testFile);
        
        Assert.assertTrue(wp.getWarcFile().length() > origSize + testFile.length());
    }
    
    @Test
    public void testReportSucces() throws Exception {
        addDescription("Test the reportSucces method");
        WarcPacker wp = new WarcPacker(conf);
        
        CumulusRecord record = mock(CumulusRecord.class);
        WarcDigest digest = new Digest("MD5").getDigestOfBytes("TEST".getBytes());
        wp.packagedRecords.add(record);
        wp.reportSucces(digest);
        
        verify(record).setStringValueInField(eq(Constants.FieldNames.METADATA_PACKAGE_ID), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.ARCHIVE_MD5), anyString());
        verify(record).setDateValueInField(eq(Constants.FieldNames.BEVARINGS_DATO), any(Date.class));
        verify(record).setStringEnumValueForField(eq(Constants.FieldNames.PRESERVATION_STATUS), 
                eq(Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_COMPLETED));
        verify(record).setStringValueInField(eq(Constants.FieldNames.QA_ERROR), eq(""));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testReportFailure() throws Exception {
        addDescription("Test the reportFailure method");
        WarcPacker wp = new WarcPacker(conf);
        
        String failureMessage = "THIS MUST FAIL!!!";
        CumulusRecord record = mock(CumulusRecord.class);
        wp.packagedRecords.add(record);
        wp.reportFailure(failureMessage);
        
        verify(record).setStringEnumValueForField(eq(Constants.FieldNames.PRESERVATION_STATUS), 
                eq(Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_FAILED));
        verify(record).setStringValueInField(eq(Constants.FieldNames.QA_ERROR), eq(failureMessage));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testAddRecordToPackagedList() throws Exception {
        addDescription("Test the addRecordToPackagedList method");
        WarcPacker wp = new WarcPacker(conf);
        CumulusRecord record = mock(CumulusRecord.class);
        
        Assert.assertTrue(wp.packagedRecords.isEmpty());
        wp.addRecordToPackagedList(record);
        Assert.assertFalse(wp.packagedRecords.isEmpty());
        Assert.assertEquals(wp.packagedRecords.size(), 1);
        Assert.assertTrue(wp.packagedRecords.contains(record));

        wp.addRecordToPackagedList(record);
        Assert.assertEquals(wp.packagedRecords.size(), 1);
    }
    
    @Test
    public void testGetContentType() throws Exception {
        addDescription("Test the getContentType method");
        WarcPacker wp = new WarcPacker(conf);
        CumulusRecord record = mock(CumulusRecord.class);
        
        addStep("record has neither FILE_FORMAT_IDENTIFIER nor FORMAT_NAME", "application/binary");
        ContentType ct = wp.getContentType(record);
        Assert.assertEquals(ct.contentType, "application");
        Assert.assertEquals(ct.mediaType, "binary");
        
        addStep("record has FILE_FORMAT_IDENTIFIER = text/xml and FORMAT_NAME = null", "text/xml");
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FILE_FORMAT_IDENTIFIER))).thenReturn("text/xml");
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FORMAT_NAME))).thenReturn(null);
        ct = wp.getContentType(record);
        Assert.assertEquals(ct.contentType, "text");
        Assert.assertEquals(ct.mediaType, "xml");
        
        addStep("record has FILE_FORMAT_IDENTIFIER = image/tiff and FORMAT_NAME = null", "image/tiff");
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FILE_FORMAT_IDENTIFIER))).thenReturn("image/tiff");
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FORMAT_NAME))).thenReturn(null);
        ct = wp.getContentType(record);
        Assert.assertEquals(ct.contentType, "image");
        Assert.assertEquals(ct.mediaType, "tiff");
        
        addStep("record has FILE_FORMAT_IDENTIFIER = null and FORMAT_NAME = TIFF", "image/tiff");
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FILE_FORMAT_IDENTIFIER))).thenReturn(null);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FORMAT_NAME))).thenReturn("TIFF");
        ct = wp.getContentType(record);
        Assert.assertEquals(ct.contentType, "image");
        Assert.assertEquals(ct.mediaType, "tiff");

        addStep("record has FILE_FORMAT_IDENTIFIER = null and FORMAT_NAME = random", "application/binary");
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FILE_FORMAT_IDENTIFIER))).thenReturn(null);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.FORMAT_NAME))).thenReturn(UUID.randomUUID().toString());
        ct = wp.getContentType(record);
        Assert.assertEquals(ct.contentType, "application");
        Assert.assertEquals(ct.mediaType, "binary");
    }
    
    @Test
    public void testClose() {
        addDescription("Test the addRecordToPackagedList method");
        WarcPacker wp = new WarcPacker(conf);
        
        wp.close();
    }
}
