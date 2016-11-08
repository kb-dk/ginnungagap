package dk.kb.metadata.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dk.kb.metadata.utils.ExceptionUtils;

/**
 * Contains the different selectors for the MIX metadata, some will translate the value into the 
 * corresponding in the enumerator.
 * Handles the selection of different enumerators used for MIX.
 * 
 * The different selectors will through 'IllegalStateException' if the given value 
 * cannot be found within their enumerator.
 */
public final class MixEnumeratorSelector {
    /** Private constructor for this Utility class.*/
    private MixEnumeratorSelector() {}

    /** The collection of possible EXIF versions accepted by the MIX standard.*/
    public static Set<String> EXIF_VERSIONS = new HashSet<String>(Arrays.asList("0220", "0221", "0230"));

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

        IllegalStateException res = new IllegalStateException("Could not convert the exifVersion '" + cumulusExifVersion + "' into any "
                + "of the legal versions: " + EXIF_VERSIONS);
        ExceptionUtils.insertException(res);
        throw res;
    }

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
        } else if(orientation.equalsIgnoreCase("left top") | orientation.equals("5")) {
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

    /** Metering mode value: Average */
    private static String METERING_MODE_AVERAGE = "Average";
    /** Metering mode value: Center weighted average */
    private static String METERING_MODE_CENTER_WEIGHTED_AVERAGE = "Center weighted average";
    /** Metering mode value: Spot */
    private static String METERING_MODE_SPOT = "Spot";
    /** Metering mode value: Multispot */
    private static String METERING_MODE_MULTISPOT = "Multispot";
    /** Metering mode value: Pattern */
    private static String METERING_MODE_PATTERN = "Pattern";
    /** Metering mode value: Partial */
    private static String METERING_MODE_PARTIAL = "Partial";

    /** The collection of possible values for the field 'mix:meteringMode'.*/
    private static Set<String> METERING_MODE_RESTRICTION = new HashSet<String>(Arrays.asList(METERING_MODE_AVERAGE, 
            METERING_MODE_CENTER_WEIGHTED_AVERAGE, METERING_MODE_SPOT, METERING_MODE_MULTISPOT, METERING_MODE_PATTERN, 
            METERING_MODE_PARTIAL));

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
            case 1: return METERING_MODE_AVERAGE;
            case 2: return METERING_MODE_CENTER_WEIGHTED_AVERAGE;
            case 3: return METERING_MODE_SPOT;
            case 4: return METERING_MODE_MULTISPOT;
            case 5: return METERING_MODE_PATTERN;
            case 6: return METERING_MODE_PARTIAL;
            }
        } catch (NumberFormatException e) {
            // Not a valid number, thus throw the exception in next line.
        }

        IllegalStateException res = new IllegalStateException("Could not convert the meteringMode '" + meteringMode + "' into any "
                + "of the legal versions: " + METERING_MODE_RESTRICTION);
        ExceptionUtils.insertException(res);
        throw res;
    }

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
}
