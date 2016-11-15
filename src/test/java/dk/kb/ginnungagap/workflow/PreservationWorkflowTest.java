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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.canto.cumulus.Asset;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.FieldTypes;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.fieldvalue.AssetReference;
import com.canto.cumulus.fieldvalue.StringEnumFieldValue;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;

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
        XsltMetadataTransformer transformer = mock(XsltMetadataTransformer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        RecordItemCollection recordItemCollection = mock(RecordItemCollection.class);
        
        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);
        when(recordItemCollection.iterator()).thenReturn(new ArrayList<Item>().iterator());
        when(recordItemCollection.getLayout()).thenReturn(null);
        when(recordItemCollection.getItemCount()).thenReturn(0);
        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformer, preserver);        
        pw.run();
        
        verifyZeroInteractions(transformer);
        verifyZeroInteractions(preserver);
        
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(recordItemCollection).iterator();
        verify(recordItemCollection).getLayout();
        verify(recordItemCollection).getItemCount();
        verifyNoMoreInteractions(recordItemCollection);
    }
    
    @Test
    public void testOneItemInCatalog() throws Exception {
        addDescription("Test running on a catalog, which delivers a single item.");
        
        String catalogName = "Catalog-" + UUID.randomUUID().toString();
        conf.removeRequiredFields();
        
        CumulusServer server = mock(CumulusServer.class);
        XsltMetadataTransformer transformer = mock(XsltMetadataTransformer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        RecordItemCollection recordItemCollection = mock(RecordItemCollection.class);
        
        Item item = mock(Item.class);
        Layout layout = mock(Layout.class);
        
        FieldDefinition masterChecksumField = mock(FieldDefinition.class);
        GUID masterChecksumGuid = mock(GUID.class);
        FieldDefinition relatedIdentifierField = mock(FieldDefinition.class);
        GUID relatedIdentifierGuid = mock(GUID.class);
        FieldDefinition guidField = mock(FieldDefinition.class);
        GUID guidGuid = mock(GUID.class);
        FieldDefinition metadataGuidField = mock(FieldDefinition.class);
        GUID metadataGuidGuid = mock(GUID.class);
        FieldDefinition preservationStatusField = mock(FieldDefinition.class);
        GUID preservationStatusGuid = mock(GUID.class);
        AssetReference assetReference = mock(AssetReference.class);
        Asset asset = mock(Asset.class);
        
        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);

        when(masterChecksumField.getName()).thenReturn(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER);
        when(masterChecksumField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(masterChecksumField.getFieldUID()).thenReturn(masterChecksumGuid);
        
        when(relatedIdentifierField.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedIdentifierField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(relatedIdentifierField.getFieldUID()).thenReturn(relatedIdentifierGuid);
        
        when(guidField.getName()).thenReturn(Constants.FieldNames.GUID);
        when(guidField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(guidField.getFieldUID()).thenReturn(guidGuid);

        when(preservationStatusField.getName()).thenReturn(Constants.FieldNames.PRESERVATION_STATUS);
        when(preservationStatusField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(preservationStatusField.getFieldUID()).thenReturn(preservationStatusGuid);

        when(metadataGuidField.getName()).thenReturn(Constants.PreservationFieldNames.METADATA_GUID);
        when(metadataGuidField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(metadataGuidField.getFieldUID()).thenReturn(metadataGuidGuid);

        when(recordItemCollection.iterator()).thenReturn(Arrays.asList(item).iterator());
        when(recordItemCollection.getLayout()).thenReturn(layout);
        when(recordItemCollection.getItemCount()).thenReturn(1);
        
        when(assetReference.getAsset(any(Boolean.class))).thenReturn(asset);
        when(asset.getAsFile()).thenReturn(contentFile);
        
        when(item.getStringValue(any(GUID.class))).thenReturn("cumulus-guid");
        when(item.getAssetReferenceValue(any(GUID.class))).thenReturn(assetReference);
        
        when(layout.iterator()).thenAnswer(new Answer<Iterator<FieldDefinition>>() {
            @Override
            public Iterator<FieldDefinition> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(guidField, metadataGuidField, preservationStatusField, masterChecksumField, relatedIdentifierField).iterator();
            }
        });        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformer, preserver);
        pw.runOnCatalog(catalogName);
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(recordItemCollection).iterator();
        verify(recordItemCollection).getLayout();
        verify(recordItemCollection).getItemCount();
        verifyNoMoreInteractions(recordItemCollection);
        
        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verify(transformer).validate(any(InputStream.class));
        verifyNoMoreInteractions(transformer);
        
        verify(preserver).packRecord(any(CumulusRecord.class), any(File.class));
        verifyNoMoreInteractions(preserver);
    }
    
    @Test
    public void testItemInCatalogWhenCannotWriteToMetadataDir() throws Exception {
        addDescription("Test when it fails to write to the metadata directory, then it must ");
        
        String catalogName = "Catalog-" + UUID.randomUUID().toString();
        conf.removeRequiredFields();
        
        addStep("Define the mocks", "");
        CumulusServer server = mock(CumulusServer.class);
        XsltMetadataTransformer transformer = mock(XsltMetadataTransformer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        RecordItemCollection recordItemCollection = mock(RecordItemCollection.class);
        
        Item item = mock(Item.class);
        Layout layout = mock(Layout.class);

        FieldDefinition masterChecksumField = mock(FieldDefinition.class);
        GUID masterChecksumGuid = mock(GUID.class);
        FieldDefinition relatedIdentifierField = mock(FieldDefinition.class);
        GUID relatedIdentifierGuid = mock(GUID.class);
        FieldDefinition guidField = mock(FieldDefinition.class);
        GUID guidGuid = mock(GUID.class);
        FieldDefinition preservationStatusField = mock(FieldDefinition.class);
        GUID preservationStatusGuid = mock(GUID.class);
        FieldDefinition qaErrorField = mock(FieldDefinition.class);
        GUID qaErrorGuid = mock(GUID.class);
        FieldDefinition metadataGuidField = mock(FieldDefinition.class);
        GUID metadataGuidGuid = mock(GUID.class);

        AssetReference assetReference = mock(AssetReference.class);
        Asset asset = mock(Asset.class);

        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);
        
        // When returning a iterator, then the returned iterator is final, and its state is kept, thus 
        when(recordItemCollection.iterator()).thenAnswer(new Answer<Iterator<Item>>() {
            @Override
            public Iterator<Item> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(item).iterator();
            }
        });
        when(recordItemCollection.getLayout()).thenReturn(layout);
        when(recordItemCollection.getItemCount()).thenReturn(1);
        
        when(assetReference.getAsset(any(Boolean.class))).thenReturn(asset);
        when(asset.getAsFile()).thenReturn(contentFile);
        
        when(item.getStringValue(any(GUID.class))).thenReturn("cumulus-guid");
        when(item.getAssetReferenceValue(any(GUID.class))).thenReturn(assetReference);

        when(masterChecksumField.getName()).thenReturn(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER);
        when(masterChecksumField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(masterChecksumField.getFieldUID()).thenReturn(masterChecksumGuid);
        
        when(relatedIdentifierField.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        when(relatedIdentifierField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(relatedIdentifierField.getFieldUID()).thenReturn(relatedIdentifierGuid);
        
        when(metadataGuidField.getName()).thenReturn(Constants.PreservationFieldNames.METADATA_GUID);
        when(metadataGuidField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(metadataGuidField.getFieldUID()).thenReturn(metadataGuidGuid);
        
        when(guidField.getName()).thenReturn(Constants.FieldNames.GUID);
        when(guidField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(guidField.getFieldUID()).thenReturn(guidGuid);
        
        when(preservationStatusField.getName()).thenReturn(Constants.FieldNames.PRESERVATION_STATUS);
        when(preservationStatusField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(preservationStatusField.getFieldUID()).thenReturn(preservationStatusGuid);

        when(qaErrorField.getName()).thenReturn(Constants.FieldNames.QA_ERROR);
        when(qaErrorField.getFieldType()).thenReturn(FieldTypes.FieldTypeString);
        when(qaErrorField.getFieldUID()).thenReturn(qaErrorGuid);
        
        when(layout.iterator()).thenAnswer(new Answer<Iterator<FieldDefinition>>() {
            @Override
            public Iterator<FieldDefinition> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(guidField, preservationStatusField, qaErrorField, masterChecksumField, metadataGuidField, relatedIdentifierField).iterator();
            }
        });
        
        StringEnumFieldValue enumField = mock(StringEnumFieldValue.class);
        when(item.getStringEnumValue(any(GUID.class))).thenReturn(enumField);
        when(item.hasValue(any(GUID.class))).thenReturn(true);
        
        addStep("Run on the catalog, when the metadata dir is not writable", "Must throw an exception");
        File metadataDir = conf.getTransformationConf().getMetadataTempDir();
        
        try {
            metadataDir.setWritable(false);
            PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformer, preserver);
            pw.runOnCatalog(catalogName);
//            fail("Must fail here!");
        } catch (IllegalStateException e) {
            // expected!
            e.printStackTrace();
        } finally {
            metadataDir.setWritable(true);
        }
        
        addStep("Validate that the correct methods in the mocks are called", "");
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verifyZeroInteractions(transformer);
        verifyZeroInteractions(preserver);
        
        verify(recordItemCollection).iterator();
        verify(recordItemCollection).getLayout();
        verify(recordItemCollection).getItemCount();
        verifyNoMoreInteractions(recordItemCollection);

        verify(item).getStringEnumValue(any(GUID.class));
        verify(item).setStringEnumValue(any(GUID.class), any(StringEnumFieldValue.class));
        verify(item, times(3)).setStringValue(any(GUID.class), anyString());
        verify(item).getDisplayString();
        verify(item, times(8)).getStringValue(any(GUID.class));
        verify(item, times(3)).save();
        verify(item).getAssetReferenceValue(any(GUID.class));
        verify(item, times(7)).hasValue(any(GUID.class));
        verifyNoMoreInteractions(item);
        
        verify(layout, times(8)).iterator();
        verifyNoMoreInteractions(layout);

        verify(guidField, times(8)).getName();
        verify(guidField, times(4)).getFieldUID();
        verify(guidField, times(2)).getFieldType();
        verifyNoMoreInteractions(guidField);
        
        verify(preservationStatusField, times(6)).getName();
        verify(preservationStatusField, times(3)).getFieldUID();
        verify(preservationStatusField, times(2)).getFieldType();
        verifyNoMoreInteractions(preservationStatusField);
        
        verify(qaErrorField, times(5)).getName();
        verify(qaErrorField, times(3)).getFieldUID();
        verify(qaErrorField, times(2)).getFieldType();
        verifyNoMoreInteractions(qaErrorField);
        
        verify(masterChecksumField, times(4)).getName();
        verify(masterChecksumField, times(3)).getFieldUID();
        verify(masterChecksumField, times(2)).getFieldType();
        verifyNoMoreInteractions(masterChecksumField);
        
        verify(metadataGuidField, times(3)).getName();
        verify(metadataGuidField, times(3)).getFieldUID();
        verify(metadataGuidField, times(2)).getFieldType();
        verifyNoMoreInteractions(metadataGuidField);
        
        verify(relatedIdentifierField, times(2)).getName();
        verify(relatedIdentifierField, times(3)).getFieldUID();
        verify(relatedIdentifierField, times(2)).getFieldType();
        verifyNoMoreInteractions(relatedIdentifierField);
    }
}
