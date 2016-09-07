package dk.kb.ginnungagap.cumulus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger log = LoggerFactory.getLogger(FieldExtractor.class);

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
    public List<Field> getFields(Item item) {
        List<Field> res = new ArrayList<Field>();
        for(FieldDefinition fd : layout) {
            Field f = getFieldValue(fd, item);
            if(f != null ){
                res.add(f);
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
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getBooleanValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeDate:
            // TOOD: figure out about how to format the date.
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    item.getDateValue(fd.getFieldUID()).toString());
        case FieldTypes.FieldTypeDouble:
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getDoubleValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeEnum:
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    item.getStringEnumValue(fd.getFieldUID()).getDisplayString());
        case FieldTypes.FieldTypeInteger:
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getIntValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeLong:
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    String.valueOf(item.getLongValue(fd.getFieldUID())));
        case FieldTypes.FieldTypeString:
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    item.getStringValue(fd.getFieldUID()));
        case FieldTypes.FieldTypeBinary:
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), 
                    item.getBinaryValue(fd.getFieldUID()));
        case FieldTypes.FieldTypeAudio:
            log.info("Currently does not handle field value for type" + getFieldTypeName(fd.getFieldType())
                    + ", an empty string returned.");
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()), "");
        case FieldTypes.FieldTypePicture:
            log.info("Currently does not handle field value for type" + getFieldTypeName(fd.getFieldType())
                    + ", an empty string returned.");
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()),  "");
        case FieldTypes.FieldTypeTable:
            log.info("Currently does not handle field value for type" + getFieldTypeName(fd.getFieldType()) 
                    + ", an empty string returned.");
            return new Field(fd.getName(), getFieldTypeName(fd.getFieldType()),  "");
        }

        log.warn("Unhandled field type: " + getFieldTypeName(fd.getFieldType()));
        return null;
    }

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
}
