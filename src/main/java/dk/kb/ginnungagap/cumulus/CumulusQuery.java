package dk.kb.ginnungagap.cumulus;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.StringUtils;

public class CumulusQuery {

    /**
     * 
     * @param catalogName
     * @return
     */
    public static String getQueryForReadyToArchive(String catalogName) {
        ArgumentCheck.checkNotNullOrEmpty(catalogName, "String catalogName");
        return String.format(
                StringUtils.replaceSpacesToTabs("%s is %s\nand %s is %s"),
                Constants.FieldNames.PRESERVATION_STATUS,
                Constants.FieldValues.
                PRESERVATIONSTATE_READY_FOR_ARCHIVAL,
                Constants.FieldNames.PRODUCTION_CATALOG,
                catalogName
                );
    }
}
