package dk.kb.metadata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling a map, and keeping its order.
 */
public class OrderedMap {
    /** The content of the map.*/
    protected final Map<String, String> content;
    /** The order of the content.*/
    protected final List<String> order;
    
    /**
     * Constructor.
     */
    public OrderedMap() {
        content = new HashMap<String, String>();
        order = new ArrayList<String>();
    }

    /**
     * Put an element into the map.
     * @param key The key for the map, and also the order token.
     * @param value The value of the map.
     */
    public void put(String key, String value) {
        order.add(key);
        content.put(key, value);
    }
    
    /**
     * Checks whether the map and order list has the key.
     * @param key The key.
     * @return Whether or not it has the key.
     */
    public boolean hasKey(String key) {
        return content.containsKey(key) && order.contains(key);
    }
    
    /**
     * Retrieves the value for a key.
     * @param key The key.
     * @return The value associated with the key.
     */
    public String getValue(String key) {
        return content.get(key);
    }
    
    /**
     * The ordering of a given key.
     * @param key The key.
     * @return The order of the key.
     */
    public Integer getIndex(String key) {
        return order.indexOf(key);
    }
}
