package dk.kb.ginnungagap.convert;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import junit.framework.Assert;

public class EmagConverterTest extends ExtendedTestCase {

    String digitalObjectUrl = "digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0";
    String nonDigitalObjectUrl = "metadata://Uid:dk:kb:doms:2007-01/07f40370-a0c1-11e1-81c1-0016357f605f#2";
    File arcFile = new File("src/test/resources/conversion/KBDOMS-test.arc");

    TestConfiguration conf;
    String catalogName = "asdasdfasdf";
    EmagConverter converter;
    CumulusServer cumulusServer;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
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
        Assert.assertTrue(converter.isDigitalObject(digitalObjectUrl));
        Assert.assertFalse(converter.isDigitalObject(nonDigitalObjectUrl));
    }
    
    @Test
    public void testExtractUUID() {
        String expectedUuid = "7dfe7540-6ab1-11e2-83ab-005056887b70";

        String uuid = converter.extractUUID(digitalObjectUrl);
        Assert.assertEquals(expectedUuid, uuid);
    }
    
    @Test
    public void testExtractUUIDWithoutEndHash() {
        String expectedUuid = UUID.randomUUID().toString();
        String url = "prefix://" + expectedUuid;

        String uuid = converter.extractUUID(url);
        Assert.assertEquals(expectedUuid, uuid);
    }
    
    @Test
    public void testExtractArcRecordAsFile() throws Exception {
        try (ArchiveReader reader = ArchiveReaderFactory.get(arcFile);) {
            for(ArchiveRecord arcRecord : reader) {
                String uuid = "random-" + UUID.randomUUID().toString();
                converter.extractArcRecordAsFile(arcRecord, uuid);
                
                File expectedOutputFile = new File(conf.getBitmagConf().getTempDir(), uuid);
                Assert.assertTrue(expectedOutputFile.isFile());
            }
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testConversionFailureWhenCumulusGivesNoRecord() {
        when(cumulusServer.getItems(anyString(), any(CumulusQuery.class))).thenReturn(null);
        Assert.assertTrue(arcFile.isFile());
        
        converter.convertArcFile(arcFile);
    }

    private class TestEmagConverter extends EmagConverter {

        public TestEmagConverter(Configuration conf, CumulusServer cumulusServer, String catalogName) {
            super(conf, cumulusServer, catalogName);
        }

        @Override
        protected void handleRecord(CumulusRecord record, File contentFile) {
            // DO NOTHING!!!
        }
        
    }
}
