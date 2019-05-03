package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import dk.kb.ginnungagap.workflow.reporting.WorkflowReport;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.utils.FileUtils;

public class ImportationStepTest extends ExtendedTestCase {

    String recordGuid = "random-file-uuid";
    String warcFileId = "random-warc-id-" + UUID.randomUUID().toString();
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    String warcRecordChecksum = "a2919627d81e5e53bf9e2bce13fa44ae";
    Long warcRecordSize = 36L;

    String testRecordMetadataPath = "src/test/resources/audio_example_1345.xml";
    String warcResourcePath = "src/test/resources/warc/warcexample.warc";

    Configuration conf;
    String collectionId = "test-collection-id-" + UUID.randomUUID().toString();
    String catalogName = "test-catalog-name";
    File retainDir;

    @BeforeClass
    public void setupClass() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        retainDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), "retainDir");
    }

    @AfterClass
    public void tearDownClass() {
        TestFileUtils.tearDown();
    }

    @BeforeMethod
    public void setupMethod() {
        if(retainDir != null && retainDir.list().length > 0) {
            for(File f : retainDir.listFiles()) {
                FileUtils.deleteFile(f);
            }
        }
    }
    
    @Test
    public void testGetName() {
        addDescription("Test the GetName method.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        
        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);

        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
    }
    
    @Test
    public void testImportRecordSuccess() throws Exception {
        addDescription("Test the importation of a record for the succes scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        WorkflowReport report = mock(WorkflowReport.class);

        File outFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(recordGuid);
        when(record.getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE))).thenReturn(outFile.getAbsolutePath());
        
        when(archive.getFile(eq(warcFileId), eq(collectionId))).thenReturn(new File(warcResourcePath));
        
        step.importRecord(record, report);

        verify(report).addSuccessRecord(anyString(), anyString());
        verifyNoMoreInteractions(report);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_NONE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verify(record).getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE));
        verify(record).setNewAssetReference(any(File.class));
        verifyNoMoreInteractions(record);

        verify(archive).getFile(eq(warcFileId), eq(collectionId));
        verifyNoMoreInteractions(archive);

        verifyZeroInteractions(server);
    }
    
    @Test
    public void testImportRecordFailedToFindWarcRecord() throws Exception {
        addDescription("Test the importation of a record, when it cannot find the given warc record.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        WorkflowReport report = mock(WorkflowReport.class);

        String sillyId = UUID.randomUUID().toString();
        
        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(sillyId);
        
        when(archive.getFile(eq(warcFileId), eq(collectionId))).thenReturn(new File(warcResourcePath));
        
        step.importRecord(record, report);

        verify(report).addFailedRecord(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(report);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verifyNoMoreInteractions(record);

        verify(archive).getFile(eq(warcFileId), eq(collectionId));
        verifyNoMoreInteractions(archive);

        verifyZeroInteractions(server);
    }
    
    @Test
    public void testImportRecordFailedToGetWarcFileFromArchive() throws Exception {
        addDescription("Test the importation of a record, when it cannot retrieve the WARC file from the archive.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        WorkflowReport report = mock(WorkflowReport.class);

        String sillyId = UUID.randomUUID().toString();
        
        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(sillyId);
        
        when(archive.getFile(eq(warcFileId), eq(collectionId))).thenThrow(new IllegalStateException("This must fail!!"));
        
        step.importRecord(record, report);

        verify(report).addFailedRecord(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(report);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verifyNoMoreInteractions(record);

        verify(archive).getFile(eq(warcFileId), eq(collectionId));
        verifyNoMoreInteractions(archive);

        verifyZeroInteractions(server);
    }
    
    @Test
    public void testPerformStepWithNoRecords() throws Exception {
        addDescription("Test the importation of a record, when it cannot retrieve the WARC file from the archive.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        WorkflowReport report = mock(WorkflowReport.class);

        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        
        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.iterator()).thenReturn(new ArrayList<CumulusRecord>().iterator());
        
        step.performStep(report);

        verifyZeroInteractions(report);

        verifyZeroInteractions(archive);
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);
    }
    
    @Test
    public void testPerformStepWithOneBadRecord() throws Exception {
        addDescription("Test the importation of a bad record, which fails when trying to import.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        WorkflowReport report = mock(WorkflowReport.class);

        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenThrow(new RuntimeException("MUST FAIL"));
        
        step.performStep(report);

        verify(report).addFailedRecord(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(report);

        verifyZeroInteractions(archive);
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);

        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testDeprecateFileWhenFileExists() throws Exception {
        addDescription("Test the deprecateFile method, when the file exists. It should be moved to the retain folder.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        WorkflowReport report = mock(WorkflowReport.class);

        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);
        Assert.assertEquals(retainDir.list().length, 0);
        
        File testFile = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        Assert.assertTrue(testFile.exists());
        
        step.deprecateFile(testFile);
        
        Assert.assertEquals(retainDir.list().length, 1);
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(archive);
    }
    
    @Test
    public void testDeprecateFileWhenFileDoesNotExists() throws Exception {
        addDescription("Test the deprecateFile method, when the file does not exist. It should not be moved to the retain folder.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        
        ImportationStep step = new ImportationStep(server, archive, catalogName, retainDir);
        Assert.assertEquals(retainDir.list().length, 0);
        
        File testFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        Assert.assertFalse(testFile.exists());
        
        step.deprecateFile(testFile);
        
        Assert.assertEquals(retainDir.list().length, 0);
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(archive);
    }
}
