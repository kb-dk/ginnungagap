package dk.kb.metadata.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.kb.metadata.utils.ExceptionUtils;

/**
 * Contains the different selectors for the MIX metadata, some will translate the value into the 
 * corresponding in the enumerator.
 * Handles the selection of different enumerators used for MIX.
 * 
 * The different selectors will throw 'IllegalStateException' if the given value cannot be found within 
 * their enumerator.
 */
public final class MixEnumeratorSelector {
    /** Private constructor for this Utility class.*/
    private MixEnumeratorSelector() {}
    
    // --------------------------
    // EXIF VERSION
    // --------------------------
    
    /** The collection of possible EXIF versions accepted by the MIX standard.*/
    public static final Set<String> EXIF_VERSIONS = new HashSet<String>(Arrays.asList("0220", "0221", "0230"));

    /**
     * Extracts a exif version based on the value of a Cumulus field.
     * @param cumulusExifVersion The value from the Cumulus field.
     * @return The value from the Cumulus field, which are to be extracted as a exif version.
     */
    public static String exifVersion(String cumulusExifVersion) {
        String simple = cumulusExifVersion.replaceAll("\\D", "");

        for(String version : EXIF_VERSIONS) {
            if(version.contains(simple)) {
                return version;
            }
        }

        IllegalStateException res = new IllegalStateException("Could not convert the exifVersion '" 
                + cumulusExifVersion + "' into any of the legal versions: " + EXIF_VERSIONS);
        ExceptionUtils.insertException(res);
        throw res;
    }

    // --------------------------
    // ORIENTATION
    // --------------------------
    /*
    // Taken from http://www.impulseadventure.com/photo/exif-orientation.html
    EXIF Orientation Value  Row #0 is:  Column #0 is:                   MIX
            1               Top         Left side                       normal*
            2*              Top         Right side   (Flipped)          normal, image flipped
            3               Bottom      Right side                      normal, rotated 180°
            4*              Bottom      Left side    (Flipped)          normal, image flipped, rotated 180°
            5*              Left side   Top          (Flipped)          normal, image flipped, rotated cw 90°
            6               Right side  Top                             normal, rotated ccw 90°
            7*              Right side  Bottom       (Flipped)          normal, image flipped, rotated ccw 90°
            8               Left side   Bottom                          normal, rotated cw 90°
     */
    /** 1               Top         Left side                       normal* */
    private static final String ORIENTATION_NORMAL = "normal*";
    /** 2*              Top         Right side   (Flipped)          normal, image flipped */
    private static final String ORIENTATION_FLIPPED = "normal, image flipped";
    /** 3               Bottom      Right side                      normal, rotated 180° */
    private static final String ORIENTATION_ROTATED_180 = "normal, rotated 180°";
    /** 4*              Bottom      Left side    (Flipped)          normal, image flipped, rotated 180° */
    private static final String ORIENTATION_FLIPPED_ROTATED_180 = "normal, image flipped, rotated 180°";
    /** 5*              Left side   Top          (Flipped)          normal, image flipped, rotated cw 90° */
    private static final String ORIENTATION_FLIPPED_ROTATED_CW_90 = "normal, image flipped, rotated cw 90°";
    /** 6               Right side  Top                             normal, rotated ccw 90° */
    private static final String ORIENTATION_ROTATED_CCW_90 = "normal, rotated ccw 90°";
    /** 7*              Right side  Bottom       (Flipped)          normal, image flipped, rotated ccw 90° */
    private static final String ORIENTATION_FLIPPED_ROTATED_CCW_90 = "normal, image flipped, rotated ccw 90°";
    /** 8               Left side   Bottom                          normal, rotated cw 90° */
    private static final String ORIENTATION_ROTATED_90 = "normal, rotated cw 90°";
    /** Only alternative value! */
    private static final String ORIENTATION_UNKNOWN = "unknown";

    /** The collection of possible values for the orientation.*/
    private static final Set<String> ORIENTATIONS = new HashSet<String>(Arrays.asList(ORIENTATION_NORMAL, 
            ORIENTATION_FLIPPED, ORIENTATION_ROTATED_180, ORIENTATION_FLIPPED_ROTATED_180, 
            ORIENTATION_FLIPPED_ROTATED_CW_90, ORIENTATION_ROTATED_CCW_90, 
            ORIENTATION_FLIPPED_ROTATED_CCW_90, ORIENTATION_ROTATED_90, ORIENTATION_UNKNOWN));

