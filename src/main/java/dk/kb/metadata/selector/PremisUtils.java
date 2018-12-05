package dk.kb.metadata.selector;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

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
}
