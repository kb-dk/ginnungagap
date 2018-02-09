package dk.kb.ginnungagap.archive;

import java.io.File;
import java.util.Map;

import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.yggdrasil.bitmag.Bitrepository;
import dk.kb.yggdrasil.bitmag.BitrepositoryConfig;
import dk.kb.yggdrasil.exceptions.YggdrasilException;

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
        try {
            BitrepositoryConfig bitmagConf = new BitrepositoryConfig(conf.getSettingsDir(), conf.getPrivateKeyFile(),
                    conf.getMaxNumberOfFailingPillars(), conf.getComponentId());
            bitrepository = new Bitrepository(bitmagConf);
        } catch(RuntimeException e) {
            throw new ArgumentCheck("Could not instantiate Bitrepository connection with configuration: "
                    + conf, e);
        }
    }

    @Override
    public boolean uploadFile(File file, String collectionId) {
        log.debug("Uploading file '" + file.getAbsolutePath() + "' to bitrepository collection '"
                + collectionId + "'.");
        boolean success = bitrepository.uploadFile(file, collectionId);
        if(success) {
            log.debug("Deleting file '" + file.getName() + "' after success upload to the bitrepository.");
            FileUtils.deleteFile(file);
        }
        return success;
    }

    @Override
    public void shutdown() {
        log.debug("Shutting down the bitrepository client and access to the messagebus.");
        bitrepository.shutdown();
    }

    @Override
    public File getFile(String warcId, String collectionId) {
        try {
            return bitrepository.getFile(warcId, collectionId, null);
        } catch(YggdrasilException e) {
            throw new IllegalStateException("Could not retrieve the file '" + warcId + "' from collection '"
                    + collectionId + "'.", e);
        }
    }

    @Override
    public String getChecksum(String warcId, String collectionId) {
        Map<String, ChecksumsCompletePillarEvent> completeEvents = bitrepository.getChecksums(warcId, collectionId);
        return ChecksumUtils.getAgreedChecksum(completeEvents.values());
    }
}
