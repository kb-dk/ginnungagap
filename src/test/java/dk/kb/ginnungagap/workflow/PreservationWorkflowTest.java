package dk.kb.ginnungagap.workflow;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

public class PreservationWorkflowTest extends ExtendedTestCase {

    TestConfiguration conf;
    File contentFile;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        contentFile = TestFileUtils.createFileWithContent("This is the content");        
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testNoItems() {
        addDescription("Test the workflow, when no items are retrieved from Cumulus");
        
        CumulusServer server = mock(CumulusServer.class);
        CumulusWrapper cumulusWrapper = mock(CumulusWrapper.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        
        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(items);
        when(server.getCatalogNames()).thenReturn(Arrays.asList("TEST"));
        when(items.iterator()).thenReturn(new ArrayList<CumulusRecord>().iterator());
        when(items.getCount()).thenReturn(0);
        when(cumulusWrapper.getServer()).thenReturn(server);
        
        PreservationWorkflow pw = new PreservationWorkflow();
        pw.conf = conf;
        pw.cumulusWrapper = cumulusWrapper;
        pw.transformationHandler = transformationHandler;
        pw.preserver = preserver;
        pw.init();
        
        pw.startManually();
        pw.run();
        
        verifyZeroInteractions(transformationHandler);
        
        verify(preserver).uploadAll();
        verifyNoMoreInteractions(preserver);
        
        verify(cumulusWrapper, times(2)).getServer();
        verifyNoMoreInteractions(cumulusWrapper);
        
        verify(server).getCatalogNames();
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(items, times(2)).getCount();
        verifyNoMoreInteractions(items);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testOneItemInCatalog() throws Exception {
        addDescription("Test running on a catalog, which delivers a single item.");
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
        when(record.isMasterAsset()).thenReturn(false);
        when(record.getFile()).thenReturn(contentFile);
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

        PreservationWorkflow pw = new PreservationWorkflow();
        pw.conf = conf;
        pw.cumulusWrapper = cumulusWrapper;
        pw.transformationHandler = transformationHandler;
        pw.preserver = preserver;
        pw.init();
        
        pw.startManually();
        pw.run();
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verify(server).getCatalogNames();
        verifyNoMoreInteractions(server);

        verify(cumulusWrapper, times(2)).getServer();
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

        verify(preserver).packRecordResource(any(CumulusRecord.class));
        verify(preserver).packRecordMetadata(any(CumulusRecord.class), any(File.class));
        verify(preserver).packRepresentationMetadata(any(File.class), anyString(), anyString());
        verify(preserver).checkConditions();
        verify(preserver).uploadAll();
        verifyNoMoreInteractions(preserver);
        
        verify(items, times(2)).getCount();
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record, times(2)).getFieldValue(eq(Constants.FieldNames.METADATA_GUID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.METADATA_GUID), anyString());
        verify(record).validateFieldsExists(any(Collection.class));
        verify(record).validateFieldsHasValue(any(Collection.class));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verify(record, times(3)).getUUID();
        verify(record).isMasterAsset();
        verify(record).writeFieldMetadata(any(OutputStream.class));
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER));
        verify(record).setStringValueInField(eq(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER), anyString());
        verify(record).setStringValueInField(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY), anyString());
        verify(record).getFile();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testGetDescription() {
        CumulusWrapper cumulusWrapper = mock(CumulusWrapper.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);

        PreservationWorkflow pw = new PreservationWorkflow();
        pw.conf = conf;
        pw.cumulusWrapper = cumulusWrapper;
        pw.transformationHandler = transformationHandler;
        pw.preserver = preserver;        
        
        String description = pw.getDescription();
        Assert.assertNotNull(description);
        Assert.assertFalse(description.isEmpty());
        Assert.assertEquals(description, PreservationWorkflow.WORKFLOW_DESCRIPTION);
    }
    
    @Test
    public void testGetJobID() {
        CumulusWrapper cumulusWrapper = mock(CumulusWrapper.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        PreservationWorkflow pw = new PreservationWorkflow();
        pw.conf = conf;
        pw.cumulusWrapper = cumulusWrapper;
        pw.transformationHandler = transformationHandler;
        pw.preserver = preserver;        
        
        String name = pw.getName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.isEmpty());
        Assert.assertEquals(name, PreservationWorkflow.WORKFLOW_NAME);
    }
}
