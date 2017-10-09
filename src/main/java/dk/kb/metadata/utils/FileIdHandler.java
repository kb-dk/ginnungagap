package dk.kb.metadata.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of the file ids for the respective GUIDs.
 */
public final class FileIdHandler {
    /** Constructor for this Utility class.*/
    protected FileIdHandler() {}

    /** Maps between a GUID and the respective file id. */
    private static final Map<String, String> FILE_IDS = new HashMap<String, String>();

    /**
     * Returns the file id for the respective GUID. If no file id exists for such GUID, then it is created.
     * @param guid The GUID of the file.
     * @return The file id corresponding to the GUID.
     */
    public static String getFileID(String guid) {
        String fileId = FILE_IDS.get(guid);
        if (fileId == null) {
            fileId = "fileId" + (FILE_IDS.size() + 1);
            FILE_IDS.put(guid, fileId);
        }
        return FILE_IDS.get(guid);
    }

    /**
     * @return The map of file-ids.
     */
    public static Map<String, String> getMap() {
        return FILE_IDS;
    }
    
    /**
     * Cleanup data after use (should be called after each transformation).
     */
    public static void clean() {
        FILE_IDS.clear();
    }
}
