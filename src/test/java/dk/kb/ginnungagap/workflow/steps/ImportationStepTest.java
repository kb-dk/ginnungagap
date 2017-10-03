package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bitrepository.bitrepositoryelements.FilePart;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.yggdrasil.bitmag.Bitrepository;
import dk.kb.yggdrasil.exceptions.YggdrasilException;

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
        Bitrepository bitmag = mock(Bitrepository.class);
        
        ImportationStep step = new ImportationStep(server, bitmag, catalogName);

        String name = step.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
    }
    
    @Test
    public void testImportRecordSuccess() throws Exception {
        addDescription("Test the importation of a record for the succes scenario.");
        CumulusServer server = mock(CumulusServer.class);
        Bitrepository bitmag = mock(Bitrepository.class);
        
        File outFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        ImportationStep step = new ImportationStep(server, bitmag, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(recordGuid);
        when(record.getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE))).thenReturn(outFile.getAbsolutePath());
        
        when(bitmag.getFile(eq(warcFileId), eq(collectionId), isNull(FilePart.class))).thenReturn(new File(warcResourcePath));
        
        step.importRecord(record);

        verify(record, times(2)).getUUID();
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_NONE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verify(record).getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE));
        verify(record).setNewAssetReference(any(File.class));
        verifyNoMoreInteractions(record);

        verify(bitmag).getFile(eq(warcFileId), eq(collectionId), isNull(FilePart.class));
        verifyNoMoreInteractions(bitmag);

        verifyZeroInteractions(server);
    }
    
    @Test
    public void testImportRecordFailedToFindWarcRecord() throws Exception {
        addDescription("Test the importation of a record, when it cannot find the given warc record.");
        CumulusServer server = mock(CumulusServer.class);
        Bitrepository bitmag = mock(Bitrepository.class);
        
        String sillyId = UUID.randomUUID().toString();
        
        ImportationStep step = new ImportationStep(server, bitmag, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(sillyId);
        
        when(bitmag.getFile(eq(warcFileId), eq(collectionId), isNull(FilePart.class))).thenReturn(new File(warcResourcePath));
        
        step.importRecord(record);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verifyNoMoreInteractions(record);

        verify(bitmag).getFile(eq(warcFileId), eq(collectionId), isNull(FilePart.class));
        verifyNoMoreInteractions(bitmag);

        verifyZeroInteractions(server);
    }
    
    @Test
    public void testImportRecordFailedToGetWarcFileFromArchive() throws Exception {
        addDescription("Test the importation of a record, when it cannot retrieve the WARC file from the archive.");
        CumulusServer server = mock(CumulusServer.class);
        Bitrepository bitmag = mock(Bitrepository.class);
        
        String sillyId = UUID.randomUUID().toString();
        
        ImportationStep step = new ImportationStep(server, bitmag, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID))).thenReturn(warcFileId);
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(sillyId);
        
        when(bitmag.getFile(eq(warcFileId), eq(collectionId), isNull(FilePart.class))).thenThrow(new YggdrasilException("This must fail!!"));
        
        step.importRecord(record);

        verify(record).getUUID();
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.RESOURCEPACKAGEID));
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION), 
                eq(Constants.FieldValues.PRESERVATION_IMPORT_FAILURE));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARING_IMPORTATION_STATUS), anyString());
        verifyNoMoreInteractions(record);

        verify(bitmag).getFile(eq(warcFileId), eq(collectionId), isNull(FilePart.class));
        verifyNoMoreInteractions(bitmag);

        verifyZeroInteractions(server);
    }
    
    @Test
    public void testPerformStepWithNoRecords() throws Exception {
        addDescription("Test the importation of a record, when it cannot retrieve the WARC file from the archive.");
        CumulusServer server = mock(CumulusServer.class);
        Bitrepository bitmag = mock(Bitrepository.class);
        
        ImportationStep step = new ImportationStep(server, bitmag, catalogName);
        
        RecordItemCollection ric = mock(RecordItemCollection.class);
        Layout layout = mock(Layout.class);
        when(ric.getLayout()).thenReturn(layout);
        when(ric.iterator()).thenReturn((new ArrayList<Item>()).iterator());
        
        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(ric);
        
        step.performStep();
        
        verifyZeroInteractions(bitmag);
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
    }
}