    /**
     * Retrieves the value for the "mix:orientation" field.
     * Some values will be translated into their corresponding value from the enumerator.
     * @param orientation The value for the orientation.
     * @return The orientation, either directly or the corresponding value.
     */
    public static String orientation(String orientation) {
        if(ORIENTATIONS.contains(orientation.toLowerCase())) {
            return orientation;
        }

        if(orientation.equalsIgnoreCase("top left") || orientation.equals("1")) {
            return ORIENTATION_NORMAL;
        } else if(orientation.equalsIgnoreCase("top right") || orientation.equals("2")) {
            return ORIENTATION_FLIPPED;
        } else if (orientation.equalsIgnoreCase("bottom right") || orientation.equals("3")) {
            return ORIENTATION_ROTATED_180;
        } else if (orientation.equalsIgnoreCase("bottom left") || orientation.equals("4")) {
            return ORIENTATION_FLIPPED_ROTATED_180;
        } else if(orientation.equalsIgnoreCase("left top") || orientation.equals("5")) {
            return ORIENTATION_FLIPPED_ROTATED_CW_90;
        } else if(orientation.equalsIgnoreCase("right top") || orientation.equals("6")) {
            return ORIENTATION_ROTATED_CCW_90;
        } else if(orientation.equalsIgnoreCase("right bottom") || orientation.equals("7")) {
            return ORIENTATION_FLIPPED_ROTATED_CCW_90;
        } else if(orientation.equalsIgnoreCase("left bottom") || orientation.equals("8")) {
            return ORIENTATION_ROTATED_90;
        }

        IllegalStateException res = new IllegalStateException("The orientation '" + orientation 
                + "' is invalid for the MIX restrictions: '" 
                + ORIENTATIONS + "'");
        ExceptionUtils.insertException(res);
        throw res;
    }
    
    // --------------------------
    // METERING MODE
    // --------------------------

    /** Metering mode value: Average */
    private static final String METERING_MODE_AVERAGE = "Average";
    /** Metering mode value: Center weighted average */
    private static final String METERING_MODE_CENTER_WEIGHTED_AVERAGE = "Center weighted average";
    /** Metering mode value: Spot */
    private static final String METERING_MODE_SPOT = "Spot";
    /** Metering mode value: Multispot */
    private static final String METERING_MODE_MULTISPOT = "Multispot";
    /** Metering mode value: Pattern */
    private static final String METERING_MODE_PATTERN = "Pattern";
    /** Metering mode value: Partial */
    private static final String METERING_MODE_PARTIAL = "Partial";

    /** The collection of possible values for the field 'mix:meteringMode'.*/
    private static final Set<String> METERING_MODE_RESTRICTION = new HashSet<String>(Arrays.asList(
            METERING_MODE_AVERAGE,METERING_MODE_CENTER_WEIGHTED_AVERAGE, METERING_MODE_SPOT, METERING_MODE_MULTISPOT, 
            METERING_MODE_PATTERN, METERING_MODE_PARTIAL));

    /**
     * Figures out whether the MeteringMode field is valid.
     * E.g. Whether it is null, the empty String or the EXIF value for unknown ('0').
     * 
     * @param meteringMode The metering mode field value.
     * @return Whether it is valid or not.
     */
    public static Boolean validMeteringMode(String meteringMode) {
        if(meteringMode == null || meteringMode.isEmpty() || meteringMode.equals("0")) {
            return false;
        }

        return true;
    }

    /**
     * Retrieves the values for the field 'mix:meteringMode'.
     * Maps the following from EXIF, if it is not the MIX values:
     * 1 = Average 
     * 2 = CenterWeightedAverage
     * 3 = Spot
     * 4 = MultiSpot
     * 5 = Pattern
     * 6 = Partial 
     * 
     * @param meteringMode The meteringMode.
     * @return The given metering mode.
     */
    public static String meteringMode(String meteringMode) {
        for(String restriction : METERING_MODE_RESTRICTION) {
            if(restriction.contains(meteringMode)) {
                return restriction;
            }
        }

        try {
            Integer i = Integer.parseInt(meteringMode);
            switch (i) {
            case 1: 
                return METERING_MODE_AVERAGE;
            case 2: 
                return METERING_MODE_CENTER_WEIGHTED_AVERAGE;
            case 3: 
                return METERING_MODE_SPOT;
            case 4: 
                return METERING_MODE_MULTISPOT;
            case 5: 
                return METERING_MODE_PATTERN;
            case 6: 
                return METERING_MODE_PARTIAL;
            default: 
                throw new NumberFormatException("Cannot handle the number '" + i + "'.");
            }
        } catch (NumberFormatException e) {
            IllegalStateException res = new IllegalStateException("Could not convert the meteringMode '" 
                    + meteringMode + "' into any of the legal versions: " + METERING_MODE_RESTRICTION);
            ExceptionUtils.insertException(res);
            throw res;
        }
    }
    
    // --------------------------
    // COLOR SPACE
    // --------------------------

    /**
     * Removes the potential suffix of the colorSpace value.
     * E.g. the value 'RGB Color' would become just 'RGB'.
     * @param colorSpace The value with the potential suffix to be removed.
     * @return The colorSpace.
     */
    public static String colorSpace(String colorSpace) {
        if(colorSpace.contains(" Color")) {
            return colorSpace.substring(0,  colorSpace.indexOf(" "));
        }
        return colorSpace;
    }
    
    // --------------------------
    // EXPOSURE PROGRAM
    // --------------------------

