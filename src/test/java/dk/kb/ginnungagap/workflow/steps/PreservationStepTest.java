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
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

public class PreservationStepTest extends ExtendedTestCase {

    String recordGuid = "random-file-uuid";
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    String warcRecordChecksum = "a2919627d81e5e53bf9e2bce13fa44ae";
    Long warcRecordSize = 36L;

    String testRecordMetadataPath = "src/test/resources/audio_example_1345.xml";

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
        addDescription("Test the getName method.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        Assert.assertNotNull(step.getName());
        Assert.assertFalse(step.getName().isEmpty());
    }
    
    @Test
    public void testPerformStep() throws Exception {
        addDescription("Test the performStep method, when no records are found");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        
        CumulusRecordCollection records = mock(CumulusRecordCollection.class);
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(records);
        when(records.getCount()).thenReturn(0);
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        step.performStep();
        
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verifyZeroInteractions(preserver);
        
        verifyZeroInteractions(transformationHandler);
        
        verify(records, times(2)).getCount();
        verifyNoMoreInteractions(records);
    }
    
    @Test
    public void testPreserveRecordItems() {
        addDescription("Test the preserve record items method, with one record which fails when trying to be preserved.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);

        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(items.getCount()).thenReturn(1);
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new RuntimeException("THIS MUST FAIL");
            }
        }).when(record).initFieldsForPreservation();
        
        step.preserveRecordItems(items, catalogName);
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformationHandler);
        
        verify(items).iterator();
        verify(items).getCount();
        verifyNoMoreInteractions(items);
        
        verify(record).initFieldsForPreservation();
        verify(record).setPreservationFailed(anyString());
        verify(record, times(2)).getUUID();
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testSendRecordToPreservationSuccessMaster() throws IOException {
        addDescription("Test the sendRecordToPreservation method for the success scenario for a master record.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        MetadataTransformer metsTransformer = mock(MetadataTransformer.class);
        MetadataTransformer ieTransformer = mock(MetadataTransformer.class);
        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        when(record.getMetadataGUID()).thenReturn(recordGuid);
        when(record.getMetadata(any(File.class))).thenReturn(new FileInputStream(new File(testRecordMetadataPath)));
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(UUID.randomUUID().toString());
        when(record.isMasterAsset()).thenReturn(true);
        
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS))).thenReturn(metsTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY))).thenReturn(ieTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION))).thenReturn(representationTransformer);

        step.sendRecordToPreservation(record);

        verifyZeroInteractions(server);

        verify(preserver).packRecordResource(eq(record));
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
        verify(record, times(2)).getMetadataGUID();
        verify(record, times(2)).getMetadata(any(File.class));
        verify(record, times(3)).getPreservationCollectionID();
        verify(record).getUUID();
        verify(record).initFieldsForPreservation();
        verify(record).initRepresentationFields();
        verify(record).isMasterAsset();
        verify(record).resetMetadataGuid();
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verify(record).validateRequiredFields(any(RequiredFields.class));
        verify(record).resetRepresentationMetadataGuid();
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testSendRecordToPreservationSuccessNonMaster() throws IOException {
        addDescription("Test the sendRecordToPreservation method for the success scenario for a non-master record.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        MetadataTransformer metsTransformer = mock(MetadataTransformer.class);
        MetadataTransformer ieTransformer = mock(MetadataTransformer.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        when(record.getMetadataGUID()).thenReturn(recordGuid);
        when(record.getMetadata(any(File.class))).thenReturn(new FileInputStream(new File(testRecordMetadataPath)));
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.isMasterAsset()).thenReturn(false);
        
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS))).thenReturn(metsTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY))).thenReturn(ieTransformer);

        step.sendRecordToPreservation(record);

        verifyZeroInteractions(server);

        verify(preserver).packRecordResource(eq(record));
        verify(preserver).packRecordMetadata(eq(record), any(File.class));
        verify(preserver).packRepresentationMetadata(any(File.class), anyString());
        verify(preserver).checkConditions();
        verifyNoMoreInteractions(preserver);

        verify(metsTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(metsTransformer);
        
        verify(ieTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(ieTransformer);
        
        verify(transformationHandler).getMetadataStandards(any(InputStream.class));
        verify(transformationHandler).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS));
        verify(transformationHandler).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY));
        verify(transformationHandler, times(2)).validate(any(InputStream.class));
        verifyNoMoreInteractions(transformationHandler);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record, times(2)).getMetadataGUID();
        verify(record).getMetadata(any(File.class));
        verify(record).getPreservationCollectionID();
        verify(record).getUUID();
        verify(record).initFieldsForPreservation();
        verify(record).isMasterAsset();
        verify(record).resetMetadataGuid();
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verify(record).validateRequiredFields(any(RequiredFields.class));
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
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        step.setMetadataStandardsForRecord(record, testMetadataFile);
        
        verifyZeroInteractions(server);
        verifyZeroInteractions(preserver);
        
        verify(transformationHandler).getMetadataStandards(any(InputStream.class));
        verifyNoMoreInteractions(transformationHandler);
        
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verifyNoMoreInteractions(record);
    }
    
    @Test(expectedExceptions = IOException.class)
    public void testTransformAndPreserveIntellectualEntityFailureIERawFile() throws IOException {
        addDescription("Test the transformAndPreserveIntellectualEntity method when the IE raw file cannot be read");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String ieUUID = UUID.randomUUID().toString();
        
        File ieRawFile = new File(conf.getTransformationConf().getMetadataTempDir(), ieUUID + PreservationStep.RAW_FILE_SUFFIX);
        Assert.assertTrue(ieRawFile.createNewFile());
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        try {
            ieRawFile.setReadable(false);
            step.transformAndPreserveIntellectualEntity(ieUUID, UUID.randomUUID().toString(), UUID.randomUUID().toString(), record);
        } finally {
            ieRawFile.setReadable(true);            
        }
    }
    
    @Test(expectedExceptions = IOException.class)
    public void testTransformAndPreserveIntellectualEntityFailureWriteIEMetadataFile() throws IOException {
        addDescription("Test the TransformAndPreserveIntellectualEntity method when the IE metadata file cannot be written");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String ieUUID = UUID.randomUUID().toString();
        
        File ieMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), ieUUID);
        Assert.assertTrue(ieMetadataFile.createNewFile());
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        try {
            ieMetadataFile.setWritable(false);
            step.transformAndPreserveIntellectualEntity(ieUUID, UUID.randomUUID().toString(), UUID.randomUUID().toString(), record);
        } finally {
            ieMetadataFile.setWritable(true);            
        }
    }
    
    @Test(expectedExceptions = IOException.class)
    public void testTransformAndPreserveIntellectualEntityFailureReadIEMetadataFile() throws IOException {
        addDescription("Test the TransformAndPreserveIntellectualEntity method when the IE metadata file cannot be written");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String ieUUID = UUID.randomUUID().toString();
        
        File ieMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), ieUUID);
        Assert.assertTrue(ieMetadataFile.createNewFile());
        
        when(transformationHandler.getTransformer(anyString())).thenReturn(transformer);
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        try {
            ieMetadataFile.setReadable(false);
            step.transformAndPreserveIntellectualEntity(ieUUID, UUID.randomUUID().toString(), UUID.randomUUID().toString(), record);
        } finally {
            ieMetadataFile.setReadable(true);            
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateIErawFileFailure() throws IOException {
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);

        String ieUUID = UUID.randomUUID().toString();
        
        File ieMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), ieUUID + PreservationStep.RAW_FILE_SUFFIX);
        Assert.assertTrue(ieMetadataFile.createNewFile());
        
        PreservationStep step = new PreservationStep(conf.getTransformationConf(), server, transformationHandler, preserver, catalogName);
        
        try {
            ieMetadataFile.setWritable(false);
            step.createIErawFile(ieUUID, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        } finally {
            ieMetadataFile.setWritable(true);
        }
    }
}
