package dk.kb.ginnungagap.emagasin.importation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Substitutes of part of the path for the Asset Reference.
 * It is due to some of the servers and paths previously defined and used when the data was preserved
 * are no longer available, that these paths must therefore be changed to the new servers and folder-structures.
 * 
 * The substitute part of the configuration from the import section:
 *   <li>substitute:<br/> -$sub1_name 
 *   <ul>
 *     <li>from: $from1</li>
 *     <li>to: $to1</li>
 *   </ul>
 *   - $sub2_name
 *   <ul>
 *     <li>from: $from2</li>
 *     <li>to: $to2</li>
 *   </ul>
 *   - ...
 *   </li> 
 */
public class PathSubstituteConfiguration {
    /** The substitute field 'from'.*/
    protected static final String SUBSTITUTE_ELEMENT_FROM = "from";
    /** The substitute field 'to'.*/
    protected static final String SUBSTITUTE_ELEMENT_TO = "to";
    
    /** The map between which part of path should be substituted, and what it should be substituted with.*/
    protected final Map<String, String> substitutes; 
    
    /**
     * Constructor.
     * @param config The configuration map.
     */
    public PathSubstituteConfiguration(List<Map<String, String>> config) {
        ArgumentCheck.checkNotNullOrEmpty(config, "List<Map<String, String>> config");
        substitutes = new HashMap<String, String>();
        for(Map<String, String> subMap : config) {
            if(!subMap.containsKey(SUBSTITUTE_ELEMENT_FROM) || !subMap.containsKey(SUBSTITUTE_ELEMENT_TO)) {
                throw new IllegalStateException("Invalid configuration for the substitution: " + subMap);
            }
            substitutes.put(subMap.get(SUBSTITUTE_ELEMENT_FROM), subMap.get(SUBSTITUTE_ELEMENT_TO));
        }
    }
    
    /**
     * Substitute the path, if it starts with any of the substitutions.
     * @param path The path to substitute.
     * @return The substituted path.
     */
    public String substitute(String path) {
        ArgumentCheck.checkNotNullOrEmpty(path, "String path");
        for(Map.Entry<String, String> sub : substitutes.entrySet()) {
            if(path.startsWith(sub.getKey())) {
                return path.replace(sub.getKey(), sub.getValue());
            }
        }
        return path;
    }
}
