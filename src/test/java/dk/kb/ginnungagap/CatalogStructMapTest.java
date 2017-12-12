package dk.kb.ginnungagap;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollectionTest;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

public class CatalogStructMapTest extends ExtendedTestCase {

    File testConf;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        FileUtils.copyDirectory(new File("src/main/resources/scripts/xslt"), new File(TestFileUtils.getTempDir(), "scripts/xslt"));
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testOnlyOneArgument() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(testConf.getAbsolutePath());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testOnlyTwoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(testConf.getAbsolutePath(), "Audio");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNullArgumentForConfiguration() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(null, "Audio", "G");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testInvalidArchiveType() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            CatalogStructmap.main(testConf.getAbsolutePath(), "THIS_IS_NOT_A_VALID_ARCHIVE");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test
    public void testCheckConfigurationSuccess() {
        Configuration conf = AbstractMain.instantiateConfiguration(testConf.getAbsolutePath());
        CatalogStructmap.checkConfiguration(conf, "Audio OM");
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCheckConfigurationFailure() {
        Configuration conf = AbstractMain.instantiateConfiguration(testConf.getAbsolutePath());
        CatalogStructmap.checkConfiguration(conf, "THIS IS NOT A CUMULUS CATALOG");
    }
    
//    @Test
//    public void testCreateCatalogStructmap() {
//        CumulusServer cumulusServer = mock(CumulusServer.class);
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        TestConfiguration conf = new TestConfiguration(testConf);
//        String catalogName = "CATALOG";
//        String collectionID = "CollectionID";
//        String intellectualEntityID = "intellectualEntityID";
//        
//        RecordItemCollection records = mock(RecordItemCollection.class);
//        Layout layout = mock(Layout.class);
//        
//        FieldDefinition recordIntellectualEntityDefintion = mock(FieldDefinition.class);
//        GUID recordIntellectualEntityGuid = mock(GUID.class);
//        when(recordIntellectualEntityDefintion.getName()).thenReturn(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
//        when(recordIntellectualEntityDefintion.getFieldUID()).thenReturn(recordIntellectualEntityGuid);
//        
//        FieldDefinition recordNameDefintion = mock(FieldDefinition.class);
//        GUID recordNameGuid = mock(GUID.class);
//        when(recordNameDefintion.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
//        when(recordNameDefintion.getFieldUID()).thenReturn(recordNameGuid);
//        
//        when(layout.iterator()).thenReturn(Arrays.asList(recordIntellectualEntityDefintion, recordNameDefintion).iterator());
//        when(records.getLayout()).thenReturn(layout);
//        when(records.iterator()).thenReturn(new ArrayList<Item>().iterator());
//
//        when(cumulusServer.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(new CumulusRecordCollection(records, cumulusServer, catalogName));
//        
//        CatalogStructmap.createCatalogStructmap(cumulusServer, transformer, preserver, conf, catalogName, collectionID, intellectualEntityID);
//
//        verify(cumulusServer).getItems(eq(catalogName), any(CumulusQuery.class));
//        verifyNoMoreInteractions(cumulusServer);
//        
//        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
//        verifyNoMoreInteractions(transformer);
//        
//        verify(preserver).uploadAll();
//        verify(preserver).packRepresentationMetadata(any(File.class), eq(collectionID));
//        verifyNoMoreInteractions(preserver);
//        
//        verify(records).getLayout();
//        verify(records).iterator();
//        verifyNoMoreInteractions(records);
//        
//        verify(layout, times(2)).iterator();
//        verifyNoMoreInteractions(layout);
//        
//        verify(recordIntellectualEntityDefintion).getName();
//        verify(recordIntellectualEntityDefintion).getFieldUID();
//        verifyNoMoreInteractions(recordIntellectualEntityDefintion);
//        
//        verifyZeroInteractions(recordIntellectualEntityGuid);
//        
//        verify(recordNameDefintion).getName();
//        verify(recordNameDefintion).getFieldUID();
//        verifyNoMoreInteractions(recordNameDefintion);
//        
//        verifyZeroInteractions(recordNameGuid);
//    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailingCumulusAccess() {
        CatalogStructmap.main(testConf.getAbsolutePath(), "Audio OM", "G", "local", "intellectual-entity");
    }
    
    @Test
    public void testInstantiation() {
        CatalogStructmap catalogStructmap = new CatalogStructmap();
        Assert.assertNotNull(catalogStructmap);
        Assert.assertTrue(catalogStructmap instanceof CatalogStructmap);
    }
}
