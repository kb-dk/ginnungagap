package dk.kb.metadata.selector;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PremisPreservatoinLevelEnumeratorSelectorTest extends ExtendedTestCase {

    @Test
    public void testConstructor() {
        addDescription("Tests the constructor.");
        PremisPreservationLevelEnumeratorSelector pples = new PremisPreservationLevelEnumeratorSelector();
        Assert.assertNotNull(pples);
    }
    
    @Test
    public void testGetBitPreservationLevelValueSucces() {
        addDescription("Test that the getBitPreservationLevelValuemethod delivers different types of bit preservation levels");
        for(String s : PremisPreservationLevelEnumeratorSelector.BIT_PRESERVATION_LEVEL_VALUES) {
            String level = PremisPreservationLevelEnumeratorSelector.getBitPreservationLevelValue(s);
            Assert.assertNotNull(level);
            Assert.assertTrue(s.contains(level));
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetBitPreservationLevelValueFailure() {
        addDescription("Test that the getBitPreservationLevelValuemethod fails when given a wrong type.");
        PremisPreservationLevelEnumeratorSelector.getBitPreservationLevelValue("Bits stored in /dev/null !!!");
    }
    
    @Test
    public void testGetLogicalPreservationLevelValueSuccess() {
        addDescription("Test that the getLogicalPreservationLevelValue delivers different types of logical preservation levels");
        for(String s : PremisPreservationLevelEnumeratorSelector.LOGICAL_PRESERVATION_LEVEL_VALUES) {
            String level = PremisPreservationLevelEnumeratorSelector.getLogicalPreservationLevelValue(s);
            Assert.assertNotNull(level);
            Assert.assertTrue(s.contains(level));
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetLogicalPreservationLevelValueFailure() {
        addDescription("Test that the getLogicalPreservationLevelValue fails when given a wrong type.");
        PremisPreservationLevelEnumeratorSelector.getLogicalPreservationLevelValue("Bogus sorting of bits!!!");
    }
    
    
    @Test
    public void testGetConfidentialityPreservationLevelValueSuccess() {
        addDescription("Test that the getConfidentialityPreservationLevelValue delivers different types of confidential preservation levels");
        for(String s : PremisPreservationLevelEnumeratorSelector.CONFIDENTIALITY_PRESERVATION_LEVEL_VALUES) {
            String level = PremisPreservationLevelEnumeratorSelector.getConfidentialityPreservationLevelValue(s);
            Assert.assertNotNull(level);
            Assert.assertTrue(s.contains(level));
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetConfidentialityPreservationLevelValueFailure() {
        addDescription("Test that the getConfidentialityPreservationLevelValue fails when given a wrong type.");
        PremisPreservationLevelEnumeratorSelector.getConfidentialityPreservationLevelValue("Use negging to remove confidence!!!");
    }
}
