package dk.kb.ginnungagap.archive;

import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.yggdrasil.bitmag.Bitrepository;
import dk.kb.yggdrasil.bitmag.BitrepositoryConfig;

/**
 * Bitrepository archive.
 */
public class BitmagArchive implements Archive {
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
}
