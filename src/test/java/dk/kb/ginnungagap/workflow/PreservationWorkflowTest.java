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

import static org.testng.Assert.fail;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.FieldTypes;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;
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
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }
    
    @AfterClass
    public void tearDown() {
//        TestFileUtils.tearDown();
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
        
        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);
        
        when(recordItemCollection.iterator()).thenReturn(Arrays.asList(item).iterator());
        when(recordItemCollection.getLayout()).thenReturn(layout);
        when(recordItemCollection.getItemCount()).thenReturn(1);
        
        when(layout.iterator()).thenReturn(new ArrayList<FieldDefinition>().iterator());
        
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

        FieldDefinition guidField = mock(FieldDefinition.class);
        GUID guidGuid = mock(GUID.class);
        FieldDefinition preservationStatusField = mock(FieldDefinition.class);
        GUID preservationStatusGuid = mock(GUID.class);
        FieldDefinition qaErrorField = mock(FieldDefinition.class);
        GUID qaErrorGuid = mock(GUID.class);

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
                return Arrays.asList(guidField, preservationStatusField, qaErrorField).iterator();
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
            fail("Must fail here!");
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
        verify(item).setStringValue(any(GUID.class), anyString());
        verify(item).getDisplayString();
        verify(item, times(5)).getStringValue(any(GUID.class));
        verify(item, times(3)).hasValue(any(GUID.class));
        verify(item).save();
        verifyNoMoreInteractions(item);
        
        verify(layout, times(5)).iterator();
        verifyNoMoreInteractions(layout);

        verify(guidField, times(5)).getName();
        verify(guidField, times(4)).getFieldUID();
        verify(guidField, times(2)).getFieldType();
        verifyNoMoreInteractions(guidField);
        
        verifyZeroInteractions(guidGuid);
        
        verify(preservationStatusField, times(3)).getName();
        verify(preservationStatusField, times(3)).getFieldUID();
        verify(preservationStatusField, times(2)).getFieldType();
        verifyNoMoreInteractions(preservationStatusField);
        
        verifyZeroInteractions(preservationStatusGuid);

        verify(qaErrorField, times(2)).getName();
        verify(qaErrorField, times(3)).getFieldUID();
        verify(qaErrorField, times(2)).getFieldType();
        verifyNoMoreInteractions(qaErrorField);

        verifyZeroInteractions(qaErrorGuid);
    }
}
