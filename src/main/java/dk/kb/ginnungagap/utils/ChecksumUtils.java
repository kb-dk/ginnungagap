package dk.kb.ginnungagap.utils;

import java.io.File;

import org.jwat.warc.WarcDigest;

import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.warc.Digest;

/**
 * Utility class for dealing with checksums.
 */
public class ChecksumUtils {
    /** MD5 algorithm name.*/
    public static final String MD5_ALGORITHM = "MD5";
    
    /**
     * Calculates the checksum of a file with a given checksum.
     * @param file The file to calculate the checksum of.
     * @param algorithm The algorithm for the checksum calculation.
     * @return The checksum of the file wrapped in a WarcDigest.
     */
    public static WarcDigest calculateChecksum(File file, String algorithm) {
        try {
            Digest digestor = new Digest(algorithm);
            return digestor.getDigestOfFile(file);
        } catch (YggdrasilException e) {
            throw new IllegalStateException("Could not calculate the checksum of the file '" 
                    + file.getAbsolutePath() + "'", e);
        }

    }
}
