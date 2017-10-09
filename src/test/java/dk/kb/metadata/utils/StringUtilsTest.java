package dk.kb.metadata.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        StringUtils su = new StringUtils();
        Assert.assertNotNull(su);
    }
    
    @Test
    public void testSplitOutOfBounds() {
        Assert.assertNull(StringUtils.split("1, 2, 3, 4", ",", 10));
    }
    
    @Test
    public void testSplitableOnCommaTrue() {
        Assert.assertTrue(StringUtils.splitableOnComma("asfd, fdsa"));
    }
    
    @Test
    public void testSplitableOnCommaFalse() {
        Assert.assertFalse(StringUtils.splitableOnComma("asfd-fdsa"));
    }

    @Test
    public void testSplitOnComma() {
        String prefix = "asdf";
        String suffix = "fdsa";
        String testLine = prefix + ", " + suffix;
        Assert.assertEquals(StringUtils.splitOnComma(testLine, 0), prefix);
        Assert.assertEquals(StringUtils.splitOnComma(testLine, 1), suffix);
    }
    
    @Test
    public void testSplitOnCommaFail() {
        Assert.assertNull(StringUtils.splitOnComma("asdf-fdsa", 10));
    }
    
    @Test
    public void testSplitableOnSlashTrue() {
        Assert.assertTrue(StringUtils.splitableOnSlash("asfd/fdsa"));
    }
    
    @Test
    public void testSplitableOnSlashFalse() {
        Assert.assertFalse(StringUtils.splitableOnSlash("asfd-fdsa"));
    }
    
    @Test
    public void testSplitOnSlash() {
        String prefix = "asdf";
        String suffix = "fdsa";
        String testLine = prefix + "/" + suffix;
        Assert.assertEquals(StringUtils.splitOnSlash(testLine, 0), prefix);
        Assert.assertEquals(StringUtils.splitOnSlash(testLine, 1), suffix);
    }
    
    @Test
    public void testSplitOnSlashFail() {
        Assert.assertNull(StringUtils.splitOnSlash("asdf-fdsa", 10));
    }
    
    @Test
    public void testCalculateFraction() {
        Float nominator = 4.5f;
        Float denominator = 1.2f;
        String res = StringUtils.calculateFraction(nominator.toString() + "/" + denominator);
        
        Assert.assertEquals(res, "" + nominator/denominator);
    }
    
    @Test
    public void testCalculateFractionFail() {
        String line = "ASDFFDSA";
        Assert.assertEquals(line, StringUtils.calculateFraction(line));
    }
    
    @Test
    public void testExtractIntegerFromDouble() {
        Double d = 3.1415d;
        Assert.assertEquals("3", StringUtils.extractIntegerFromDouble(d.toString()));
    }
    
    @Test
    public void testEncodeAsUpperCamelCase() {
        Assert.assertEquals("AsdfFdsa", StringUtils.encodeAsUpperCamelCase("asdf", "fdsa"));
    }
    
    @Test
    public void testRetrieveNominatorAsIntegerFraction() {
        Assert.assertEquals("31415", StringUtils.retrieveNominatorAsInteger("3.1415"));
    }
    
    @Test
    public void testRetrieveNominatorAsIntegerNonFraction() {
        Assert.assertEquals("223", StringUtils.retrieveNominatorAsInteger("223"));
    }
    
    @Test
    public void testRetrieveDenominatorAsIntegerFraction() {
        Assert.assertEquals("10000", StringUtils.retrieveDenominatorAsInteger("3.1415"));
    }
    
    @Test
    public void testRetrieveDenominatorAsIntegerNonFraction() {
        Assert.assertEquals("1", StringUtils.retrieveDenominatorAsInteger("223"));
    }
}