    /** Exposure program value for not defined.*/
    public static final String EXPOSURE_PROGRAM_NOT_DEFINED = "Not defined";
    /** Exposure program value for manual.*/
    public static final String EXPOSURE_PROGRAM_MANUEL = "Manual";
    /** Exposure program value for normal program.*/
    public static final String EXPOSURE_PROGRAM_NORMAL_PROGRAM = "Normal program";
    /** Exposure program value for aperture priority.*/
    public static final String EXPOSURE_PROGRAM_APERTURE_PRIORITY = "Aperture priority";
    /** Exposure program value for shutter priority.*/
    public static final String EXPOSURE_PROGRAM_SHUTTER_PRIORITY = "Shutter priority";
    /** Exposure program value for creative program (biased toward depth of field).*/
    public static final String EXPOSURE_PROGRAM_CREATIVE_PROGRAM = "Creative program (biased toward depth of field)";
    /** Exposure program value for action program (biased toward fast shutter speed).*/
    public static final String EXPOSURE_PROGRAM_ACTION_PROGRAM = "Action program (biased toward fast shutter speed)";
    /** Exposure program value for portrait mode (for closeup photos with the background out of focus).*/
    public static final String EXPOSURE_PROGRAM_PORTRAIT_MODE = 
            "Portrait mode (for closeup photos with the background out of focus)";
    /** Exposure program value for landscape mode (for landscape photos with the background in focus).*/
    public static final String EXPOSURE_PROGRAM_LANDSCAPE_MODE = 
            "Landscape mode (for landscape photos with the background in focus)";
    
    /** Collection of all possible exposure values.*/
    public static final List<String> EXPOSURE_PROGRAMS = Arrays.asList(EXPOSURE_PROGRAM_NOT_DEFINED, 
            EXPOSURE_PROGRAM_MANUEL,EXPOSURE_PROGRAM_NORMAL_PROGRAM, EXPOSURE_PROGRAM_APERTURE_PRIORITY, 
            EXPOSURE_PROGRAM_SHUTTER_PRIORITY,EXPOSURE_PROGRAM_CREATIVE_PROGRAM, EXPOSURE_PROGRAM_ACTION_PROGRAM, 
            EXPOSURE_PROGRAM_PORTRAIT_MODE, EXPOSURE_PROGRAM_LANDSCAPE_MODE);
    
    /**
     * Converts the Cumulus field for exposure program to a valid value for the MIX field.
     * If it is an integer, then it will return the value at the given index.
     * Will throw an exception, if a valid value for the MIX field cannot be found.
     * @param exposureProgram The exposure program value from the Cumulus field.
     * @return The valid exposure program value for the MIX field.
     */
    public static String exposureProgram(String exposureProgram) {
        for(String validValue : EXPOSURE_PROGRAMS) {
            if(validValue.startsWith(exposureProgram)) {
                return validValue;
            }
        }
        
        try {
            int i = Integer.parseInt(exposureProgram);
            return EXPOSURE_PROGRAMS.get(i);
        } catch (NumberFormatException e) {
            ExceptionUtils.insertException(e);
        }
        
        IllegalStateException res = new IllegalStateException("Could not convert the exposure program '" 
                + exposureProgram + "' into any of the legal values: " + EXPOSURE_PROGRAMS);
        ExceptionUtils.insertException(res);
        throw res;
    }
    
    
    // --------------------------
    // LIGHT SOURCE
    // We have only implemented the values at both Cumulus and the MIX field. 
    // TODO: add the rest of the possible values
    // --------------------------

    /** Light source value for daylight.*/
    public static final String LIGHT_SOURCE_DAYLIGHT = "Daylight";
    /** Light source value for flash.*/
    public static final String LIGHT_SOURCE_FLASH = "Flash";
    /** Light source value for other light source.*/
    public static final String LIGHT_SOURCE_OTHER_LIGHT_SOURCE = "other light source";
    
    /** Collection of all possible light sources.*/
    public static final List<String> LIGHT_SOURCES = Arrays.asList(LIGHT_SOURCE_DAYLIGHT, LIGHT_SOURCE_FLASH,
            LIGHT_SOURCE_OTHER_LIGHT_SOURCE);
    
    /**
     * Converts the Cumulus field for light source to a valid value for the MIX field.
     * Only the two directly translatable values between the Cumulus enumerator and the MIX enumerator
     * is implemented.
     * Any other value from the Cumulus enumerator will be returned as 'other light source'.
     * @param lightSource The light source value from the Cumulus field.
     * @return The valid light source value for the MIX field.
     */
    public static String lightSource(String lightSource) {
        for(String validValue : LIGHT_SOURCES) {
            if(validValue.equalsIgnoreCase(lightSource)) {
                return validValue;
            }
        }
        
        return LIGHT_SOURCE_OTHER_LIGHT_SOURCE;
    }
    
    // --------------------------
    // EXPOSURE BIAS
    // --------------------------

    /**
     * Figures out whether the exposure bias field is valid.
     * E.g. Whether it is null, the empty String or '0' (meaning no bias).
     * 
     * @param exposureBias The exposure bias field value.
     * @return Whether it is valid or not.
     */
    public static Boolean validExposureBias(String exposureBias) {
        if(exposureBias == null || exposureBias.isEmpty() || exposureBias.equalsIgnoreCase("0")) {
            return false;
        }

        return true;
    }
}
