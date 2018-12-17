package dk.kb.metadata.selector;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import dk.kb.ginnungagap.archive.WarcInfoConstants;
import dk.kb.metadata.utils.StringUtils;

/**
 * Utility class for dealing with Premis specific methods to be used in the XSLT.
 */
public class PremisUtils {

    /**
     * Retrieves the GUID for the latest historic preservation of the record.
     * Generally just parses the values from the field: "Bevarings metadata historik", 
     * and delivers the GUID from the last line. 
     * @param nodeIterator The note iterator.
     * @return The GUID value of the last line in the field.
     */
    public static String getLatestHistoricGuid(NodeIterator nodeIterator) {
        Node lastNode = nodeIterator.nextNode();
        Node next;
        while((next = nodeIterator.nextNode()) != null) {
            lastNode = next;
        }
        String line = lastNode.getFirstChild().getNodeValue();
        
        return StringUtils.split(line, " ", 0);
    }
    
    /**
     * Retrieves the system environment and properties.
     * @return the system environment and properties.
     */
    public static String getEnvironmentAndProperties() {
        StringBuffer res = new StringBuffer();
        for(String key : WarcInfoConstants.SYSTEM_PROPERTIES) {
            String value = System.getProperty((String) key);
            if(value != null && !value.isEmpty()) {
                res.append(key + ": " + value + "\n");
            }
        }
        res.append("\n");
        for(String key : WarcInfoConstants.ENV_VARIABLES) {
            String value = System.getenv().get(key);
            if(value != null && !value.isEmpty()) {
                res.append(key + ": " + value + "\n");
            }
        }

        return res.toString();
    }
}
