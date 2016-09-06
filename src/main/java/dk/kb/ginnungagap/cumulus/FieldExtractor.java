package dk.kb.ginnungagap.cumulus;

import java.util.HashMap;
import java.util.Map;

import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.FieldTypes;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;

/**
 * Class for extracting the values of all the fields of an item according to the layout.
 * 
 * TODO: make logging, etc.
 */
public class FieldExtractor {
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
     * @return The mapping between field names and field values for the item.
     */
    public Map<String, String> getFieldsAsStrings(Item item) {
        Map<String, String> res = new HashMap<String, String>();
        for(FieldDefinition fd : layout) {
            String value = getFieldValue(fd, item);
            if(value == null || value.isEmpty()) {
                // ignore
            } else {
                res.put(fd.getName(), value);
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
    protected String getFieldValue(FieldDefinition fd, Item item) {
        if(!item.hasValue(fd.getFieldUID())) {
            return null;
        }

        switch(fd.getFieldType()) {
        case FieldTypes.FieldTypeBool:
            return "" + item.getBooleanValue(fd.getFieldUID());
        case FieldTypes.FieldTypeDate:
            // TOOD: figure out about how to format the date.
            return item.getDateValue(fd.getFieldUID()).toString();
        case FieldTypes.FieldTypeDouble:
            return "" + item.getDoubleValue(fd.getFieldUID());
        case FieldTypes.FieldTypeEnum:
            return item.getStringEnumValue(fd.getFieldUID()).getDisplayString();
        case FieldTypes.FieldTypeInteger:
            return "" + item.getIntValue(fd.getFieldUID());
        case FieldTypes.FieldTypeLong:
            return "" + item.getLongValue(fd.getFieldUID());
        case FieldTypes.FieldTypeString:
            return item.getStringValue(fd.getFieldUID());
        case FieldTypes.FieldTypeAudio:
        case FieldTypes.FieldTypeBinary:
        case FieldTypes.FieldTypePicture:
        case FieldTypes.FieldTypeTable:
            // Ignore these formats.
            return null;
        }
        // could not figure out.
        return null;
    }
}
