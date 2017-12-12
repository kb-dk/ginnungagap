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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;

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
        
        ImportationStep step = new ImportationStep(server, archive, catalogName);

        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
    }
    
    @Test
    public void testImportRecordSuccess() throws Exception {
        addDescription("Test the importation of a record for the succes scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Archive archive = mock(Archive.class);
        
        File outFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        ImportationStep step = new ImportationStep(server, archive, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(recordGuid);
        when(record.getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE))).thenReturn(outFile.getAbsolutePath());
        
        when(archive.getFile(eq(warcFileId), eq(collectionId))).thenReturn(new File(warcResourcePath));
        
        step.importRecord(record);

        verify(record, times(2)).getUUID();
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
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
        
        String sillyId = UUID.randomUUID().toString();
        
        ImportationStep step = new ImportationStep(server, archive, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(sillyId);
        
        when(archive.getFile(eq(warcFileId), eq(collectionId))).thenReturn(new File(warcResourcePath));
        
        step.importRecord(record);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
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
        
        String sillyId = UUID.randomUUID().toString();
        
        ImportationStep step = new ImportationStep(server, archive, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(sillyId);
        
        when(archive.getFile(eq(warcFileId), eq(collectionId))).thenThrow(new IllegalStateException("This must fail!!"));
        
        step.importRecord(record);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTIONID));
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
        
        ImportationStep step = new ImportationStep(server, archive, catalogName);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        
        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.iterator()).thenReturn(new ArrayList<CumulusRecord>().iterator());
        
        step.performStep();
        
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
        
        ImportationStep step = new ImportationStep(server, archive, catalogName);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID))).thenThrow(new RuntimeException("MUST FAIL"));
        
        step.performStep();
        
        verifyZeroInteractions(archive);
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCEPACKAGEID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verifyNoMoreInteractions(record);
    }
}
