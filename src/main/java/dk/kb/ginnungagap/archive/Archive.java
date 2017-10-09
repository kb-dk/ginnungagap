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
    
    /**
     * Retrieve the file from the archive.
     * @param warcId The id of the WARC file to retrieve.
     * @param collectionId The collection, where the file must be located.
     * @return The file.
     */
    File getFile(String warcId, String collectionId);
    
    /**
     * Retrieve the checksum of a file in the archive.
     * @param warcId The id of a WARC file in the archive.
     * @param collectionId The collection with the file to have its checksum calculated.
     * @return The checksum of the file.
     */
    String getChecksum(String warcId, String collectionId);
    
    /**
     * Shutdown the archive, or any connections required for accessing the archive.
     */
    void shutdown();
}
