package dk.kb.ginnungagap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Asset;
import com.canto.cumulus.CumulusException;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.exceptions.UnresolvableAssetReferenceException;
import com.canto.cumulus.fieldvalue.AssetReference;

import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollectionTest;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import dk.kb.ginnungagap.utils.FileUtils;
import junit.framework.Assert;

public class CumulusFileValidationTest extends ExtendedTestCase {

    File testConf;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInstantiation() {
        CumulusFileValidation cumulusFileValidation = new CumulusFileValidation();
        Assert.assertNotNull(cumulusFileValidation);
        Assert.assertTrue(cumulusFileValidation instanceof CumulusFileValidation);
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testMissingArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CumulusFileValidation.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNonExistingConfigurationFile() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CumulusFileValidation.main(UUID.randomUUID().toString());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnableToConnect() throws Exception {
        CumulusFileValidation.main(testConf.getAbsolutePath(), UUID.randomUUID().toString());
    }
    
    @Test
    public void testGetOutputFileNonExistingFile() {
        File testDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        File outputFile = new File(testDir, UUID.randomUUID().toString());
        Assert.assertFalse(outputFile.exists());
        Assert.assertEquals(0, testDir.listFiles().length);
        CumulusFileValidation.getOutputFile(outputFile.getAbsolutePath());
        Assert.assertFalse(outputFile.exists());
    }
    
    @Test
    public void testGetOutputFileExistingFile() throws IOException {
        File outputFile = TestFileUtils.createFileWithContent(UUID.randomUUID().toString());
        Assert.assertTrue(outputFile.exists());
        CumulusFileValidation.getOutputFile(outputFile.getAbsolutePath());
        Assert.assertFalse(outputFile.exists());
        Assert.assertTrue(new File(outputFile.getAbsolutePath() + ".old").exists());
    }
    
    @Test
    public void testCheckRecordWithFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CumulusRecord record = mock(CumulusRecord.class);
        String catalogName = UUID.randomUUID().toString();
        
        when(record.getFile()).thenReturn(testConf);
        CumulusFileValidation.checkRecord(record, catalogName, baos);
        
        String res = baos.toString();
        Assert.assertTrue(res.contains(catalogName));
        Assert.assertTrue(res.contains(CumulusFileValidation.OUTPUT_RES_FOUND));
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    

    @Test
    public void testCheckRecordWithMissingFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CumulusRecord record = mock(CumulusRecord.class);
        String catalogName = UUID.randomUUID().toString();
        
        when(record.getFile()).thenReturn(new File(UUID.randomUUID().toString()));
        CumulusFileValidation.checkRecord(record, catalogName, baos);
        
        String res = baos.toString();
        Assert.assertTrue(res.contains(catalogName));
        Assert.assertTrue(res.contains(CumulusFileValidation.OUTPUT_RES_MISSING));
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testCheckRecordWithError() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CumulusRecord record = mock(CumulusRecord.class);
        String catalogName = UUID.randomUUID().toString();
        
        when(record.getFile()).thenThrow(new RuntimeException("TEST FAILURE"));
        CumulusFileValidation.checkRecord(record, catalogName, baos);
        
        String res = baos.toString();
        Assert.assertTrue(res.contains(catalogName));
        Assert.assertTrue(res.contains(CumulusFileValidation.OUTPUT_RES_ERROR));
        
        verify(record).getFile();
        verify(record).getUUID();
        verifyNoMoreInteractions(record);
    }
    
//    @Test
//    public void testValidateCumulusRecordFiles() throws Exception {
//        CumulusServer server = mock(CumulusServer.class);
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        File outputFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()); 
//        
//        RecordItemCollection items = mock(RecordItemCollection.class);
//        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(new CumulusRecordCollection(items, server, conf.getCumulusConf().getCatalogs().get(0)));
//        Layout layout = mock(Layout.class);
//        when(items.getLayout()).thenReturn(layout);
//        when(items.iterator()).thenReturn(new ArrayList<Item>().iterator());
//        
//        CumulusFileValidation.validateCumulusRecordFiles(server, conf, outputFile);
//        
//        verify(server).getItems(anyString(), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(items).getLayout();
//        verify(items).iterator();
//        verifyNoMoreInteractions(items);
//        
//        verifyZeroInteractions(layout);
//    }
//    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testValidateCumulusRecordFilesFailure() throws Exception {
        CumulusServer server = mock(CumulusServer.class);
        TestConfiguration conf = TestFileUtils.createTempConf();
        File outputFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()); 
        
        try {
            TestFileUtils.getTempDir().setWritable(false);
            CumulusFileValidation.validateCumulusRecordFiles(server, conf, outputFile);            
        } finally {
            TestFileUtils.getTempDir().setWritable(true);
        }
    }
    
//    @Test
//    public void testValidateForCatalog() throws IOException, CumulusException, UnresolvableAssetReferenceException {
//        CumulusServer server = mock(CumulusServer.class);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        String catalogName = UUID.randomUUID().toString();
//        
//        RecordItemCollection items = mock(RecordItemCollection.class);
//        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(new CumulusRecordCollection(items, server, catalogName));
//        Layout layout = mock(Layout.class);
//        when(items.getLayout()).thenReturn(layout);
//        Item item = mock(Item.class);
//        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());
//        
//        FieldDefinition guidField = mock(FieldDefinition.class);
//        GUID guidGuid = mock(GUID.class);
//        when(guidField.getName()).thenReturn(Constants.FieldNames.GUID);
//        when(guidField.getFieldUID()).thenReturn(guidGuid);
//        
//        when(item.getStringValue(any(GUID.class))).thenReturn(UUID.randomUUID().toString());
//        
//        Collection<FieldDefinition> fields = Arrays.asList(guidField);
//        when(layout.iterator()).thenReturn(fields.iterator()).thenReturn(fields.iterator()).thenReturn(fields.iterator()).thenReturn(fields.iterator());
//
//        
//        AssetReference assetReference = mock(AssetReference.class);
//        when(item.getAssetReferenceValue(eq(GUID.UID_REC_ASSET_REFERENCE))).thenReturn(assetReference);
//        Asset asset = mock(Asset.class);
//        when(assetReference.getAsset(eq(false))).thenReturn(asset);
//        when(asset.getAsFile()).thenReturn(testConf);
//        
//        CumulusFileValidation.validateForCatalog(server, catalogName, out);
//        
//        addStep("Check the output", "Contains the file");
//        Assert.assertTrue(out.toString().contains(CumulusFileValidation.OUTPUT_RES_FOUND));
//        
//        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(items).getLayout();
//        verify(items).iterator();
//        verifyNoMoreInteractions(items);
//        
//        verify(layout).iterator();
//        verifyNoMoreInteractions(layout);
//        
//        verify(item).getStringValue(any(GUID.class));
//        verify(item).getAssetReferenceValue(eq(GUID.UID_REC_ASSET_REFERENCE));
//        verifyNoMoreInteractions(item);
//        
//        verify(guidField).getName();
//        verify(guidField).getFieldUID();
//        verifyNoMoreInteractions(guidField);
//        
//        verifyZeroInteractions(guidGuid);
//        
//        verify(assetReference).getAsset(eq(false));
//        verifyNoMoreInteractions(assetReference);
//        
//        verify(asset).getAsFile();
//        verifyNoMoreInteractions(asset);
//    }
}
