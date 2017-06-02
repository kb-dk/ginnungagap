package dk.kb.ginnungagap.emagasin.importation;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RecordUUIDsTest extends ExtendedTestCase {

    RecordUUIDs recordUUIDs;
    
    String catalogID = "Catalog";
    String arcFilename = "ARC filename";
    String arcRecordUUID = UUID.randomUUID().toString();
    String cumulusRecordUUID = UUID.randomUUID().toString();
    
    @BeforeMethod
    public void setup() {
        recordUUIDs = new RecordUUIDs(catalogID, arcFilename, arcRecordUUID, cumulusRecordUUID);
    }
    
    @Test
    public void testRecord() {
        Assert.assertNotNull(recordUUIDs);
        Assert.assertFalse(recordUUIDs.isFound());
        
        Assert.assertEquals(recordUUIDs.getCatalogID(), catalogID);
        Assert.assertEquals(recordUUIDs.getArcFilename(), arcFilename);
        Assert.assertEquals(recordUUIDs.getArcRecordUUID(), arcRecordUUID);
        Assert.assertEquals(recordUUIDs.getCumulusRecordUUID(), cumulusRecordUUID);
        
        Assert.assertTrue(recordUUIDs.toString().contains(catalogID));
        Assert.assertTrue(recordUUIDs.toString().contains(arcFilename));
        Assert.assertTrue(recordUUIDs.toString().contains(arcRecordUUID));
        Assert.assertTrue(recordUUIDs.toString().contains(cumulusRecordUUID));
    }
    
    @Test
    public void testFound() {
        Assert.assertFalse(recordUUIDs.isFound());
        recordUUIDs.setFound();
        Assert.assertTrue(recordUUIDs.isFound());
    }
}
