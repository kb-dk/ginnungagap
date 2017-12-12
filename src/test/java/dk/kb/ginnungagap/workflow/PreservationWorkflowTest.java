package dk.kb.ginnungagap.workflow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
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
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        
        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(items);
        when(server.getCatalogNames()).thenReturn(Arrays.asList("TEST"));
        when(items.iterator()).thenReturn(new ArrayList<CumulusRecord>().iterator());
        when(items.getCount()).thenReturn(0);
        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformationHandler, preserver);        
        pw.start();
        
        verifyZeroInteractions(transformationHandler);
        
        verify(preserver).uploadAll();
        verifyNoMoreInteractions(preserver);
        
        verify(server).getCatalogNames();
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(items, times(2)).getCount();
        verifyNoMoreInteractions(items);
    }
    
    @Test
    public void testOneItemInCatalog() throws Exception {
        addDescription("Test running on a catalog, which delivers a single item.");
        CumulusServer server = mock(CumulusServer.class);
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
        
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(items.getCount()).thenReturn(1);
        
        when(record.getMetadataGUID()).thenReturn(UUID.randomUUID().toString());
        when(record.getMetadata(any(File.class))).thenReturn(new ByteArrayInputStream(UUID.randomUUID().toString().getBytes()));
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.isMasterAsset()).thenReturn(false);

        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS))).thenReturn(metsTransformer);
        when(transformationHandler.getTransformer(eq(MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY))).thenReturn(ieTransformer);
        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformationHandler, preserver);
        pw.start();
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verify(server).getCatalogNames();
        verifyNoMoreInteractions(server);
        
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
        verify(preserver).packRepresentationMetadata(any(File.class), anyString());
        verify(preserver).checkConditions();
        verify(preserver).uploadAll();
        verifyNoMoreInteractions(preserver);
        
        verify(items, times(2)).getCount();
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).initFieldsForPreservation();
        verify(record).resetMetadataGuid();
        verify(record).validateRequiredFields(any(RequiredFields.class));
        verify(record).setStringValueInField(eq(Constants.FieldNames.BEVARINGS_METADATA), anyString());
        verify(record).getUUID();
        verify(record).getPreservationCollectionID();
        verify(record).isMasterAsset();
        verify(record, times(2)).getMetadataGUID();
        verify(record).getMetadata(any(File.class));
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testGetDescription() {
        CumulusServer server = mock(CumulusServer.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformationHandler, preserver);
        
        String description = pw.getDescription();
        Assert.assertNotNull(description);
        Assert.assertFalse(description.isEmpty());
    }
    
    @Test
    public void testGetJobID() {
        CumulusServer server = mock(CumulusServer.class);
        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformationHandler, preserver);
        
        String jobId = pw.getJobID();
        Assert.assertNotNull(jobId);
        Assert.assertFalse(jobId.isEmpty());
    }
}
