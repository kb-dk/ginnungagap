package dk.kb.ginnungagap.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class CalendarUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        CalendarUtils c = new CalendarUtils();
        Assert.assertNotNull(c);
    }
    
    @Test
    public void testNow() {
        String res = CalendarUtils.nowToText();
        Assert.assertNotNull(res);
        Assert.assertFalse(res.isEmpty());
    }
}
