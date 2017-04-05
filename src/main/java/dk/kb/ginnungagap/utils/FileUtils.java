package dk.kb.ginnungagap.utils;

import java.io.File;
import java.io.IOException;

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
    
    /**
     * Retrieves the given subdirectory of a given directory.
     * @param dir The parent directory.
     * @param path The name of the directory
     * @return The directory.
     */
    public static File getDirectory(File dir, String path) {
        return getDirectory(new File(dir, path).getAbsolutePath());
    }
    
    /**
     * Creates a new file. Will throw an exception, if the file already exists, or if it cannot be instantiated.
     * @param dir The directory of the new file.
     * @param name The name of the new file.
     * @return The new file.
     */
    public static File getNewFile(File dir, String name) {
        File res = new File(dir, name);
        try {
            if(!res.createNewFile()) {
                throw new IllegalStateException("Cannot create a new file at '" + res.getAbsolutePath() + "'");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Issue occured while instantiation new file at '" + res.getAbsolutePath() 
                + "'", e);
        }
        return res;
    }
}
