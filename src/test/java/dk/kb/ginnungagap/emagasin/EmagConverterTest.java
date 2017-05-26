package dk.kb.ginnungagap.emagasin;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
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

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.emagasin.EmagImportation;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import junit.framework.Assert;

public class EmagConverterTest extends ExtendedTestCase {

    String digitalObjectUrl = "digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0";
    String nonDigitalObjectUrl = "metadata://Uid:dk:kb:doms:2007-01/07f40370-a0c1-11e1-81c1-0016357f605f#2";
    File arcFile = new File("src/test/resources/conversion/KBDOMS-test.arc");
    File contentFile;

    TestConfiguration conf;
    String catalogName = "asdasdfasdf";
    EmagImportation converter;
    CumulusServer cumulusServer;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        contentFile = TestFileUtils.createFileWithContent("This is the random content: " + UUID.randomUUID().toString());
    }
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        cumulusServer = mock(CumulusServer.class);
        converter = new TestEmagConverter(conf, cumulusServer, catalogName);
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testIsDigitalObject() {
        addDescription("Test which ARC-record URLs are digital objects");
        Assert.assertTrue(converter.isDigitalObject(digitalObjectUrl));
        Assert.assertFalse(converter.isDigitalObject(nonDigitalObjectUrl));
    }
    
    @Test
    public void testExtractUUID() {
        addDescription("Test extracting the UUID");
        String expectedUuid = "7dfe7540-6ab1-11e2-83ab-005056887b70";

        String uuid = converter.extractUUID(digitalObjectUrl);
        Assert.assertEquals(expectedUuid, uuid);
    }
    
    @Test
    public void testExtractUUIDWithoutEndHash() {
        addDescription("Test extracting an UUID from a ARC-record URL, whith no suffix");
        String expectedUuid = UUID.randomUUID().toString();
        String url = "prefix://" + expectedUuid;

        String uuid = converter.extractUUID(url);
        Assert.assertEquals(expectedUuid, uuid);
    }
    
    @Test
    public void testExtractArcRecordAsFile() throws Exception {
        addDescription("Test extracting the ARC records as files.");
        try (ArchiveReader reader = ArchiveReaderFactory.get(arcFile);) {
            for(ArchiveRecord arcRecord : reader) {
                String uuid = "random-" + UUID.randomUUID().toString();
                converter.extractArcRecordAsFile(arcRecord, uuid);
                
                File expectedOutputFile = new File(conf.getImportationConfiguration().getTempDir(), uuid);
                Assert.assertTrue(expectedOutputFile.isFile());
            }
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testConversionFailureWhenCumulusGivesNoRecord() {
        addDescription("Test that the conversion fails, when no cumulus records are found.");
        when(cumulusServer.getItems(anyString(), any(CumulusQuery.class))).thenReturn(null);
        Assert.assertTrue(arcFile.isFile());
        
        converter.convertArcFile(arcFile);
    }
    
    @Test
    public void testConversion() throws Exception {
        addDescription("Test successfull conversion flow - for the generic conversion.");
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
        when(cumulusServer.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);
        
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
        
        converter.convertArcFile(arcFile);
        Assert.assertEquals(((TestEmagConverter) converter).callsToHandler, 1);
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);
        
        verify(recordItemCollection, times(2)).iterator();
        verify(recordItemCollection).getLayout();
        verify(recordItemCollection).getItemCount();
        verifyNoMoreInteractions(recordItemCollection);        
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFindCumulusRecordWhenEmptyListIsReturned() throws Exception {
        addDescription("Test that finding no Cumulus records for a UUID will raise an exception");
        RecordItemCollection recordItemCollection = mock(RecordItemCollection.class);
        when(recordItemCollection.iterator()).thenAnswer(new Answer<Iterator<Item>>() {
            @Override
            public Iterator<Item> answer(InvocationOnMock invocation) throws Throwable {
                return new ArrayList<Item>().iterator();
            }
        });
        
        converter.findCumulusRecord(UUID.randomUUID().toString());
    }
    
    @Test
    public void testFindCumulusRecordWhenMultipleRecordsAreReturned() throws Exception {
        addDescription("Test that when finding multiple Cumulus records, it will not fail");
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
        when(cumulusServer.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);
        
        // When returning a iterator, then the returned iterator is final, and its state is kept, thus 
        when(recordItemCollection.iterator()).thenAnswer(new Answer<Iterator<Item>>() {
            @Override
            public Iterator<Item> answer(InvocationOnMock invocation) throws Throwable {
                return Arrays.asList(item, item).iterator();
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
        
        CumulusRecord cr = converter.findCumulusRecord(UUID.randomUUID().toString());
        
        Assert.assertNotNull(cr);
        
        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(cumulusServer);
        
        verify(recordItemCollection, times(2)).iterator();
        verify(recordItemCollection).getLayout();
        verify(recordItemCollection).getItemCount();
        verifyNoMoreInteractions(recordItemCollection); 
    }

    private class TestEmagConverter extends EmagImportation {
        int callsToHandler = 0;
        
        public TestEmagConverter(Configuration conf, CumulusServer cumulusServer, String catalogName) {
            super(conf, cumulusServer, catalogName);
        }

        @Override
        protected void handleRecord(CumulusRecord record, File contentFile) {
            callsToHandler++;
        }
    }
}
