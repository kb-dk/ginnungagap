package dk.kb.metadata.utils;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderedMapTest extends ExtendedTestCase {

    String key = "KEY";
    String value = "value";
    
    @Test
    public void test() {
        OrderedMap om = new OrderedMap();
        
        Assert.assertEquals(0, om.content.size());
        Assert.assertEquals(0, om.order.size());
        
        om.put(key, value);
        
        Assert.assertEquals(1, om.content.size());
        Assert.assertEquals(1, om.order.size());
        
        Assert.assertTrue(om.hasKey(key));
        Assert.assertEquals(0, om.getIndex(key).intValue());
        Assert.assertEquals(value, om.getValue(key));
    }
    
    @Test
    public void testSilly() {
        OrderedMap om = new OrderedMap();
        
        om.put(key, value);
        Assert.assertTrue(om.hasKey(key));
        om.order.clear();
        Assert.assertFalse(om.hasKey(key));
        om.content.clear();
        Assert.assertFalse(om.hasKey(key));
        
        om.put(key, value);
        Assert.assertTrue(om.hasKey(key));
        om.content.clear();
        Assert.assertFalse(om.hasKey(key));
        om.order.clear();
        Assert.assertFalse(om.hasKey(key));

    }
}
