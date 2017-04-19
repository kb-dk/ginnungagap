package dk.kb.ginnungagap.emagasin;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.FileUtils;
import junit.framework.Assert;

public class EmagImporterTest extends ExtendedTestCase {

    String digitalObjectUrl = "digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0";
    String nonDigitalObjectUrl = "metadata://Uid:dk:kb:doms:2007-01/07f40370-a0c1-11e1-81c1-0016357f605f#2";
    File arcFile = new File("src/test/resources/conversion/KBDOMS-test.arc");
    File contentFile;

    TestConfiguration conf;
    String catalogName = "asdasdfasdf";
    EmagImportation converter;
    CumulusServer cumulusServer;
    BitmagPreserver preserver;
    MetadataTransformer transformer;
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        contentFile = TestFileUtils.createFileWithContent("This is the random content: " + UUID.randomUUID().toString());
        
        cumulusServer = mock(CumulusServer.class);
        preserver = mock(BitmagPreserver.class);
        transformer = mock(MetadataTransformer.class);
        converter = new EmagImportation(conf, cumulusServer, catalogName);
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testImportRecordSuccess() throws IOException {
        addDescription("Test the successful import of a file into Cumulus.");
        CumulusRecord record = mock(CumulusRecord.class);
        
        File cumulusOutputFile = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        when(record.getMetadataGUID()).thenReturn(UUID.randomUUID().toString());
        when(record.getMetadata(any(File.class))).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(record.getFile()).thenReturn(cumulusOutputFile);
        
        Assert.assertFalse(cumulusOutputFile.exists());
        converter.handleRecord(record, contentFile);
        Assert.assertTrue(cumulusOutputFile.exists());
        
        verify(record).getFile();
        verifyNoMoreInteractions(record);
        
        verifyZeroInteractions(transformer);
        
        verifyZeroInteractions(preserver);
        
        verifyZeroInteractions(cumulusServer);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailedImportMissingRights() throws Exception {
        addDescription("Test the scenario, where the file cannot be imported due to missing rights");
        File cumulusOutputDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        try {
            cumulusOutputDir.setReadOnly();
            CumulusRecord record = mock(CumulusRecord.class);

            File cumulusOutputFile = new File(cumulusOutputDir, UUID.randomUUID().toString());

            when(record.getMetadataGUID()).thenReturn(UUID.randomUUID().toString());
            when(record.getMetadata(any(File.class))).thenReturn(new ByteArrayInputStream(new byte[0]));
            when(record.getFile()).thenReturn(cumulusOutputFile);

            Assert.assertFalse(cumulusOutputFile.exists());
            converter.handleRecord(record, contentFile);
        } finally {
            cumulusOutputDir.setWritable(true);
            cumulusOutputDir.setExecutable(true);
        }
    }
}
