package dk.kb.ginnungagap.emagasin.importation;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;

/**
 * Utility class for deciding whether or not to import the file to a given Cumulus record.
 */
public class ImportDecider {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(ImportDecider.class);

    /** The TIFF format.*/
    protected static final String FORMAT_TIFF = "TIFF";
    
    /**
     * Tests whether the import prerequisites are met.
     * If it is a TIFF file, or it is missing, then it will be imported.
     * @param record The Cumlus record.
     * @return Whether it has the right format.
     */
    public static boolean shouldImportRecord(CumulusRecord record) {
        String format = record.getFieldValue(Constants.FieldNames.FILE_FORMAT);
        if(format.contains(FORMAT_TIFF)) {
            log.debug("Importing TIFF file.");
            return true;
        }
        String filePath = record.getFieldValueForNonStringField(Constants.FieldNames.ASSET_REFERENCE);
        File currentFile = new File(filePath);
        if(!currentFile.exists()) {
            log.debug("Importing Missing file.");
            return true;
        }
        long length = record.getFieldLongValue(Constants.PreservationFieldNames.FILE_DATA_SIZE);
        if(currentFile.length() != length) {
            log.debug("Importing file due to wrong size.");
            return true;
        }
        log.debug("Not importing the file.");
        return false;
    }
}
