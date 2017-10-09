package dk.kb.ginnungagap.workflow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.StreamUtils;

public class CatalogStructMapWorkflowTest extends ExtendedTestCase {

    TestConfiguration conf;
    File contentFile;
    String catalogName = "catalog";
    String collectionID = "collection-id-" + UUID.randomUUID().toString();
    
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
    public void testExtractGuidsAndFileIDsForCatalogSuccessSingleItem() throws IOException {
        addDescription("Test extracting the XML format for a single item.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String intellectualEntityID = UUID.randomUUID().toString();
        String relatedEntityValue = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        FieldDefinition relatedEntityFieldDefinition = mock(FieldDefinition.class);
        GUID relatedEntityGuid = mock(GUID.class);
        when(relatedEntityFieldDefinition.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedEntityFieldDefinition.getFieldUID()).thenReturn(relatedEntityGuid);
        FieldDefinition recordNameFieldDefinition = mock(FieldDefinition.class);
        GUID recordNameGuid = mock(GUID.class);
        when(recordNameFieldDefinition.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
        when(recordNameFieldDefinition.getFieldUID()).thenReturn(recordNameGuid);
        
        Layout layout = mock(Layout.class);
        when(layout.iterator()).thenReturn(Arrays.asList(relatedEntityFieldDefinition, recordNameFieldDefinition).iterator());
        
        Item item = mock(Item.class);
        when(item.hasValue(any(GUID.class))).thenReturn(true);
        when(item.getStringValue(eq(relatedEntityGuid))).thenReturn(relatedEntityValue);
        when(item.getStringValue(eq(recordNameGuid))).thenReturn(recordNameValue);
        
        RecordItemCollection items = mock(RecordItemCollection.class);
        when(items.getLayout()).thenReturn(layout);
        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());

        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, intellectualEntityID);
        
        File f = workflow.extractGuidsAndFileIDsForCatalog();
        
        String fileContent = StreamUtils.extractInputStreamAsString(new FileInputStream(f));
        Assert.assertTrue(fileContent.contains(intellectualEntityID));
        Assert.assertTrue(fileContent.contains(relatedEntityValue));
        Assert.assertTrue(fileContent.contains(recordNameValue));
        Assert.assertTrue(fileContent.contains("<record>"));
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);

        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformer);

        verify(relatedEntityFieldDefinition).getName();
        verify(relatedEntityFieldDefinition).getFieldUID();
        verifyNoMoreInteractions(relatedEntityFieldDefinition);
        
        verifyZeroInteractions(relatedEntityGuid);
        
        verify(recordNameFieldDefinition).getName();
        verify(recordNameFieldDefinition).getFieldUID();
        verifyNoMoreInteractions(recordNameFieldDefinition);

        verifyZeroInteractions(recordNameGuid);

        verify(layout, times(2)).iterator();
        verifyNoMoreInteractions(layout);
        
        verify(item).hasValue(any(GUID.class));
        verify(item).getStringValue(eq(relatedEntityGuid));
        verify(item).getStringValue(eq(recordNameGuid));
        verifyNoMoreInteractions(item);
        
        verify(items).getLayout();
        verify(items).iterator();
        verifyNoMoreInteractions(items);
    }
    
    @Test
    public void testExtractGuidsAndFileIDsForCatalogSuccessNoItems() throws IOException {
        addDescription("Test extracting the XML format when no items are found.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String intellectualEntityID = UUID.randomUUID().toString();
        
        FieldDefinition relatedEntityFieldDefinition = mock(FieldDefinition.class);
        GUID relatedEntityGuid = mock(GUID.class);
        when(relatedEntityFieldDefinition.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedEntityFieldDefinition.getFieldUID()).thenReturn(relatedEntityGuid);
        FieldDefinition recordNameFieldDefinition = mock(FieldDefinition.class);
        GUID recordNameGuid = mock(GUID.class);
        when(recordNameFieldDefinition.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
        when(recordNameFieldDefinition.getFieldUID()).thenReturn(recordNameGuid);
        
        Layout layout = mock(Layout.class);
        when(layout.iterator()).thenReturn(Arrays.asList(relatedEntityFieldDefinition, recordNameFieldDefinition).iterator());
        
        RecordItemCollection items = mock(RecordItemCollection.class);
        when(items.getLayout()).thenReturn(layout);
        when(items.iterator()).thenReturn(new ArrayList<Item>().iterator());

        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, intellectualEntityID);
        
        File f = workflow.extractGuidsAndFileIDsForCatalog();
        
        String fileContent = StreamUtils.extractInputStreamAsString(new FileInputStream(f));
        Assert.assertTrue(fileContent.contains(intellectualEntityID));
        Assert.assertFalse(fileContent.contains("<record>"));

        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);

        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformer);

        verify(relatedEntityFieldDefinition).getName();
        verify(relatedEntityFieldDefinition).getFieldUID();
        verifyNoMoreInteractions(relatedEntityFieldDefinition);
        
        verifyZeroInteractions(relatedEntityGuid);
        
        verify(recordNameFieldDefinition).getName();
        verify(recordNameFieldDefinition).getFieldUID();
        verifyNoMoreInteractions(recordNameFieldDefinition);

        verifyZeroInteractions(recordNameGuid);

        verify(layout, times(2)).iterator();
        verifyNoMoreInteractions(layout);
        
        verify(items).getLayout();
        verify(items).iterator();
        verifyNoMoreInteractions(items);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testExtractGuidsAndFileIDsForCatalogFailureBadItem() throws IOException {
        addDescription("Test failure to extract the XML when an item does not have the required field.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String intellectualEntityID = UUID.randomUUID().toString();
        
        FieldDefinition relatedEntityFieldDefinition = mock(FieldDefinition.class);
        GUID relatedEntityGuid = mock(GUID.class);
        when(relatedEntityFieldDefinition.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedEntityFieldDefinition.getFieldUID()).thenReturn(relatedEntityGuid);
        FieldDefinition recordNameFieldDefinition = mock(FieldDefinition.class);
        GUID recordNameGuid = mock(GUID.class);
        when(recordNameFieldDefinition.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
        when(recordNameFieldDefinition.getFieldUID()).thenReturn(recordNameGuid);
        
        Layout layout = mock(Layout.class);
        when(layout.iterator()).thenReturn(Arrays.asList(relatedEntityFieldDefinition, recordNameFieldDefinition).iterator());
        
        Item item = mock(Item.class);
        when(item.hasValue(any(GUID.class))).thenReturn(false);
        
        RecordItemCollection items = mock(RecordItemCollection.class);
        when(items.getLayout()).thenReturn(layout);
        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());

        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, intellectualEntityID);
        
        workflow.extractGuidsAndFileIDsForCatalog();
    }
    
    @Test
    public void testRunSuccess() throws IOException {
        addDescription("Test running the workflow.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String intellectualEntityID = UUID.randomUUID().toString();
        String relatedEntityValue = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        FieldDefinition relatedEntityFieldDefinition = mock(FieldDefinition.class);
        GUID relatedEntityGuid = mock(GUID.class);
        when(relatedEntityFieldDefinition.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedEntityFieldDefinition.getFieldUID()).thenReturn(relatedEntityGuid);
        FieldDefinition recordNameFieldDefinition = mock(FieldDefinition.class);
        GUID recordNameGuid = mock(GUID.class);
        when(recordNameFieldDefinition.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
        when(recordNameFieldDefinition.getFieldUID()).thenReturn(recordNameGuid);
        
        Layout layout = mock(Layout.class);
        when(layout.iterator()).thenReturn(Arrays.asList(relatedEntityFieldDefinition, recordNameFieldDefinition).iterator());
        
        Item item = mock(Item.class);
        when(item.hasValue(any(GUID.class))).thenReturn(true);
        when(item.getStringValue(eq(relatedEntityGuid))).thenReturn(relatedEntityValue);
        when(item.getStringValue(eq(recordNameGuid))).thenReturn(recordNameValue);
        
        RecordItemCollection items = mock(RecordItemCollection.class);
        when(items.getLayout()).thenReturn(layout);
        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());

        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, intellectualEntityID);
        
        workflow.run();
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);

        verify(preserver).packMetadataRecordWithoutCumulusReference(any(File.class), eq(collectionID));
        verifyNoMoreInteractions(preserver);
        
        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transformer);

        verify(relatedEntityFieldDefinition).getName();
        verify(relatedEntityFieldDefinition).getFieldUID();
        verifyNoMoreInteractions(relatedEntityFieldDefinition);
        
        verifyZeroInteractions(relatedEntityGuid);
        
        verify(recordNameFieldDefinition).getName();
        verify(recordNameFieldDefinition).getFieldUID();
        verifyNoMoreInteractions(recordNameFieldDefinition);

        verifyZeroInteractions(recordNameGuid);

        verify(layout, times(2)).iterator();
        verifyNoMoreInteractions(layout);
        
        verify(item).hasValue(any(GUID.class));
        verify(item).getStringValue(eq(relatedEntityGuid));
        verify(item).getStringValue(eq(recordNameGuid));
        verifyNoMoreInteractions(item);
        
        verify(items).getLayout();
        verify(items).iterator();
        verifyNoMoreInteractions(items);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testRunFailure() throws IOException {
        addDescription("Test running the workflow.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String intellectualEntityID = UUID.randomUUID().toString();
        String relatedEntityValue = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        FieldDefinition relatedEntityFieldDefinition = mock(FieldDefinition.class);
        GUID relatedEntityGuid = mock(GUID.class);
        when(relatedEntityFieldDefinition.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedEntityFieldDefinition.getFieldUID()).thenReturn(relatedEntityGuid);
        FieldDefinition recordNameFieldDefinition = mock(FieldDefinition.class);
        GUID recordNameGuid = mock(GUID.class);
        when(recordNameFieldDefinition.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
        when(recordNameFieldDefinition.getFieldUID()).thenReturn(recordNameGuid);
        
        Layout layout = mock(Layout.class);
        when(layout.iterator()).thenReturn(Arrays.asList(relatedEntityFieldDefinition, recordNameFieldDefinition).iterator());
        
        Item item = mock(Item.class);
        when(item.hasValue(any(GUID.class))).thenReturn(true);
        when(item.getStringValue(eq(relatedEntityGuid))).thenReturn(relatedEntityValue);
        when(item.getStringValue(eq(recordNameGuid))).thenReturn(recordNameValue);
        
        RecordItemCollection items = mock(RecordItemCollection.class);
        when(items.getLayout()).thenReturn(layout);
        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());

        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException("THIS MUST FAIL");
            }
        }).when(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, intellectualEntityID);
        
        workflow.run();
    }
}
