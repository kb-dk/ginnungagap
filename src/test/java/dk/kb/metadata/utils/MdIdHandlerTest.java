package dk.kb.metadata.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MdIdHandlerTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        MdIdHandler mih = new MdIdHandler();
        Assert.assertNotNull(mih);
    }

    @Test
    public void testCreateNewMdId() {
        String id = "GNU";
        String mdId = MdIdHandler.createNewMdId(id);
        Assert.assertTrue(mdId.contains(id));
        
        String mdId2 = MdIdHandler.createNewMdId(id);
        Assert.assertTrue(mdId2.contains(id));
        Assert.assertNotEquals(mdId, mdId2);
    }
    
    // THE REST OF THE CLASS IS TESTED INDIRECTLY THROUGH OTHER TESTS
}
