package dk.kb.ginnungagap.cumulus;

import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import dk.kb.cumulus.CumulusQuery;

public class CumulusQueryUtilsTest extends ExtendedTestCase {

    @Test
    public void testGetPreservationAllQuery() {
        String catalogName = UUID.randomUUID().toString();
        CumulusQuery cq = CumulusQueryUtils.getPreservationAllQuery(catalogName);
        
        Assert.assertTrue(cq.getQuery().contains(catalogName));
    }
    
    @Test
    public void testGetPreservationSubAssetQuery() {
        String catalogName = UUID.randomUUID().toString();        
        CumulusQuery cq = CumulusQueryUtils.getPreservationSubAssetQuery(catalogName);
        
        Assert.assertTrue(cq.getQuery().contains(catalogName));        
    }
    
    @Test
    public void testGetPreservationMasterAssetQuery() {
        String catalogName = UUID.randomUUID().toString();        
        CumulusQuery cq = CumulusQueryUtils.getPreservationMasterAssetQuery(catalogName);
        
        Assert.assertTrue(cq.getQuery().contains(catalogName));        
    }
    
    @Test
    public void testGetQueryForPreservationValidation() {
        String catalogName = UUID.randomUUID().toString();  
        String preservaionValidationValue = UUID.randomUUID().toString();
        CumulusQuery cq = CumulusQueryUtils.getQueryForPreservationValidation(catalogName, preservaionValidationValue);
        
        Assert.assertTrue(cq.getQuery().contains(catalogName));
        Assert.assertTrue(cq.getQuery().contains(preservaionValidationValue));
    }

    @Test
    public void testGetQueryForPreservationImportation() {
        String catalogName = UUID.randomUUID().toString();  
        CumulusQuery cq = CumulusQueryUtils.getQueryForPreservationImportation(catalogName);
        
        Assert.assertTrue(cq.getQuery().contains(catalogName));
    }
}
