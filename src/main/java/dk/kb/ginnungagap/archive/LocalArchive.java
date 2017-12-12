package dk.kb.ginnungagap.archive;

import java.io.File;

import org.jwat.warc.WarcDigest;

import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.FileUtils;

/**
 * A local archive, which just places the files in a local 'archive' folder with subfolder for each collection.
 */
public class LocalArchive implements Archive {
    /** The base archiving directory. Get sub-directory for each collection.*/
    protected final File archiveBaseDir;
    
    /** The default path for the local archive.*/
    public static final String DEFAULT_PATH = "archive";
    
    /**
     * Constructor.
     * @param path The path to the directory.
     */
    public LocalArchive(String path) {
        this.archiveBaseDir = FileUtils.getDirectory(path);
    }
    
    @Override
    public boolean uploadFile(File file, String collectionId) {
        File collectionDir = FileUtils.getDirectory(new File(archiveBaseDir, collectionId).getAbsolutePath());
        File toFile = new File(collectionDir, file.getName());
        
        return file.renameTo(toFile);
    }

    @Override
    public void shutdown() {
        // Do nothing!!!
    }

    @Override
    public File getFile(String warcId, String collectionId) {
        File collectionDir = FileUtils.getDirectory(new File(archiveBaseDir, collectionId).getAbsolutePath());
        return new File(collectionDir, warcId);
    }

    @Override
    public String getChecksum(String warcId, String collectionId) {
        WarcDigest digest = ChecksumUtils.calculateChecksum(getFile(warcId, collectionId), 
                ChecksumUtils.MD5_ALGORITHM);
        return digest.digestString;
    }
}
