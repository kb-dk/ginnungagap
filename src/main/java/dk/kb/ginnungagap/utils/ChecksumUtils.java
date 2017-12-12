package dk.kb.ginnungagap.utils;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;
import org.bitrepository.bitrepositoryelements.ChecksumType;
import org.bitrepository.client.eventhandler.OperationEvent.OperationEventType;
import org.bitrepository.common.utils.Base16Utils;
import org.jwat.warc.WarcDigest;

import dk.kb.ginnungagap.exception.ArgumentCheck;
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
        ArgumentCheck.checkExistsNormalFile(file, "File file");
        try {
            Digest digestor = new Digest(algorithm);
            return digestor.getDigestOfFile(file);
        } catch (YggdrasilException e) {
            throw new IllegalStateException("Could not calculate the checksum of the file '" 
                    + file.getAbsolutePath() + "'", e);
        }
    }

    /**
     * Validates that a collection of checksum complete pillar events have the same checksum for the same file. 
     * It will throw an exception if no results are found.
     * @param checksumEvents The checksum complete pillar events to validate. 
     * @return The checksum which is agreed upon.
     */
    public static String getAgreedChecksum(Collection<ChecksumsCompletePillarEvent> checksumEvents) {
        Set<String> filenames = new HashSet<>();
        Set<ChecksumType> csType = new HashSet<>();
        Set<String> checksums = new HashSet<>();
        for(ChecksumsCompletePillarEvent event : checksumEvents) {
            if(event.getEventType() == OperationEventType.COMPONENT_COMPLETE) {
                filenames.add(event.getFileID());
                filenames.add(event.getChecksums().getChecksumDataItems().get(0).getFileID());
                csType.add(event.getChecksumType().getChecksumType());
                checksums.add(Base16Utils.decodeBase16(
                        event.getChecksums().getChecksumDataItems().get(0).getChecksumValue()));
            } else {
                throw new IllegalStateException("A component failed to deliver checksum results, "
                        + "thus not valid result: " + event);
            }
        }

        if(checksums.isEmpty()) {
            throw new IllegalStateException("No results -> No WARC file found.");
        }
        if(filenames.size() > 1 || csType.size() > 1 || checksums.size() > 1) {
            throw new IllegalStateException("Too many results; filenames: '" + filenames + "', checksumTypes: '"
                    + csType + "', checksums: '" + checksums + "'");
        }
        return checksums.iterator().next();
    }
}
