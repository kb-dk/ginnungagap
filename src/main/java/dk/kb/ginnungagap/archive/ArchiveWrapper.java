package dk.kb.ginnungagap.archive;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.ginnungagap.config.Configuration;

/**
 * Wrapped archive.
 */
@Component
public class ArchiveWrapper implements Archive {
    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The wrapped archive, depending on the type of archive.*/
    protected Archive archive;

    /**
     * Initialization
     */
    @PostConstruct
    protected void init() {
        if(conf.getLocalConfiguration().getIsTest()) {
            this.archive = new LocalArchive(conf.getLocalConfiguration().getLocalArchiveDir());
        } else {
            this.archive = new BitmagArchive(conf.getBitmagConf());
        }
    }
    
    @Override
    public boolean uploadFile(File file, String collectionId) {
        return archive.uploadFile(file, collectionId);
    }

    @Override
    public File getFile(String warcId, String collectionId) {
        return archive.getFile(warcId, collectionId);
    }

    @Override
    public String getChecksum(String warcId, String collectionId) {
        return archive.getChecksum(warcId, collectionId);
    }
    
    @Override
    public void close() {
        archive.close();
    }
}
