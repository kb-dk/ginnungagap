package dk.kb.ginnungagap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Utility class for dealing with files.
 */
public class FileUtils {
    /** The number of milliseconds per second. */
    protected static final long MILLIS_PER_SECOND = 1000L;
    /** The deprecation suffix.*/
    protected static final String DEPRECATION_SUFFIX = ".old";
    
    /**
     * Retrieves the directory at the given path.
     * If the directory does not exist yet, then it is created.
     * An exception will be thrown, if it is not possible to create the directory.
     * @param path The path to the directory.
     * @return The directory.
     */
    public static File getDirectory(String path) {
        File res = new File(path);
        if(!res.isDirectory() && !res.mkdirs()) {
            throw new IllegalStateException("Cannot instantiate the directory at '" + path + "'.");
        }
        return res;
    }
    
    /**
     * Method for moving a file from one position to another.
     * It will override the destination file, if it already exists.
     * @param from The file to move from.
     * @param to The file to move to.
     */
    public static void forceMove(File from, File to) {
        ArgumentCheck.checkExistsNormalFile(from, "File from");
        ArgumentCheck.checkNotNull(to, "File to");
        
        long moveDate = from.lastModified();
        
        try {
            StreamUtils.copyInputStreamToOutputStream(new FileInputStream(from), new FileOutputStream(to));
            deleteFile(from);
        } catch (IOException e) {
            throw new IllegalStateException("Could not move the file '" + from.getAbsolutePath() 
                    + "' to the location '" + to.getAbsolutePath() + "'", e);
        }
        
        if(to.lastModified() + MILLIS_PER_SECOND < moveDate) {
            throw new IllegalStateException("Moved file is older than time for moving (" + to.lastModified() 
                    + " < " + moveDate + ")");
        }
    }
    
    /**
     * Moves a file 'from' to the given destination file 'to'.
     * If 'to' already exists, then it is deprecated.
     * @param from The file to be moved.
     * @param to The destination of the file.
     */
    public static void deprecateMove(File from, File to) {
        ArgumentCheck.checkExistsNormalFile(from, "File from");
        ArgumentCheck.checkNotNull(to, "File to");
        
        if(to.exists()) {
            deprecateFile(to);
        }
        forceMove(from, to);
    }
    
    /**
     * Deprecates a file, if it exists.
     * It will move the file to a copy of the path suffixed by '.old'.
     * @param f The file to deprecate.
     */
    public static void deprecateFileIfExists(File f) {
        if(f.exists()) {
            deprecateFile(f);
        }
    }
    
    /**
     * Deprecate a file. This is done by adding the suffix '.old' to the filename.
     * Also, if another file exists on the deprecation position, then that file is also deprecated. 
     * @param f The file to deprecate.
     */
    public static void deprecateFile(File f) {
        ArgumentCheck.checkExistsNormalFile(f, "File f");        
        File newFile = new File(f.getAbsolutePath() + DEPRECATION_SUFFIX);
        if(newFile.exists()) {
            deprecateFile(newFile);
        }
        try {
            Files.move(f.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Could not deprecate the file '" + f.getAbsolutePath() + "'.", e);
        }
    }
    
    /**
     * Delete method, which validates that the file is actually not present afterwards.
     * @param f The file to delete.
     */
    public static void deleteFile(File f) {
        if(!f.exists()) {
            return;
        }
        boolean success = f.delete();
        if(!success) {
            throw new IllegalStateException("Could not delete the file '" + f.getAbsolutePath() + "'");
        }
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
    public static File createNewFile(File dir, String name) {
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

    /**
     * Create a file containing the given text
     * @param pathToFile path to the file instance
     * @param fileContent What is put into the file
     * @return the file with error text
     */
    public static File createFileWithText(String pathToFile, String fileContent) {
        File file = new File(pathToFile);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(pathToFile);
            fw.write(fileContent);
            fw.close();
        } catch (IOException e) {
            throw new IllegalStateException("Creating an error file failed", e);
        }
        return file;
    }

    /**
     * Delete files with specified extension
     * @param folder the folder from which the files should be deleted
     * @param extension the extension of the files to be deleted
     */
    public static void deleteFilesInFolder(File folder, String extension) {
        try {
            Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .filter(f -> f.getName().endsWith(extension))
                    .forEach(File::delete);
        } catch (Exception e) {
            throw new IllegalStateException("Temp file could not be deleted", e);
        }
    }

    /**
     * Deletes directory recursively
     * @param directoryToBeDeleted
     */
    public static void deleteDir(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            try {
                for (File file : allContents) {
                    Files.delete(file.toPath());
                }
                Files.delete(directoryToBeDeleted.toPath());
            } catch (NoSuchFileException e) {
                throw new IllegalStateException("Failed to delete file", e);
            } catch (DirectoryNotEmptyException e) {
                throw new IllegalStateException("Failed to delete empty dir", e);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete dir", e);
            }
        }
    }
}
