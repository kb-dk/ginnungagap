package dk.kb.ginnungagap.cumulus;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.FieldTypes;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;

import dk.kb.ginnungagap.cumulus.field.Field;
import dk.kb.ginnungagap.cumulus.field.StringField;
import dk.kb.ginnungagap.cumulus.field.TableField;

/**
 * Class for extracting the values of all the fields of an item according to the layout.
 * 
 * TODO: make logging, etc.
 */
public class FieldExtractor {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(FieldExtractor.class);

    /** The layout for this extractor.*/
    protected final Layout layout;

    /**
     * Constructor.
     * @param layout The field-layout for the extractor.
     */
    public FieldExtractor(Layout layout) {
        this.layout = layout;
    }

    /**
     * Extracts all the fields of the item according to the layout, and returns them as a mapping between
     * the name of the field and the value (in string format).
     * @param item The item to extract all fields for.
     * @return The collection of fields for the item. Fields with no value are ignored.
     */
    public Map<String, Field> getFields(Item item) {
        Map<String, Field> res = new HashMap<String, Field>();
        for(FieldDefinition fd : layout) {
            Field f = getFieldValue(fd, item);
            if(f != null) {
                res.put(f.getName(), f);
            }
        }
        return res;
    }

    /**
     * Extracts all the fields of the item according to the layout, and returns them as a mapping between
     * the name of the field and the value (in string format).
     * @param item The item to extract all fields for.
     * @return The collection of fields for the item. Fields with no value are ignored.
     */
    public Map<String, String> getMap(Item item) {
        Map<String, String> res = new HashMap<String, String>();
        for(FieldDefinition fd : layout) {
            StringField f = (StringField) getFieldValue(fd, item);
            if(f != null) {
                res.put(f.getName(), f.getStringValue());
            }
        }
        return res;
    }
    
    /**
     * Extracts the value of a specific field from the given item.
     * @param fd The definition of the field.
     * @param item The item to have its field value extracted.
     * @return The string value of the field. If the field is not natively string, then it is
     * converted into a string.
     */
    protected Field getFieldValue(FieldDefinition fd, Item item) {
        if(!item.hasValue(fd.getFieldUID())) {
            log.trace("No element at uid " + fd.getFieldUID());
            return null;
        }

        switch(fd.getFieldType()) {
        case FieldTypes.FieldTypeBool:
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getBooleanValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeDate:
            // TOOD: figure out about how to format the date.
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    item.getDateValue(fd.getFieldUID()).toString());
        case FieldTypes.FieldTypeDouble:
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getDoubleValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeEnum:
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    item.getStringEnumValue(fd.getFieldUID()).getDisplayString());
        case FieldTypes.FieldTypeInteger:
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getIntValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeLong:
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getLongValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeString:
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), 
                    item.getStringValue(fd.getFieldUID()));
        case FieldTypes.FieldTypeBinary:
            log.info("Currently does not handle field value for type" + getFieldTypeName(fd.getFieldType())
            + ", an empty string returned.");
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), "");
        case FieldTypes.FieldTypeAudio:
            log.info("Currently does not handle field value for type" + getFieldTypeName(fd.getFieldType())
                    + ", an empty string returned.");
            return new StringField(fd, getFieldTypeName(fd.getFieldType()), "");
        case FieldTypes.FieldTypePicture:
            log.info("Currently does not handle field value for type" + getFieldTypeName(fd.getFieldType())
                    + ", an empty string returned.");
            return new StringField(fd, getFieldTypeName(fd.getFieldType()),  "");
        case FieldTypes.FieldTypeTable:
            return new TableField(fd, getFieldTypeName(fd.getFieldType()),  item.getTableValue(fd.getFieldUID()));
        }

        log.warn("Unhandled field type: " + getFieldTypeName(fd.getFieldType()));
        return null;
    }

    /**
     * Retrieves the name of a given field type.
     * @param fieldType The field type ordinal.
     * @return the name of the field type.
     */
    protected String getFieldTypeName(int fieldType) {
        switch(fieldType) {
        case FieldTypes.FieldTypeBool:
            return "boolean";
        case FieldTypes.FieldTypeDate:
            return "date";
        case FieldTypes.FieldTypeDouble:
            return "double";
        case FieldTypes.FieldTypeEnum:
            return "enumerator";
        case FieldTypes.FieldTypeInteger:
            return "integer";
        case FieldTypes.FieldTypeLong:
            return "long";
        case FieldTypes.FieldTypeString:
            return "string";
        case FieldTypes.FieldTypeBinary:
            return "binary";
        case FieldTypes.FieldTypeAudio:
            return "audio";
        case FieldTypes.FieldTypePicture:
            return "picture";
        case FieldTypes.FieldTypeTable:
            return "table";
        }

        // Should we throw an error/exception here?
        return "NOT DEFINED!!!";
    }
    
    /**
     * Extracts the GUID for the field with the given name.
     * NOTE: If there is multiple fields with the name (ignore case), only the first found is returned.
     * 
     * @param fieldName The name of the field, whose GUID should be extracted.
     * @return The GUID, or null if not found.
     */
    public GUID getFieldGUID(String fieldName) {
        for(FieldDefinition fd : layout) {
            if(fd.getName().equalsIgnoreCase(fieldName)) {
                return fd.getFieldUID();
            }
        }
        log.warn("Could not find field: " + fieldName);
        return null;
    }
}
