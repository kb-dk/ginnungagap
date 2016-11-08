package dk.kb.ginnungagap.archive;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.yggdrasil.bitmag.Bitrepository;
import dk.kb.yggdrasil.bitmag.BitrepositoryConfig;

/**
 * Bitrepository archive.
 */
public class BitmagArchive implements Archive {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(BitmagArchive.class);
    
    /** The bitrepository client from Yggdrasil.*/
    protected Bitrepository bitrepository;
    
    /**
     * Constructor.
     * @param conf The configuration for the bitrepository.
     */
    public BitmagArchive(BitmagConfiguration conf) {
        BitrepositoryConfig bitmagConf = new BitrepositoryConfig(conf.getSettingsDir(), conf.getPrivateKeyFile(),
                conf.getMaxNumberOfFailingPillars(), conf.getComponentId());
        bitrepository = new Bitrepository(bitmagConf);
    }

    @Override
    public boolean uploadFile(File file, String collectionId) {
        log.debug("Uploading file '" + file.getAbsolutePath() + "' to bitrepository collection '"
                + collectionId + "'.");
        boolean success = bitrepository.uploadFile(file, collectionId);
        if(success) {
            log.debug("Deleting file '" + file.getName() + "' after success upload to the bitrepository.");
            file.delete();
        }
        return success;
    }
}
