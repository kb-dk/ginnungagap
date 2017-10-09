package dk.kb.metadata.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileFormatUtilsTest extends ExtendedTestCase {

    String imageFormat = FileFormatUtils.FILE_FORMAT_TIFF;
    String audioFormat = FileFormatUtils.FILE_FORMAT_WAVE;
    String sillyFormat = "THE PRIMORIDAL VOID";
    
    @Test
    public void testConstructor() {
        addDescription("Test the constructor.");
        FileFormatUtils ffu = new FileFormatUtils();
        Assert.assertNotNull(ffu);
    }
    
    @Test
    public void testFormatForMix() {
        addDescription("Test the formats for MIX");
        Assert.assertTrue(FileFormatUtils.formatForMix(imageFormat));
        Assert.assertFalse(FileFormatUtils.formatForMix(audioFormat));
        Assert.assertFalse(FileFormatUtils.formatForMix(sillyFormat));
    }
    
    @Test
    public void testFormatForBext() {
        addDescription("Test the formats for BEXT");
        Assert.assertFalse(FileFormatUtils.formatForBext(imageFormat));
        Assert.assertTrue(FileFormatUtils.formatForBext(audioFormat));
        Assert.assertFalse(FileFormatUtils.formatForBext(sillyFormat));
    }
    
    @Test
    public void testFormatForPbCore() {
        addDescription("Test the formats for PBCore");
        Assert.assertFalse(FileFormatUtils.formatForPbCore(imageFormat));
        Assert.assertTrue(FileFormatUtils.formatForPbCore(audioFormat));
        Assert.assertFalse(FileFormatUtils.formatForPbCore(sillyFormat));
    }
}
