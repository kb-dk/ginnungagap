package dk.kb.ginnungagap.convert;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import junit.framework.Assert;

public class EmagWarcConverterTest extends ExtendedTestCase {

    String digitalObjectUrl = "digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0";
    String nonDigitalObjectUrl = "metadata://Uid:dk:kb:doms:2007-01/07f40370-a0c1-11e1-81c1-0016357f605f#2";
    File arcFile = new File("src/test/resources/conversion/KBDOMS-test.arc");
    File contentFile;

    TestConfiguration conf;
    String catalogName = "asdasdfasdf";
    EmagWarcConverter converter;
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
        converter = new EmagWarcConverter(conf, cumulusServer, catalogName, preserver, transformer);
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testHandleRecordSuccess() throws IOException {
        addDescription("Test the ");
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(record.getMetadataGUID()).thenReturn(UUID.randomUUID().toString());
        when(record.getMetadata(any(File.class))).thenReturn(new ByteArrayInputStream(new byte[0]));
        
        Assert.assertEquals(conf.getTransformationConf().getMetadataTempDir().list().length, 0);
        converter.handleRecord(record, contentFile);
        Assert.assertEquals(conf.getTransformationConf().getMetadataTempDir().list().length, 1);
        
        verify(record).initFields();
        verify(record).validateRequiredFields(any(RequiredFields.class));
        verify(record).getMetadataGUID();
        verify(record).getMetadata(any(File.class));
        verifyNoMoreInteractions(record);
        
        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verify(transformer).validate(any(InputStream.class));
        verifyNoMoreInteractions(transformer);
        
        verify(preserver).packRecordWithNonAssetResource(eq(record), any(File.class), any(File.class));
        verifyNoMoreInteractions(preserver);
        
        verifyZeroInteractions(cumulusServer);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testHandleRecordFailure() throws IOException {
        addDescription("Test the ");
        CumulusRecord record = mock(CumulusRecord.class);
        
        when(record.getMetadataGUID()).thenReturn(UUID.randomUUID().toString());
        when(record.getMetadata(any(File.class))).thenReturn(new ByteArrayInputStream(new byte[0]));
        
        doThrow(new IOException("FAIL HERE!!!")).when(transformer).validate(any(InputStream.class));
        
        Assert.assertEquals(conf.getTransformationConf().getMetadataTempDir().list().length, 0);
        converter.handleRecord(record, contentFile);
    }
}
