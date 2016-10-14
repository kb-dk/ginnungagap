package dk.kb.ginnungagap.utils;

import java.io.File;

/**
 * Utility class for dealing with files.
 */
public class FileUtils {

    /**
     * Retrieves the directory at the given path.
     * If the directory does not exist yet, then it is created.
     * An exception will be thrown, if it is not possible to create the directory.
     * @param path The path to the directory.
     * @return The directory.
     */
    public static File getDirectory(String path) {
        File res = new File(path);
        if(!res.isDirectory()) {
            res.mkdirs();
            if(!res.isDirectory()) {
                throw new IllegalStateException("Cannot instantiate the directory at '" + path + "'.");
            }
        }
        return res;
    }
}
