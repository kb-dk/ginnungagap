package dk.kb.ginnungagap.utils;

import java.util.List;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Utility class for dealings with Strings.
 */
public class StringUtils {
    /** The result of an empty string.*/
    protected static final String EMPTY_LIST = "[]";
    
    /**
     * Replaces all spaces with tabs.
     * @param s The string to have its spaces replaced by tabs.
     * @return The string where all the spaces have been replaced by tabs.
     */
    public static String replaceSpacesToTabs(String s) {
        ArgumentCheck.checkNotNullOrEmpty(s, "String s");
        return s.replaceAll(" ", "\t");
    }
    
    /**
     * Method for changing a list of strings, into a single string.
     * @param list The list of string.
     * @param separator The separator between each string in the list.
     * @return The strings in the list combined to a single string, but separated by the separator.
     */
    public static String listToString(List<String> list, String separator) {
        if(list == null || list.isEmpty()) {
            return EMPTY_LIST;
        }
        StringBuilder res = new StringBuilder();
        for(String s : list) {
            res.append(s);
            res.append(separator);
        }
        return res.toString();
    }

}
