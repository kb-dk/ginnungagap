package dk.kb.ginnungagap.cumulus;

import java.util.EnumSet;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.constants.CombineMode;
import com.canto.cumulus.constants.FindFlag;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.StringUtils;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;

/**
 * Class for encapsulating the query for locating specific items in Cumulus.
 */
public class CumulusQuery {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(PreservationWorkflow.class);

    /** The query.*/
    protected final String query;
    /** The flags.*/
    protected final EnumSet<FindFlag> findFlags;
    /** The combine mode.*/
    protected final CombineMode combineMode;
    /** The locale. Defaults to null.*/
    protected Locale locale;

    /**
     * Constructor. 
     * Automatically sets the locale to null.
     * @param query The query.
     * @param findFlags The flags.
     * @param combineMode The combine mode.
     */
    public CumulusQuery(String query, EnumSet<FindFlag> findFlags, CombineMode combineMode) {
        ArgumentCheck.checkNotNullOrEmpty(query, "String query");
        ArgumentCheck.checkNotNullOrEmptyCollection(findFlags, "EnumSet<FindFlag> findFlags");
        ArgumentCheck.checkNotNull(combineMode, "CombineMode combineMode");
        this.query = query;
        this.findFlags = findFlags;
        this.combineMode = combineMode;
        this.locale = null;
        
        log.debug("Instantiated Cumulus query '" + query + "' with flags, '" + findFlags + "' and combine-mode: '" 
                + combineMode.name() + "'");
    }

    /** @return The query string. */
    public String getQuery() {
        return query;
    }

    /** @return The enum set of flags for the query. */
    public EnumSet<FindFlag> getFindFlags() {
        return findFlags;
    }

    /** @return The combine mode.*/
    public CombineMode getCombineMode() {
        return combineMode;
    }

    /** @return The locale. This can be null.*/
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale The new value for the locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * The default query for extracting preservation ready items from a given catalog.
     * @param catalogName The name of the catalog.
     * @return The Cumulus query.
     */
    public static CumulusQuery getPreservationQuery(String catalogName) {
        ArgumentCheck.checkNotNullOrEmpty(catalogName, "String catalogName");
        String query = String.format(
                StringUtils.replaceSpacesToTabs("%s is %s\nand %s is %s\nand %s is %s"),
                Constants.FieldNames.PRESERVATION_STATUS,
                Constants.FieldValues.
                PRESERVATIONSTATE_READY_FOR_ARCHIVAL,
                Constants.FieldNames.REGISTRATIONSTATE,
                Constants.FieldValues.REGISTRATIONSTATE_FINISHED,
                Constants.FieldNames.CATALOG_NAME,
                catalogName);
        EnumSet<FindFlag> findFlags = EnumSet.of(
                FindFlag.FIND_MISSING_FIELDS_ARE_ERROR, 
                FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);    

        return new CumulusQuery(query, findFlags, CombineMode.FIND_NEW);
    }
}
