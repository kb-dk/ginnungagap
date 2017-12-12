package dk.kb.metadata.utils;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class GuidExtractionUtilsTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        GuidExtrationUtils geu = new GuidExtrationUtils();
        Assert.assertNotNull(geu);
    }
    
    @Test
    public void testExtractUUIDWithoutEndHash() {
        addDescription("Test extracting an UUID from a ARC-record URL, whith no suffix");
        String expectedUuid = UUID.randomUUID().toString();
        String url = "prefix://" + expectedUuid;

        String uuid = GuidExtrationUtils.extractGuid(url);
        Assert.assertEquals(expectedUuid, uuid);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExtractGuidFailureNullArgument() {
        addDescription("Test the way the extractGuid method fails, when given a null argument");
        GuidExtrationUtils.extractGuid(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExtractGuidFailureEmptyStringArgument() {
        addDescription("Test the way the extractGuid method fails, when given the empty string as argument");
        GuidExtrationUtils.extractGuid("");
    }
    
    @Test
    public void testExtractGuidContainingHash() {
        addDescription("Test the extractGuid method, when the GUID contains a hash (#)");
        String prefix = UUID.randomUUID().toString();
        String suffix = UUID.randomUUID().toString();
        String guid = GuidExtrationUtils.extractGuid(prefix + "#" + suffix);
        Assert.assertEquals(guid, prefix);
    }
}
