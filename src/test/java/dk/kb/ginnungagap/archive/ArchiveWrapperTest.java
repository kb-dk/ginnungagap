package dk.kb.ginnungagap.archive;

import java.io.File;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class ArchiveWrapperTest extends ExtendedTestCase {

    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
    }

    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }


    @Test
    public void testInit() {
        addDescription("Test the initialization of the wrapper for the test instance.");
        Configuration conf = TestFileUtils.createTempConf();
        try (ArchiveWrapper wrapper = new ArchiveWrapper()) {
            wrapper.conf = conf;
            Assert.assertTrue(conf.getLocalConfiguration().getIsTest());
            wrapper.init();
            Assert.assertTrue(wrapper.archive instanceof LocalArchive);
        }
    }

    @Test
    public void testUploadFile() {
        addDescription("Test the uploadFile method.");
        Archive archive = Mockito.mock(Archive.class);
        File f = new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        String collectionId = UUID.randomUUID().toString();

        ArchiveWrapper wrapper = new ArchiveWrapper();
        wrapper.archive = archive;

        wrapper.uploadFile(f, collectionId);

        Mockito.verify(archive).uploadFile(Mockito.eq(f), Mockito.eq(collectionId));
        Mockito.verifyNoMoreInteractions(archive);
    }

    @Test
    public void testGetFile() {
        addDescription("Test the getFile method.");
        Archive archive = Mockito.mock(Archive.class);
        String fileId = UUID.randomUUID().toString();
        String collectionId = UUID.randomUUID().toString();

        ArchiveWrapper wrapper = new ArchiveWrapper();
        wrapper.archive = archive;

        wrapper.getFile(fileId, collectionId);

        Mockito.verify(archive).getFile(Mockito.eq(fileId), Mockito.eq(collectionId));
        Mockito.verifyNoMoreInteractions(archive);
    }

    @Test
    public void testGetChecksum() {
        addDescription("Test the getChecksum method.");
        Archive archive = Mockito.mock(Archive.class);
        String fileId = UUID.randomUUID().toString();
        String collectionId = UUID.randomUUID().toString();

        ArchiveWrapper wrapper = new ArchiveWrapper();
        wrapper.archive = archive;

        wrapper.getChecksum(fileId, collectionId);

        Mockito.verify(archive).getChecksum(Mockito.eq(fileId), Mockito.eq(collectionId));
        Mockito.verifyNoMoreInteractions(archive);
    }

    @Test
    public void testClose() {
        addDescription("Test the close method.");
        Archive archive = Mockito.mock(Archive.class);
        String fileId = UUID.randomUUID().toString();
        String collectionId = UUID.randomUUID().toString();

        ArchiveWrapper wrapper = new ArchiveWrapper();
        wrapper.archive = archive;

        wrapper.close();

        Mockito.verify(archive).close();
        Mockito.verifyNoMoreInteractions(archive);
    }
    
}
