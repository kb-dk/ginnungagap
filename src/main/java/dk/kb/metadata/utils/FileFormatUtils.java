package dk.kb.metadata.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for validating whether a file format is relevant for a given metadata schema.
 */
public class FileFormatUtils {

    /** Constructor.*/
    protected FileFormatUtils() {}
    
    /** The file format name for Tiff images. */
    protected static final String FILE_FORMAT_TIFF = "TIFF Image";
    /** The file format name for Wave sound. */
    protected static final String FILE_FORMAT_WAVE = "Wave Sound";

    /** The list of file formats for the MIX metadata schema. */
    protected static final List<String> FORMATS_FOR_MIX = Collections.unmodifiableList(Arrays.asList(
            FILE_FORMAT_TIFF));

    /**
     * Determines whether the file format is amongst the file formats with technical metadata in MIX/NISO.
     * @param format The file format.
     * @return Whether it is a MIX format.
     */
    public static boolean formatForMix(String format) {
        return FORMATS_FOR_MIX.contains(format);
    }


    /** The list of file formats for the BEXT metadata schema. */
    protected static final List<String> FORMATS_FOR_BEXT = Collections.unmodifiableList(Arrays.asList(
            FILE_FORMAT_WAVE));

    /**
     * Determines whether the file format is amongst the file formats with technical metadata in BWF BEXT.
     * @param format The file format.
     * @return Whether it is a BEXT format.
     */
    public static boolean formatForBext(String format) {
        return FORMATS_FOR_BEXT.contains(format);
    }

    /** The list of file formats for the PBCore metadata schema. */
    protected static final List<String> FORMATS_FOR_PBCORE = Collections.unmodifiableList(Arrays.asList(
            FILE_FORMAT_WAVE));

    /**
     * Determines whether the file format is amongst the file formats with metadata in PBCore.
     * @param format The file format.
     * @return Whether it is a PBCore format.
     */
    public static boolean formatForPbCore(String format) {
        return FORMATS_FOR_PBCORE.contains(format);
    }
}
