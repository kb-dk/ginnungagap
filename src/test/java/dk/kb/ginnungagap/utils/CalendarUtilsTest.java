package dk.kb.ginnungagap.utils;

import java.util.Date;

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
    
    @Test
    public void testDateToText() {
        Date date = new Date(0);
        String s = CalendarUtils.dateToText(date);
        Assert.assertEquals("to, 1 jan. 1970 01:00:00 Central European Time", s);
    }
}
