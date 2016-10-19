package dk.kb.ginnungagap.archive;

import java.io.File;

/**
 * Interface for the archive possibilities for Ginnungagap.
 * Currently either a local archive (which is thus not properly preserved), 
 * or a bitrepository-based archive for proper preservation.
 */
public interface Archive {
    /**
     * Uploads the file to the archive.
     * It is expected, that the file is removed by this process (if it is successful).
     * 
     * @param file The file to upload.
     * @param collectionId The collection to upload the file to.
     * @return Whether or not it was successful.
     */
    boolean uploadFile(File file, String collectionId);
}
