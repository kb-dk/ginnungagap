package dk.kb.metadata.utils;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class FileIdHandlerTest extends ExtendedTestCase {

    String defaultGUID = UUID.randomUUID().toString();
    
    @BeforeClass
    public void setupClass() {
        FileIdHandler.clean();
    }
    
    @Test
    public void testHandler() {
        Assert.assertEquals(0, FileIdHandler.FILE_IDS.size());
        String res = FileIdHandler.getFileID(defaultGUID);
        Assert.assertEquals(res, FileIdHandler.getFileID(defaultGUID));
        Assert.assertEquals(1, FileIdHandler.FILE_IDS.size());
        FileIdHandler.clean();
        Assert.assertEquals(0, FileIdHandler.FILE_IDS.size());
    }
}
