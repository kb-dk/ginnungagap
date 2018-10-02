package dk.kb.ginnungagap.utils;

/**
 * Utility class for the methods regarding boolean objects.
 */
public class BooleanUtils {
    
    /**
     * Retrieves the boolean value from an unidentified type of object.
     * @param b The unidentified type of object.
     * @return The boolean value.
     */
    public static Boolean extractBoolean(Object b) {
        if(b instanceof String) {
            return Boolean.parseBoolean((String) b);
        }
        return (Boolean) b;
    }
}
