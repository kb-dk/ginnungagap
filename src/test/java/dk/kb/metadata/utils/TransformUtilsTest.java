package dk.kb.metadata.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TransformUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        TransformUtils eu = new TransformUtils();
        Assert.assertNotNull(eu);
    }
    
    @Test
    public void testGetCumulusVal() {
        Assert.assertEquals(TransformUtils.getCumulusVal("TEST VALUE"), "TEST VALUE");
        Assert.assertEquals(TransformUtils.getCumulusVal("TEST|VALUE"), "VALUE");
    }
    
    @Test
    public void testGetCumulusSimpleVal() {
        Assert.assertEquals(TransformUtils.getCumulusSimpleVal("TEST VALUE"), "TEST VALUE");
        Assert.assertEquals(TransformUtils.getCumulusSimpleVal("TEST|VALUE"), "VALUE");
    }
    
    @Test
    public void testIsCumulusValNonSort() {
        Assert.assertFalse(TransformUtils.isCumulusValNonSort("RANDOM STUFF"));
        Assert.assertTrue(TransformUtils.isCumulusValNonSort("GNU<<ASDF>>GNU"));
    }
    
    @Test
    public void testGetCumulusValNonSort() {
        Assert.assertEquals(TransformUtils.getCumulusValNonSort("TEST VALUE"), "TEST VALUE");
        Assert.assertEquals(TransformUtils.getCumulusValNonSort("TEST <<GNU>> VALUE"), "GNU");
    }
    
    @Test
    public void testIsCumulusValTranslit() {
        Assert.assertFalse(TransformUtils.isCumulusValTranslit("RANDOM STUFF"));
        Assert.assertTrue(TransformUtils.isCumulusValTranslit("GNU<<ASDF=FDSA>>GNU"));
    }
    
    @Test
    public void testGetCumulusValTranslit() {
        Assert.assertEquals(TransformUtils.getCumulusValTranslit("TEST VALUE"), "TEST VALUE");
        Assert.assertEquals(TransformUtils.getCumulusValTranslit("TEST|VALUE"), "VALUE");
    }
    
    @Test
    public void testApplyRules() {
        Assert.assertEquals(TransformUtils.applyRules("", TransformUtils.IS_NON_TRANSLITERATION), "");
        Assert.assertEquals(TransformUtils.applyRules("", TransformUtils.IS_TRANSLITERATION_REX), "");
        Assert.assertEquals(TransformUtils.applyRules("", TransformUtils.IS_RSS), "");
        
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU>>ASDF", TransformUtils.IS_NON_TRANSLITERATION), "ASDFASDF");
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU>>ASDF", TransformUtils.IS_TRANSLITERATION_REX), "ASDFASDF");
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU>>ASDF", TransformUtils.IS_RSS), "ASDF<<GNU>>ASDF");
        
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU=EMU>>ASDF", TransformUtils.IS_NON_TRANSLITERATION), "ASDFGNUASDF");
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU=EMU>>ASDF", TransformUtils.IS_TRANSLITERATION_REX), "ASDFEMUASDF");
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU=EMU>>ASDF", TransformUtils.IS_RSS), "ASDFEMUASDF");
        
        Assert.assertEquals(TransformUtils.applyRules("ASDF<<GNU=EMU>>ASDF", 100), "ASDF<<GNU=EMU>>ASDF");
    }
    
    @Test
    public void testGetCumulusLang() {
        String defaultLang1 = "GNU";
        String defaultLang2 = "en";

        Assert.assertEquals(TransformUtils.getCumulusLang("String with no language tag", defaultLang1), defaultLang1);
        Assert.assertEquals(TransformUtils.getCumulusLang("String with no language tag", defaultLang2), defaultLang2);
        
        Assert.assertEquals(TransformUtils.getCumulusLang("TEST|String with no language tag", defaultLang1), defaultLang1);
        Assert.assertEquals(TransformUtils.getCumulusLang("da|String with no language tag", defaultLang1), "da");
    }
    
    @Test
    public void testIsTocTitle() {
        Assert.assertFalse(TransformUtils.isTocTitle("NO LANGUAGE SEPARATOR", "LANG1", "LANG2"));
        Assert.assertTrue(TransformUtils.isTocTitle("NO LANGUAGE SEPARATOR", "LANG1", "LANG1"));
        
        Assert.assertFalse(TransformUtils.isTocTitle("RANDOM|WITH LANGUAGE SEPARATOR", "LANG1", "LANG2"));
        Assert.assertTrue(TransformUtils.isTocTitle("LANG1|WITH LANGUAGE SEPARATOR", "LANG1", "LANG2"));        
    }
    
    @Test
    public void testGetIsoDate() {
        Assert.assertEquals(TransformUtils.getIsoDate("YYYY-MM-dd", "YYYY", "1970-01-01"), "1970");
        Assert.assertEquals(TransformUtils.getIsoDate("YYYY-MM-dd", "YYYY", "THIS DOES NOT MATCH THE PATTERN"), "0000-00-00");
    }
    
    @Test
    public void testGetTocTitle() {
        Assert.assertEquals(TransformUtils.getTocTitle("gnu|test", "da"), "gnu|test");
        Assert.assertEquals(TransformUtils.getTocTitle("da|test", "da"), "test");
    }
}
