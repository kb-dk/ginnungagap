package dk.kb.ginnungagap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import junit.framework.Assert;

public class ReinstantiateCumulusAssetsTest extends ExtendedTestCase {

    File testConf;
    File testInputFile;
    String recordId;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
        recordId = UUID.randomUUID().toString();
        testInputFile = TestFileUtils.createFileWithContent(recordId);
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInstantiation() {
        ReinstantiateCumulusAssets reinstantiateCumulusAssets = new ReinstantiateCumulusAssets();
        Assert.assertNotNull(reinstantiateCumulusAssets);
        Assert.assertTrue(reinstantiateCumulusAssets instanceof ReinstantiateCumulusAssets);
    }

//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testMissingArguments() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            ReinstantiateCumulusAssets.main();
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testMissingFileArgument() throws Exception {
//        String catalogName = UUID.randomUUID().toString();
//        String badInputFilePath = UUID.randomUUID().toString();
//        Assert.assertFalse(new File(badInputFilePath).exists());
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            ReinstantiateCumulusAssets.main(testConf.getAbsolutePath(), catalogName, badInputFilePath);
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//
//    @Test(expectedExceptions = IllegalStateException.class)
//    public void testUnableToConnect() throws Exception {
//        // Use bad path, but a 'yes' for all records in catalog
//        String badInputFilePath = UUID.randomUUID().toString();
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            ReinstantiateCumulusAssets.main(testConf.getAbsolutePath(), UUID.randomUUID().toString(), badInputFilePath, "yes");
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//
//    @Test
//    public void testReinstantiateListOfCumulusAssets() {
//        CumulusServer server = mock(CumulusServer.class);
//        String catalogName = UUID.randomUUID().toString();
//        CumulusRecord record = mock(CumulusRecord.class);
//        
//        when(server.findCumulusRecord(eq(catalogName), eq(recordId))).thenReturn(record);
//        when(record.getFile()).thenReturn(testInputFile);
//        
//        ReinstantiateCumulusAssets.reinstantiateListOfCumulusAssets(server, catalogName, testInputFile);
//        
//        verify(server).findCumulusRecord(eq(catalogName), eq(recordId));
//        verifyNoMoreInteractions(server);
//        
//        verify(record).getFile();
//        verify(record).setNewAssetReference(eq(testInputFile));
//        verifyNoMoreInteractions(record);
//    }
//    
//    @Test
//    public void testReinstantiateAllCumulusAssets() {
//        CumulusServer server = mock(CumulusServer.class);
//        String catalogName = UUID.randomUUID().toString();
//        CumulusRecord record = mock(CumulusRecord.class);
//        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
//        
//        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(items);
//        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
//        when(record.getFile()).thenReturn(testInputFile);
//        
//        ReinstantiateCumulusAssets.reinstantiateAllCumulusAssets(server, catalogName);
//        
//        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(items).iterator();
//        verifyNoMoreInteractions(items);
//        
//        verify(record).getFile();
//        verify(record).setNewAssetReference(eq(testInputFile));
//        verifyNoMoreInteractions(record);
//    }
//
//    @Test(expectedExceptions = IllegalStateException.class)
//    public void testReinstantiateListOfCumulusAssetsBadFile() {
//        CumulusServer server = mock(CumulusServer.class);
//        String catalogName = UUID.randomUUID().toString();
//
//        File badFile = new File(UUID.randomUUID().toString());
//        Assert.assertFalse(badFile.exists());
//        
//        ReinstantiateCumulusAssets.reinstantiateListOfCumulusAssets(server, catalogName, badFile);
//    }
//    
//    @Test(expectedExceptions = IllegalStateException.class)
//    public void testReinstantiateAllCumulusAssets() throws Exception {
//        CumulusServer server = mock(CumulusServer.class);
//        String catalogName = UUID.randomUUID().toString();
//        
//        RecordItemCollection items = mock(RecordItemCollection.class);
//        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(new CumulusRecordCollection(items, server, catalogName));
//        Layout layout = mock(Layout.class);
//        when(items.getLayout()).thenReturn(layout);
//        Item item = mock(Item.class);
//        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());
//        
//        when(item.getAssetReferenceValue(eq(GUID.UID_REC_ASSET_REFERENCE))).thenThrow(new RuntimeException("THIS MUST FAIL!!"));
//        
//        ReinstantiateCumulusAssets.reinstantiateAllCumulusAssets(server, catalogName);
//    }
}
