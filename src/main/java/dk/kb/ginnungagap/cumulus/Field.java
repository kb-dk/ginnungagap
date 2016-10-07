package dk.kb.ginnungagap.cumulus;

import com.canto.cumulus.FieldDefinition;

/**
 * Container for Cumulus field.
 */
public abstract class Field {
    /** The name of the field.*/
    protected final FieldDefinition definition;
    
    /** The type of field.*/
    protected final String type;
    
    /**
     * Constructor, for a string value.
     * @param name The name of the field.
     * @param fieldType The type of field.
     */
    public Field(FieldDefinition fd, String fieldType) {
        this.definition = fd;
        this.type = fieldType; 
    }
    
    /** @return The name of the field.*/
    public String getName() {
        return definition.getName();
    }
    
    /** @return The type of field.*/
    public String getType() {
        return type;
    }
    
    /**
     * @return Whether or not the field is editable.
     */
    public boolean isFieldEditable() {
        return definition.isEditable();
    }
    
    /**
     * @return Whether or not it contains any actual value (spaces are ignored).
     */
    public abstract boolean isEmpty();
}
