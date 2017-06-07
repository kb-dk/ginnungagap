package dk.kb.metadata.utils;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class FileIdHandlerTest extends ExtendedTestCase {

    String defaultGUID = UUID.randomUUID().toString();
    
    @Test
    public void testHandler() {
        Assert.assertEquals(0, FileIdHandler.fileIds.size());
        String res = FileIdHandler.getFileID(defaultGUID);
        Assert.assertEquals(res, FileIdHandler.getFileID(defaultGUID));
        Assert.assertEquals(1, FileIdHandler.fileIds.size());
        FileIdHandler.clean();
        Assert.assertEquals(0, FileIdHandler.fileIds.size());
    }
}
