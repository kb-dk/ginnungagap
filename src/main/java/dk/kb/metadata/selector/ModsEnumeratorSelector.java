package dk.kb.metadata.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains the different selectors for the MODS metadata, some will translate the value into the 
 * corresponding in the enumerator.
 * 
 * The different selectors will through 'IllegalStateException' if the given value 
 * cannot be found within their enumerator.
 */
public final class ModsEnumeratorSelector {
    /** Private constructor for this Utility class.*/
    protected ModsEnumeratorSelector() {}

    /** The type of resource for text.*/
    protected static final String TYPE_OF_RESOURCE_TEXT = "text";
    /** The type of resource for cartographic.*/
    protected static final String TYPE_OF_RESOURCE_CARTOGRAPHIC = "cartographic";
    /** The type of resource for notated music.*/
    protected static final String TYPE_OF_RESOURCE_NOTATED_MUSIC = "notated music";
    /** The type of resource for sound recording-musical.*/
    protected static final String TYPE_OF_RESOURCE_SOUND_RECORDING_MUSICAL = "sound recording-musical";
    /** The type of resource for sound recording-nonmusical.*/
    protected static final String TYPE_OF_RESOURCE_SOUND_RECORDING_NONMUSICAL = "sound recording-nonmusical";
    /** The type of resource for sound recording.*/
    protected static final String TYPE_OF_RESOURCE_SOUND_RECORDING = "sound recording";
    /** The type of resource for still image.*/
    protected static final String TYPE_OF_RESOURCE_STILL_IMAGE = "still image";
    /** The type of resource for moving image.*/
    protected static final String TYPE_OF_RESOURCE_MOVING_IMAGE = "moving image";
    /** The type of resource for three dimensional object.*/
    protected static final String TYPE_OF_RESOURCE_THREE_DIMENSIONAL_OBJECT = "three dimensional object";
    /** The type of resource for software, multimedia.*/
    protected static final String TYPE_OF_RESOURCE_SOFTWARE_MULTIMEDIA = "software, multimedia";
    /** The type of resource for mixed material.*/
    protected static final String TYPE_OF_RESOURCE_MIXED_MATERIAL = "mixed material";
    /** The type of resource for no value.*/
    protected static final String TYPE_OF_RESOURCE_NO_VALUE = "";
    
    /** The restrictions for the 'mods:typeOfResource'*/
    protected static final Set<String> TYPE_OF_RESOURCE_RESTRICTIONS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList(TYPE_OF_RESOURCE_TEXT, TYPE_OF_RESOURCE_CARTOGRAPHIC, 
                    TYPE_OF_RESOURCE_NOTATED_MUSIC, TYPE_OF_RESOURCE_SOUND_RECORDING_MUSICAL, 
                    TYPE_OF_RESOURCE_SOUND_RECORDING_NONMUSICAL, TYPE_OF_RESOURCE_SOUND_RECORDING, 
                    TYPE_OF_RESOURCE_STILL_IMAGE, TYPE_OF_RESOURCE_MOVING_IMAGE, 
                    TYPE_OF_RESOURCE_THREE_DIMENSIONAL_OBJECT, TYPE_OF_RESOURCE_SOFTWARE_MULTIMEDIA, 
                    TYPE_OF_RESOURCE_MIXED_MATERIAL, TYPE_OF_RESOURCE_NO_VALUE)));

    /**
     * A method for passing the 4 arguments from the XSLT MODS transformation of the typeOfResource.
     * They will be changed into a collection and passed into the other typeOfResource method,
     * where the values will be parsed, and the first valid value will be chosen.
     * @param value1 The value of the field Materialebetegnelse.
     * @param value2 The value of the field Resourcedescription
     * @param value3 The value of the field Generel materialebetegnelse
     * @param value4 The value of the field General Resourcedescription
     * @return The validated value.
     */
    public static String typeOfResource(String value1, String value2, String value3, String value4) {
        return typeOfResource(Arrays.asList(value1, value2, value3, value4));
    }
    
    /**
     * Selects a given valid entry for the field 'mods:typeOfResource'.
     * @param values The values. 
     * @return The evaluated value, or the empty value.
     */
    public static String typeOfResource(Collection<String> values) {
        for(String value : values) {
            if(value == null || value.isEmpty()) {
                continue;
            }

            if(TYPE_OF_RESOURCE_RESTRICTIONS.contains(value.toLowerCase())) {
                return value;
            }
            
            if(value.equalsIgnoreCase("LYD")) {
                return TYPE_OF_RESOURCE_SOUND_RECORDING;
            }
            if(value.equalsIgnoreCase("Todimentionelt billedmateriale")) {
                return TYPE_OF_RESOURCE_STILL_IMAGE;
            }
            if(value.equalsIgnoreCase("Billede, Todimentionelt billedmateriale")) {
                return TYPE_OF_RESOURCE_STILL_IMAGE;
            }
            if(value.equalsIgnoreCase("Kort, Todimentionelt billedmateriale")) {
                return TYPE_OF_RESOURCE_CARTOGRAPHIC;
            }
            if(value.equalsIgnoreCase("Musikalier, tryk")) {
                return TYPE_OF_RESOURCE_NOTATED_MUSIC;
            }
            if(value.equalsIgnoreCase("Musikalier, håndskrift")) {
                return TYPE_OF_RESOURCE_NOTATED_MUSIC;
            }
            if(value.equalsIgnoreCase("Smaatryk")) {
                return TYPE_OF_RESOURCE_STILL_IMAGE;
            }
        }

        return TYPE_OF_RESOURCE_NO_VALUE;
    }

    /** The collection of the restrictions values for the 'mods:relatedItem/@type'.*/
    protected static final Set<String> RELATED_ITEM_TYPE_RESTRICTION = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("preceding", "succeeding", "original", "host", "constituent", "series", 
                    "otherVersion", "otherFormat", "isReferencedBy", "references", "reviewOf")));

    /**
     * Retrieves the restricted value for the 'mode:relatedItem/@type'.
     * @param type The type to validate.
     * @param defaultValue The default value.
     * @return The evaluated value.
     */
    public static String relatedItemAttributeType(String type, String defaultValue) {
        if(type == null || type.isEmpty()) {
            return defaultValue;
        }

        if(RELATED_ITEM_TYPE_RESTRICTION.contains(type)) {
            return type;
        }

        return defaultValue;
    }
}
