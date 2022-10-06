package dk.kb.metadata.selector;

import java.util.Arrays;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class ModsEnumeratorSelectorTest extends ExtendedTestCase {
    String defaultTypeOfResourceValue = ModsEnumeratorSelector.TYPE_OF_RESOURCE_SOFTWARE_MULTIMEDIA;
    String defaultRelatedItemAttributeValue = "THIS IS THE DEFAULT RELATED ITEM ATTRIBUTE VALUE.";

    @Test
    public void testConstructor() {
        addDescription("Tests the constructor.");
        ModsEnumeratorSelector as = new ModsEnumeratorSelector();
        Assert.assertNotNull(as);
    }
    
    @Ignore
    @Test
    public void testTypeOfResourceSuccessDefault() {
        addDescription("Test the typeOfResource when given the default resulting values.");
        for(String s : ModsEnumeratorSelector.TYPE_OF_RESOURCE_RESTRICTIONS) {
            Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList(s)), s);
        }
    }

    @Ignore
    @Test
    public void testTypeOfResourceWithEmptyElement() {
        addDescription("Test the typeOfResource when given the empty or null values.");
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("", defaultTypeOfResourceValue)), defaultTypeOfResourceValue);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList(null, defaultTypeOfResourceValue)), defaultTypeOfResourceValue);
    }

    @Ignore
    @Test
    public void testTypeOfResourceWithConvertionElements() {
        addDescription("Test the typeOfResource when given the conversion elements.");
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("LYD")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_SOUND_RECORDING);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("Todimentionelt billedmateriale")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_STILL_IMAGE);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("Billede, Todimentionelt billedmateriale")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_STILL_IMAGE);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("Kort, Todimentionelt billedmateriale")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_CARTOGRAPHIC);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("Musikalier, tryk")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_NOTATED_MUSIC);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("Musikalier, håndskrift")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_NOTATED_MUSIC);
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("Smaatryk")), ModsEnumeratorSelector.TYPE_OF_RESOURCE_STILL_IMAGE);
    }

    @Ignore
    @Test
    public void testTypeOfResourceBadValue() {
        addDescription("Test the typeOfResource when given an incorrect value.");
        Assert.assertEquals(ModsEnumeratorSelector.typeOfResource(Arrays.asList("THIS IS DEFINITELY NOT A PROPER TYPE OF RESOURCE")), 
                ModsEnumeratorSelector.TYPE_OF_RESOURCE_NO_VALUE);
    }
    
    @Test
    public void testRelatedItemAttributeTypeSuccessDefault() {
        addDescription("Test the relatedItemAttributeType when given the default resulting values.");
        for(String s : ModsEnumeratorSelector.RELATED_ITEM_TYPE_RESTRICTION) {
            Assert.assertEquals(ModsEnumeratorSelector.relatedItemAttributeType(s, defaultRelatedItemAttributeValue), s);
        }
    }
    
    @Test
    public void testRelatedItemAttributeTypeEmpty() {
        addDescription("Test the relatedItemAttributeType when given the empty or null values.");
        Assert.assertEquals(ModsEnumeratorSelector.relatedItemAttributeType("", defaultRelatedItemAttributeValue), defaultRelatedItemAttributeValue);
        Assert.assertEquals(ModsEnumeratorSelector.relatedItemAttributeType(null, defaultRelatedItemAttributeValue), defaultRelatedItemAttributeValue);
    }

    @Test
    public void testRelatedItemAttributeTypeBadValue() {
        addDescription("Test the relatedItemAttributeType when given an incorrect value.");
        Assert.assertEquals(ModsEnumeratorSelector.relatedItemAttributeType("THIS IS DEFINITELY NOT A PROPER RELATED ITEM ATTRIBUTE TYPE", defaultRelatedItemAttributeValue), defaultRelatedItemAttributeValue);
    }
}
