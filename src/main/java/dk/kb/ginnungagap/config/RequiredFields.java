package dk.kb.ginnungagap.config;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.exception.YamlException;
import dk.kb.ginnungagap.utils.YamlTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The fields required for a Cumulus record to be preserved.<br/>
 * Currently supports two types of fields:
 * <ul>
 *   <li>Fields with must be present and having content</li>
 *   <li>Field which must exist and writable (though not required to contain.</li>
 * </ul>
 *
 * The required fields file must be structured in the following way:
 * <ul>
 *   <li>required_fields</li>
 *   <ul>
 *     <li>base</li>
 *     <ul>
 *       <li>- field 1</li>
 *       <li>- field 2</li>
 *       <li>...</li>
 *     </ul>
 *     <li>writable</li>
 *     <ul>
 *       <li>- field 3</li>
 *       <li>- field 4</li>
 *       <li>...</li>
 *     </ul>
 *   </ul>
 * </ul>
 */
public class RequiredFields {
    /** The list of base required fields.*/
    private final List<String> baseFields;
    /** The list of required fields, which must also be writable.*/
    private final List<String> writableFields;

    /** The root element in the Require Fields File.*/
    private static final String RFF_ROOT = "required_fields";
    /** The Base element in the Require Fields File.*/
    private static final String RFF_BASE = "base";
    /** The Base element in the Require Fields File.*/
    private static final String RFF_WRITABLE = "writable";

    /**
     * Constructor.
     * @param baseFields The list of required fields.
     * @param writableFields The list of required fields, which must also be writable.
     */
    public RequiredFields(Collection<String> baseFields, Collection<String> writableFields) {
        this.baseFields = new ArrayList<String>(baseFields);
        this.writableFields = new ArrayList<String>(writableFields);
    }

    /** @return The list of required fields. */
    public List<String> getBaseFields() {
        return baseFields;
    }

    /** @return The list of required fields, which must also be writable. */
    public List<String> getWritableFields() {
        return writableFields;
    }

    /**
     * Creates a RequiredFields from a file.
     * @param requiredFieldsFile The YAML file with the required fields in the format description atop the class.
     * @return The RequiredFields instantiated based on the file.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static RequiredFields loadRequiredFieldsFile(File requiredFieldsFile) {
        ArgumentCheck.checkExistsNormalFile(requiredFieldsFile, "File requiredFieldsFile");
        LinkedHashMap<String, LinkedHashMap> map = null;
        try {
            map = YamlTools.loadYamlSettings(requiredFieldsFile);
            ArgumentCheck.checkTrue(map.containsKey(RFF_ROOT), "Reqiuired Fields File '"
                    + requiredFieldsFile.getAbsolutePath() + "' must contain the element '" + RFF_ROOT + "'.");
            LinkedHashMap<String, Object> rootMap = map.get(RFF_ROOT);

            ArgumentCheck.checkTrue(rootMap.containsKey(RFF_BASE), "Reqiuired Fields File '"
                    + requiredFieldsFile.getAbsolutePath() + "' must contain the element '" + RFF_BASE + "'.");
            ArgumentCheck.checkTrue(rootMap.containsKey(RFF_WRITABLE), "Reqiuired Fields File '"
                    + requiredFieldsFile.getAbsolutePath() + "' must contain the element '" + RFF_WRITABLE+ "'.");

            List<String> baseFields = (List<String>) rootMap.get(RFF_BASE);
            List<String> wriableFields = (List<String>) rootMap.get(RFF_WRITABLE);

            return new RequiredFields(baseFields, wriableFields);
        } catch (YamlException e) {
            throw new ArgumentCheck("The YAML file '" + requiredFieldsFile.getAbsolutePath()
                    + "' could not be properly read.", e);
        }
    }
}
