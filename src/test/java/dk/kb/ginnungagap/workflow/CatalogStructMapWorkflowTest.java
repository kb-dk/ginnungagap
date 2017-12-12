package dk.kb.ginnungagap.workflow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
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
        
        String catalogIntellectualEntityID = UUID.randomUUID().toString();
        String recordRelatedIntellectualEntityValue = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.getCount()).thenReturn(1);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(recordRelatedIntellectualEntityValue);
        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(recordNameValue);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, catalogIntellectualEntityID);
        
        File f = workflow.extractGuidsAndFileIDsForCatalog();
        
        String fileContent = StreamUtils.extractInputStreamAsString(new FileInputStream(f));
        Assert.assertTrue(fileContent.contains(catalogIntellectualEntityID));
        Assert.assertTrue(fileContent.contains("<record>"));
        Assert.assertTrue(fileContent.contains(recordRelatedIntellectualEntityValue));
        Assert.assertTrue(fileContent.contains(recordNameValue));
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);

        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformer);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testExtractGuidsAndFileIDsForCatalogSuccessNoItems() throws IOException {
        addDescription("Test extracting the XML format when no cumulus records are found.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String catalogIntellectualEntityID = UUID.randomUUID().toString();
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        
        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.getCount()).thenReturn(1);
        when(items.iterator()).thenReturn(new ArrayList<CumulusRecord>().iterator());

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, catalogIntellectualEntityID);
        
        File f = workflow.extractGuidsAndFileIDsForCatalog();
        
        String fileContent = StreamUtils.extractInputStreamAsString(new FileInputStream(f));
        Assert.assertTrue(fileContent.contains(catalogIntellectualEntityID));
        Assert.assertFalse(fileContent.contains("<record>"));
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);

        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformer);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testExtractGuidsAndFileIDsForCatalogFailureBadItem() throws IOException {
        addDescription("Test failure to extract the XML when an item does not have the required field.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String catalogIntellectualEntityID = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.getCount()).thenReturn(1);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(null);
        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(recordNameValue);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, catalogIntellectualEntityID);
        
        workflow.extractGuidsAndFileIDsForCatalog();
    }
    
    @Test(expectedExceptions = IOException.class)
    public void testExtractGuidsAndFileIDsForCatalogFailureCannotWriteFile() throws IOException {
        addDescription("Test failure to extract the XML when it is not possible to write the output file.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String catalogIntellectualEntityID = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.getCount()).thenReturn(1);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(null);
        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(recordNameValue);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, catalogIntellectualEntityID);
        
        File outputDir = conf.getTransformationConf().getMetadataTempDir();
        try {
            outputDir.setWritable(false);
            workflow.extractGuidsAndFileIDsForCatalog();
        } finally {
            outputDir.setWritable(true);
        }
    }
    
    @Test
    public void testRunSuccess() throws IOException {
        addDescription("Test running the workflow successfully.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        String catalogIntellectualEntityID = UUID.randomUUID().toString();
        String recordRelatedIntellectualEntityValue = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.getCount()).thenReturn(1);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(recordRelatedIntellectualEntityValue);
        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(recordNameValue);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, catalogIntellectualEntityID);

        workflow.run();
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);

        verify(preserver).packRepresentationMetadata(any(File.class), eq(collectionID));
        verifyNoMoreInteractions(preserver);
        
        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transformer);
        
        verify(items).iterator();
        verifyNoMoreInteractions(items);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
        verifyNoMoreInteractions(record);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testRunFailure() throws IOException {
        addDescription("Test running the workflow when it fails.");
        CumulusServer cumulusServer = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        
        String catalogIntellectualEntityID = UUID.randomUUID().toString();
        String recordNameValue = UUID.randomUUID().toString();
        
        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
        when(items.getCount()).thenReturn(1);
        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(null);
        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(recordNameValue);

        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, catalogName, collectionID, catalogIntellectualEntityID);
        
        File outputDir = conf.getTransformationConf().getMetadataTempDir();
        try {
            outputDir.setWritable(false);
            workflow.run();
        } finally {
            outputDir.setWritable(true);
        }
    }
}
