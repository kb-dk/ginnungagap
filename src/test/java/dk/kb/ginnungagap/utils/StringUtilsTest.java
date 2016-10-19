package dk.kb.ginnungagap.utils;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

public class StringUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the String Utils constructor.");
        StringUtils su = new StringUtils();
        assertNotNull(su);
    }
    
    @Test
    public void testReplacingSpacesWithTabs() {
        addDescription("Test replace spaces with tabs");
        String s = "asdf fdsa 1 2 3 4 5 6 7 8 9 0";
        assertTrue(s.contains(" "));
        assertFalse(s.contains("\t"));
        String res = StringUtils.replaceSpacesToTabs(s);
        assertFalse(res.contains(" "));
        assertTrue(res.contains("\t"));
    }
}
