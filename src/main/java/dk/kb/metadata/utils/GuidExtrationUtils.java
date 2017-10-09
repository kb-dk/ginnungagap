package dk.kb.metadata.utils;

/**
 * Handles the extraction of GUIDs from the potentially old and invalid format.
 */
public final class GuidExtrationUtils {
    /** Constructor for this Utility class.*/
    protected GuidExtrationUtils() {}

    /**
     * Method for extracting the part of the KB-GUID which is valid as a 'xs:ID' standardized guid.
     * 
     * @param guid The GUID for the system.
     * @return The extracted GUID.
     */
    public static String extractGuid(String guid) {
        if(guid == null || guid.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("A GUID must be defined.");
            ExceptionUtils.insertException(e);
            throw e;
        }

        String res;
        if(guid.contains("/")) {
            String[] guidParts = guid.split("[/]");
            res = guidParts[guidParts.length-1];
        } else {
            res = guid;
        }
        
        if(res.contains("#")) {
            String[] guidParts = res.split("[#]");
            res = guidParts[0];            
        }
        
        return res;
    }
}
