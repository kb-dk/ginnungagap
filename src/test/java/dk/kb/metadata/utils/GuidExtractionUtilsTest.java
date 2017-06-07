package dk.kb.metadata.utils;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class GuidExtractionUtilsTest extends ExtendedTestCase {
    
    @Test
    public void testExtractUUIDWithoutEndHash() {
        addDescription("Test extracting an UUID from a ARC-record URL, whith no suffix");
        String expectedUuid = UUID.randomUUID().toString();
        String url = "prefix://" + expectedUuid;

        String uuid = GuidExtrationUtils.extractGuid(url);
        Assert.assertEquals(expectedUuid, uuid);
    }
}
