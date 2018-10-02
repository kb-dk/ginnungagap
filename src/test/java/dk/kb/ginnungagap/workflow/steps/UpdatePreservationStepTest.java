package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.StreamUtils;

public class UpdatePreservationStepTest extends ExtendedTestCase {

    String recordGuid = "random-file-uuid";
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    String warcRecordChecksum = "a2919627d81e5e53bf9e2bce13fa44ae";
    Long warcRecordSize = 36L;
    File contentFile;

    String testRecordMetadataPath = "src/test/resources/audio_example_1345.xml";

    Configuration conf;
    String collectionId = "test-collection-id-" + UUID.randomUUID().toString();
    String catalogName = "test-catalog-name";

    @BeforeClass
    public void setupClass() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        contentFile = TestFileUtils.createFileWithContent("This is the content");
    }

    @AfterClass
    public void tearDownClass() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testGetName() {
        addDescription("Test the getName method.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        Assert.assertNotNull(step.getName());
        Assert.assertFalse(step.getName().isEmpty());
    }
    
    @Test
    public void testPerformStepNoRecords() throws Exception {
        addDescription("Test the performStep method, when no records are found");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(records);
        when(records.getCount()).thenReturn(0);
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        step.performStep();
        
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verifyZeroInteractions(preserver);
        
        verifyZeroInteractions(transformationHandler);
        
        verify(records, times(3)).getCount();
        verifyNoMoreInteractions(records);
    }
    
    @Test
    public void testPreserveRecordItemsFailure() {
        addDescription("Test the preserve record items method, with one record which fails trying to retrieve the old preservation update history.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);

        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(items.getCount()).thenReturn(1);
        
        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException("THIS MUST FAIL");
            }
        }).when(record).getFieldValueOrNull(
                eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME));
        
        step.preserveRecordItems(items, catalogName);
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformationHandler);
        
        verify(items).iterator();
        verify(items).getCount();
        verifyNoMoreInteractions(items);
        
        verify(record).getFieldValueOrNull(eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME));
        verify(record, times(2)).getUUID();
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testSendRecordToPreservationSuccessMaster() throws Exception {
        addDescription("Test the sendRecordToPreservation method for the success scenario for a master record.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        MetadataTransformer metsTransformer = mock(MetadataTransformer.class);
        MetadataTransformer ieTransformer = mock(MetadataTransformer.class);
        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(recordGuid);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(UUID.randomUUID().toString());
        when(record.isMasterAsset()).thenReturn(true);
        when(record.getFile()).thenReturn(contentFile);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream out = (OutputStream) invocation.getArguments()[0];
                StreamUtils.copyInputStreamToOutputStream(new FileInputStream(new File(testRecordMetadataPath)), out);
                // TODO Auto-generated method stub
                return null;
            }
        }).when(record).writeFieldMetadata(any(OutputStream.class));
        
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS))).thenReturn(metsTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY))).thenReturn(ieTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION))).thenReturn(representationTransformer);

        step.sendRecordToPreservation(record);

        verifyZeroInteractions(server);

        verify(preserver).packRecordMetadata(eq(record), any(File.class));
        verify(preserver, times(3)).packRepresentationMetadata(any(File.class), anyString());
        verify(preserver).checkConditions();
        verifyNoMoreInteractions(preserver);

        verify(metsTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(metsTransformer);
        
        verify(ieTransformer, times(2)).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(ieTransformer);
        
        verify(representationTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(representationTransformer);
        
        verify(transformationHandler).getMetadataStandards(any(InputStream.class));
        verify(transformationHandler).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS));
        verify(transformationHandler, times(2)).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY));
        verify(transformationHandler).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION));
        verify(transformationHandler, times(4)).validate(any(InputStream.class));
        verifyNoMoreInteractions(transformationHandler);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record, times(2)).getFieldValue(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID));
        verify(record).getFieldValue(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER));
        verify(record).getFile();
        verify(record, times(2)).getFieldValue(eq(Constants.FieldNames.METADATA_GUID));
        verify(record, times(2)).writeFieldMetadata(any(OutputStream.class));
        verify(record, times(3)).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).getUUID();
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER));
        verify(record).setStringValueInField(eq(Constants.FieldNames.METADATA_GUID), anyString());
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY), anyString());
        verify(record).isMasterAsset();
        verify(record).setStringValueInField(eq(Constants.FieldNames.METADATA_GUID), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER), anyString());
        verify(record).validateFieldsExists(any(Collection.class));
        verify(record).validateFieldsHasValue(any(Collection.class));
        verify(record).setStringValueInField(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID), anyString());
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testSetMetadataStandardsForRecord() throws IOException {
        addDescription("Test the setMetadataStandardsForRecord method");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        File testMetadataFile = new File("src/test/resources/test-mets.xml");
        String metadataStandards = UUID.randomUUID().toString();
        
        when(transformationHandler.getMetadataStandards(any(InputStream.class))).thenReturn(Arrays.asList(metadataStandards));
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        step.setMetadataStandardsForRecord(record, testMetadataFile);
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(preserver);
        
        verify(transformationHandler).getMetadataStandards(any(InputStream.class));
        verifyNoMoreInteractions(transformationHandler);
        
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testGetOldMetadataReferenceWhenEmpty() throws IOException {
        addDescription("Test the getOldMetadataReference method when the old preservation update history field is empty");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(record.getFieldValueOrNull(eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME))).thenReturn("");
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        String s = step.getOldMetadataReference(record);
        Assert.assertEquals(s, UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_HEADER);
    }
    
    @Test
    public void testGetOldMetadataReferenceWhenNull() throws IOException {
        addDescription("Test the getOldMetadataReference method when the old preservation update history field is null");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(record.getFieldValueOrNull(eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME))).thenReturn(null);
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        String s = step.getOldMetadataReference(record);
        Assert.assertEquals(s, UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_HEADER);
    }
    
    @Test
    public void testGetOldMetadataReferenceWhenItHasAValue() throws IOException {
        addDescription("Test the getOldMetadataReference method when the old preservation update history field has a value");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String oldHistory = "History: " + UUID.randomUUID().toString();
        when(record.getFieldValueOrNull(eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME))).thenReturn(oldHistory);
        
        UpdatePreservationStep step = new UpdatePreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        String s = step.getOldMetadataReference(record);
        Assert.assertEquals(s, oldHistory);
    }

}

