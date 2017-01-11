package dk.kb.ginnungagap.convert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import junit.framework.Assert;

public class EmagImportConverterTest extends ExtendedTestCase {

    String digitalObjectUrl = "digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0";
    String nonDigitalObjectUrl = "metadata://Uid:dk:kb:doms:2007-01/07f40370-a0c1-11e1-81c1-0016357f605f#2";
    File arcFile = new File("src/test/resources/conversion/KBDOMS-test.arc");
    File contentFile;

    TestConfiguration conf;
    String catalogName = "asdasdfasdf";
    EmagImportConverter converter;
    CumulusServer cumulusServer;
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        contentFile = TestFileUtils.createFileWithContent("This is the random content: " + UUID.randomUUID().toString());
        
        cumulusServer = mock(CumulusServer.class);
        converter = new EmagImportConverter(conf, cumulusServer, catalogName);
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testHandleRecordSuccess() throws Exception {
        addDescription("Test the successfull scenario, when the file is overridden");
        
        CumulusRecord record = mock(CumulusRecord.class);
        File cumulusFile = TestFileUtils.createFileWithContent("OUTPUTFILE!!!!");
        
        when(record.getFile()).thenReturn(cumulusFile);
        
        long contentFileSize = contentFile.length();
        long origCumulusFileSize = cumulusFile.length();
        
        Assert.assertTrue(cumulusFile.length() != contentFileSize);
        converter.handleRecord(record, contentFile);

        Assert.assertEquals(cumulusFile.length(), contentFileSize);
        Assert.assertTrue(cumulusFile.length() != origCumulusFileSize);

        verify(record).getFile();
        verifyNoMoreInteractions(record);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testHandleRecordFail() throws Exception {
        addDescription("Test the failure of the ");
        
        CumulusRecord record = mock(CumulusRecord.class);
        File cumulusFile = TestFileUtils.createFileWithContent("OUTPUTFILE!!!!");
        
        when(record.getFile()).thenReturn(cumulusFile);
        
        try {
            cumulusFile.getParentFile().setReadOnly();
            converter.handleRecord(record, contentFile);
        } finally {
            cumulusFile.getParentFile().setWritable(true);
        }
    }
}
