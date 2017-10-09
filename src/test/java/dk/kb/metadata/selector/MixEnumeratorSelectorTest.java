package dk.kb.metadata.selector;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MixEnumeratorSelectorTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Tests the constructor.");
        MixEnumeratorSelector mes = new MixEnumeratorSelector();
        Assert.assertNotNull(mes);
    }
    
    @Test
    public void testExifVersionSuccess() {
        addDescription("Test the different valid values for the exif version.");
        for(String s : MixEnumeratorSelector.EXIF_VERSIONS) {
            Assert.assertEquals(MixEnumeratorSelector.exifVersion(s), s);
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testExifVersionFailure() {
        addDescription("Test the failure when the exif version is invalid");
        MixEnumeratorSelector.exifVersion("1234567890");
    }
    
    @Test
    public void testOrientationsSuccess() {
        addDescription("Test the orientation when given the default valid values.");
        for(String s : MixEnumeratorSelector.ORIENTATIONS) {
            Assert.assertEquals(MixEnumeratorSelector.orientation(s), s);
        }
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForNormal() {
        addDescription("Test the orientation when given the alternative value for 'normal'");
        String expected = MixEnumeratorSelector.ORIENTATION_NORMAL;
        Assert.assertEquals(MixEnumeratorSelector.orientation("top left"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("1"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForFlipped() {
        addDescription("Test the orientation when given the alternative value for 'flipped'");
        String expected = MixEnumeratorSelector.ORIENTATION_FLIPPED;
        Assert.assertEquals(MixEnumeratorSelector.orientation("top right"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("2"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForRotated180() {
        addDescription("Test the orientation when given the alternative value for 'rotated 180'");
        String expected = MixEnumeratorSelector.ORIENTATION_ROTATED_180;
        Assert.assertEquals(MixEnumeratorSelector.orientation("bottom right"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("3"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForFlippedRotated180() {
        addDescription("Test the orientation when given the alternative value for 'flipped rotated 180'");
        String expected = MixEnumeratorSelector.ORIENTATION_FLIPPED_ROTATED_180;
        Assert.assertEquals(MixEnumeratorSelector.orientation("bottom left"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("4"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForFlippedRotatedCw90() {
        addDescription("Test the orientation when given the alternative value for 'flipped rotated cw 90'");
        String expected = MixEnumeratorSelector.ORIENTATION_FLIPPED_ROTATED_CW_90;
        Assert.assertEquals(MixEnumeratorSelector.orientation("left top"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("5"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForRotatedCcw90() {
        addDescription("Test the orientation when given the alternative value for 'rotated ccw 90'");
        String expected = MixEnumeratorSelector.ORIENTATION_ROTATED_CCW_90;
        Assert.assertEquals(MixEnumeratorSelector.orientation("right top"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("6"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForFlippedRotatedCcw90() {
        addDescription("Test the orientation when given the alternative value for 'flipped rotated ccw 90'");
        String expected = MixEnumeratorSelector.ORIENTATION_FLIPPED_ROTATED_CCW_90;
        Assert.assertEquals(MixEnumeratorSelector.orientation("right bottom"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("7"), expected);
    }
    
    @Test
    public void testOrientationSuccessAlternativeValuesForRotated90() {
        addDescription("Test the orientation when given the alternative value for 'rotated 90'");
        String expected = MixEnumeratorSelector.ORIENTATION_ROTATED_90;
        Assert.assertEquals(MixEnumeratorSelector.orientation("left bottom"), expected);
        Assert.assertEquals(MixEnumeratorSelector.orientation("8"), expected);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testOrientationFailure() {
        addDescription("Test the failure when the orientation is invalid");
        MixEnumeratorSelector.orientation("Inside out");
    }
    
    @Test
    public void testValidMeteringMode() {
        addDescription("Test the validation of the metering mode");
        Assert.assertTrue(MixEnumeratorSelector.validMeteringMode("METERING MODE"));
        Assert.assertFalse(MixEnumeratorSelector.validMeteringMode(null));
        Assert.assertFalse(MixEnumeratorSelector.validMeteringMode(""));
        Assert.assertFalse(MixEnumeratorSelector.validMeteringMode("0"));
    }
    
    @Test
    public void testMeteringModeSuccess() {
        addDescription("Test the success of the metering mode, when given the default values.");
        for(String s : MixEnumeratorSelector.METERING_MODE_RESTRICTION) {
            Assert.assertEquals(MixEnumeratorSelector.meteringMode(s), s);
        }
    }
    
    @Test
    public void testMeteringModeSuccessIndices() {
        addDescription("Test the success of the metering mode, when given the index of the valid values.");
        Assert.assertEquals(MixEnumeratorSelector.meteringMode("1"), MixEnumeratorSelector.METERING_MODE_AVERAGE);
        Assert.assertEquals(MixEnumeratorSelector.meteringMode("2"), MixEnumeratorSelector.METERING_MODE_CENTER_WEIGHTED_AVERAGE);
        Assert.assertEquals(MixEnumeratorSelector.meteringMode("3"), MixEnumeratorSelector.METERING_MODE_SPOT);
        Assert.assertEquals(MixEnumeratorSelector.meteringMode("4"), MixEnumeratorSelector.METERING_MODE_MULTISPOT);
        Assert.assertEquals(MixEnumeratorSelector.meteringMode("5"), MixEnumeratorSelector.METERING_MODE_PATTERN);
        Assert.assertEquals(MixEnumeratorSelector.meteringMode("6"), MixEnumeratorSelector.METERING_MODE_PARTIAL);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMeteringModeFailureBadString() {
        addDescription("Test the failure of the metering mode, when given an incorrect value");
        MixEnumeratorSelector.meteringMode("THIS IS NOT A VALID METERING MODE");
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testMeteringModeFailureWrongIndex() {
        addDescription("Test the failure of the metering mode, when given a bad index");
        MixEnumeratorSelector.meteringMode("123456");
    }
    
    @Test
    public void testColorSpace() {
        addDescription("Test the colorSpace method.");
        Assert.assertEquals(MixEnumeratorSelector.colorSpace(""), "");
        Assert.assertEquals(MixEnumeratorSelector.colorSpace("MONOCHROME"), "MONOCHROME");
        Assert.assertEquals(MixEnumeratorSelector.colorSpace("Wrong Color"), "Wrong");
    }
    
    @Test
    public void testExposureProgramSuccess() {
        addDescription("Test the exposureProgram when given the default valid values.");
        for(String s : MixEnumeratorSelector.EXPOSURE_PROGRAMS) {
            Assert.assertEquals(MixEnumeratorSelector.exposureProgram(s), s);
        }
    }
    
    @Test
    public void testExposureProgramSuccessIndices() {
        addDescription("Test the exposureProgram when given the indices for values.");
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("0"), MixEnumeratorSelector.EXPOSURE_PROGRAM_NOT_DEFINED);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("1"), MixEnumeratorSelector.EXPOSURE_PROGRAM_MANUEL);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("2"), MixEnumeratorSelector.EXPOSURE_PROGRAM_NORMAL_PROGRAM);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("3"), MixEnumeratorSelector.EXPOSURE_PROGRAM_APERTURE_PRIORITY);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("4"), MixEnumeratorSelector.EXPOSURE_PROGRAM_SHUTTER_PRIORITY);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("5"), MixEnumeratorSelector.EXPOSURE_PROGRAM_CREATIVE_PROGRAM);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("6"), MixEnumeratorSelector.EXPOSURE_PROGRAM_ACTION_PROGRAM);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("7"), MixEnumeratorSelector.EXPOSURE_PROGRAM_PORTRAIT_MODE);
        Assert.assertEquals(MixEnumeratorSelector.exposureProgram("8"), MixEnumeratorSelector.EXPOSURE_PROGRAM_LANDSCAPE_MODE);
    }
    
    @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
    public void testExposureProgramFailureIndexOutOfBounds() {
        addDescription("Test the exposureProgram when given an integer which is larger than the number of exposure programs.");
        MixEnumeratorSelector.exposureProgram("100000");
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testExposureProgramFailureBadValue() {
        addDescription("Test the exposureProgram when given an incorrect value.");
        MixEnumeratorSelector.exposureProgram("THIS IS NOT AN EXPOSURE PROGRAM");
    }
    
    @Test
    public void testLightSourceSuccess() {
        addDescription("Test the lightSource method");
        for(String s : MixEnumeratorSelector.LIGHT_SOURCES) {
            Assert.assertEquals(MixEnumeratorSelector.lightSource(s), s);
            Assert.assertEquals(MixEnumeratorSelector.lightSource(s.toLowerCase()), s);
            Assert.assertEquals(MixEnumeratorSelector.lightSource(s.toUpperCase()), s);
        }
    }
     
    @Test
    public void testLightSourceSuccessDefaultValue() {
        addDescription("Test the default for the lightSource method value when given a value which does not match any.");
        Assert.assertEquals(MixEnumeratorSelector.lightSource("Darkness"), MixEnumeratorSelector.LIGHT_SOURCE_OTHER_LIGHT_SOURCE);
    }
    
    @Test
    public void testValidExposureBias() {
        addDescription("Test the validExposure method");
        Assert.assertTrue(MixEnumeratorSelector.validExposureBias("This is exposure"));
        Assert.assertFalse(MixEnumeratorSelector.validExposureBias(null));
        Assert.assertFalse(MixEnumeratorSelector.validExposureBias(""));
        Assert.assertFalse(MixEnumeratorSelector.validExposureBias("0"));
    }
}
