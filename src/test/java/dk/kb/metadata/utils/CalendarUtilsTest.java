package dk.kb.metadata.utils;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CalendarUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        CalendarUtils eu = new CalendarUtils();
        Assert.assertNotNull(eu);
    }
    
    @Test
    public void testGetCurrentDate() {
        String now = CalendarUtils.getCurrentDate();
        Assert.assertNotNull(now);
        Assert.assertFalse(now.isEmpty());
    }
    
    @Test
    public void testGetXmlGregorianCalendarSuccess() {
        XMLGregorianCalendar res = CalendarUtils.getXmlGregorianCalendar(new Date(0));
        Assert.assertNotNull(res);
        Assert.assertEquals(res.toGregorianCalendar().getTime().getTime(), 0);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetXmlGregorianCalendarFailure() {
        CalendarUtils.getXmlGregorianCalendar(null);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetDateTimeFailure() {
        CalendarUtils.getDateTime("EEE YYYY MMM dd, hh:mm:ss", "THIS IS NOT A PROPER DATE FoRMAT");
    }
}
