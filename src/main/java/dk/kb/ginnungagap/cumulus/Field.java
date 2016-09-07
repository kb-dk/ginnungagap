package dk.kb.ginnungagap.cumulus;

/**
 * Container for Cumulus field.
 */
public class Field {
    /** The name of the field.*/
    protected final String name;
    
    /** The type of field.*/
    protected final String type;
    
    /** The string value. This is only used, if the value of the field can be converted into a String. 
     * Otherwise the byteValue is used.*/
    protected String stringValue = null;
    
    /** The byte value. This is only used, if the value of the field cannot be converted into a string.*/
    protected byte[] byteValue = null;
    
    /**
     * Constructor, for a string value.
     * @param name The name of the field.
     * @param fieldType The type of field.
     * @param value The string value of the field.
     */
    public Field(String name, String fieldType, String value) {
        this.name = name;
        this.stringValue = value;
        this.type = fieldType; 
    }
    
    /**
     * Constructor, for a byte array value.
     * @param name The name of the field.
     * @param fieldType The type of field.
     * @param byteArray The byte value of the field.
     */
    public Field(String name, String fieldType, byte[] byteArray) {
        this.name = name;
        this.byteValue = byteArray;
        this.type = fieldType; 
    }
    
    /** @return The name of the field.*/
    public String getName() {
        return name;
    }
    
    /** @return The type of field.*/
    public String getType() {
        return type;
    }
    
    /**
     * @return Whether or not the field has a string value.
     */
    public boolean hasStringValue() {
        return stringValue != null;
    }
    
    /**
     * @return The string value.
     */
    public String getStringValue() {
        return stringValue;
    }
    
    /**
     * @return Whether or not the field has a byte array value.
     */
    public boolean hasByteValue() {
        return byteValue != null;
    }
    
    /**
     * @return The byte array value.
     */
    public byte[] getByteValue() {
        return byteValue;
    }
}
