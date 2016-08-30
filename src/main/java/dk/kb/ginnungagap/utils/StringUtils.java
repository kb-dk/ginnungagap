package dk.kb.ginnungagap.utils;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Utility class for dealings with Strings.
 */
public class StringUtils {

    /**
     * Replaces all spaces with tabs.
     * @param s The string to have its spaces replaced by tabs.
     * @return The string where all the spaces have been replaced by tabs.
     */
    public static String replaceSpacesToTabs(String s) {
        ArgumentCheck.checkNotNullOrEmpty(s, "String s");
        return s.replaceAll(" ", "\t");
    }
}
