package dk.kb.ginnungagap.emagasin.importation;

import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;

/**
 * Utility class for deciding whether or not to import the file to a given Cumulus record.
 */
public class ImportDecider {

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
            return true;
        }
        if(!record.getFile().exists()) {
            return true;
        }
        return false;
    }
}
