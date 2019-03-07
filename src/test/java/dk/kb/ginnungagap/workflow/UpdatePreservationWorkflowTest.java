package dk.kb.ginnungagap.workflow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
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
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.workflow.steps.UpdatePreservationStep;

public class UpdatePreservationWorkflowTest extends ExtendedTestCase {

    TestConfiguration conf;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testSingleItemSuccess() throws Exception {
        addDescription("Test preservation update of a single record");
        CumulusServer server = mock(CumulusServer.class);
        CumulusWrapper cumulusWrapper = mock(CumulusWrapper.class);
        MetadataTransformer metsTransformer = mock(MetadataTransformer.class);
        MetadataTransformer ieTransformer = mock(MetadataTransformer.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);

        String catalogName = "Catalog-" + UUID.randomUUID().toString();

        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class)))
                .thenReturn(items);
        when(server.getCatalogNames()).thenReturn(Arrays.asList(catalogName));
        
        when(cumulusWrapper.getServer()).thenReturn(server);
        
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(items.getCount()).thenReturn(1);
        
        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER))).thenReturn(UUID.randomUUID().toString());
        when(record.isMasterAsset()).thenReturn(false);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream out = (OutputStream) invocation.getArguments()[0];
                out.write(UUID.randomUUID().toString().getBytes());
                // TODO Auto-generated method stub
                return null;
            }
        }).when(record).writeFieldMetadata(any(OutputStream.class));

        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS))).thenReturn(metsTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY))).thenReturn(ieTransformer);
        
        UpdatePreservationWorkflow workflow = new UpdatePreservationWorkflow();
        workflow.conf = conf;
        workflow.cumulusWrapper = cumulusWrapper;
        workflow.transformationHandler = transformationHandler;
        workflow.preserver = preserver;
        workflow.init();

        workflow.runWorkflowSteps();
        
        verify(server).getItems(eq(conf.getCumulusConf().getCatalogs().get(0)), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(cumulusWrapper).getServer();
        verifyNoMoreInteractions(cumulusWrapper);
        
        verify(metsTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(metsTransformer);
        
        verify(ieTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(ieTransformer);
        
        verify(transformationHandler).getMetadataStandards(any(InputStream.class));
        verify(transformationHandler).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS));
        verify(transformationHandler).getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY));
        verify(transformationHandler, times(2)).validate(any(InputStream.class));
        verifyNoMoreInteractions(transformationHandler);

        verify(preserver, times(0)).packRecordResource(any(CumulusRecord.class));
        verify(preserver).packRecordMetadata(any(CumulusRecord.class), any(File.class));
        verify(preserver).packRepresentationMetadata(any(File.class), anyString(), anyString());
        verify(preserver).checkConditions();
        verify(preserver).uploadAll();
        verifyNoMoreInteractions(preserver);
        
        verify(items, times(3)).getCount();
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record, times(3)).getFieldValue(eq(Constants.FieldNames.METADATA_GUID));
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValue(eq(Constants.FieldNames.METADATA_PACKAGE_ID));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER));
        verify(record).getFieldValueOrNull(eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME));
        verify(record).isMasterAsset();
        verify(record).setStringValueInField(eq(Constants.FieldNames.METADATA_GUID), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY), anyString());
        verify(record).setStringValueInField(eq(UpdatePreservationStep.PRESERVATION_UPDATE_HISTORY_FIELD_NAME), anyString());
        verify(record, times(2)).getUUID();
        verify(record).validateFieldsExists(any(Collection.class));
        verify(record).validateFieldsHasValue(any(Collection.class));
        verify(record).writeFieldMetadata(any(OutputStream.class));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testGetDescription() {
        CumulusWrapper server = mock(CumulusWrapper.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        CumulusWrapper cumulusWrapper = mock(CumulusWrapper.class);

        UpdatePreservationWorkflow workflow = new UpdatePreservationWorkflow();
        workflow.conf = conf;
        workflow.cumulusWrapper = cumulusWrapper;
        workflow.transformationHandler = transformationHandler;
        workflow.preserver = preserver;
        workflow.init();

        String description = workflow.getDescription();
        Assert.assertNotNull(description);
        Assert.assertFalse(description.isEmpty());
        Assert.assertEquals(description, UpdatePreservationWorkflow.WORKFLOW_DESCRIPTION);
    }
    
    @Test
    public void testGetJobID() {
        CumulusWrapper server = mock(CumulusWrapper.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        CumulusWrapper cumulusWrapper = mock(CumulusWrapper.class);

        UpdatePreservationWorkflow workflow = new UpdatePreservationWorkflow();
        workflow.conf = conf;
        workflow.cumulusWrapper = cumulusWrapper;
        workflow.transformationHandler = transformationHandler;
        workflow.preserver = preserver;
        workflow.init();

        String name = workflow.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
        Assert.assertEquals(name, UpdatePreservationWorkflow.WORKFLOW_NAME);
    }
}
