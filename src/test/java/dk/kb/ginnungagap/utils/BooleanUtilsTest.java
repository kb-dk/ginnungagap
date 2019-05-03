package dk.kb.ginnungagap.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BooleanUtilsTest extends ExtendedTestCase {

    @Test
    public void testInstantiation() {
        Object o = new BooleanUtils();
        Assert.assertNotNull(o);
        Assert.assertTrue(o instanceof BooleanUtils);
    }
    
    @Test
    public void testStrings() {
        Assert.assertTrue(BooleanUtils.extractBoolean("true"));
        Assert.assertFalse(BooleanUtils.extractBoolean("false"));
        Assert.assertFalse(BooleanUtils.extractBoolean("THIS IT NOT A VALID BOOLEAN VALUE"));
    }
    
    @Test
    public void testBoolean() {
        Assert.assertTrue(Boolean.TRUE);
        Assert.assertFalse(Boolean.FALSE);
    }
}
