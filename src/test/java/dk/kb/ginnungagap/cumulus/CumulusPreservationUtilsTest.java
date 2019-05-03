package dk.kb.ginnungagap.cumulus;

import static org.mockito.Mockito.*;

import java.io.File;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.testutils.TestFileUtils;

public class CumulusPreservationUtilsTest extends ExtendedTestCase {
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
    }

    @AfterClass
    public void close() {
        TestFileUtils.tearDown();
    }

    @Test
    public void testInstantiation() {
        CumulusPreservationUtils cpu = new CumulusPreservationUtils();
        Assert.assertNotNull(cpu);
    }
    
    @Test
    public void testInitRepresentationIntellectualEntityUUIDWhenNull() {
        addDescription("Test the initRepresentationIntellectualEntityUUID mehtod, when the representation IE-UUID is null");
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(null);
        
        CumulusPreservationUtils.initRepresentationIntellectualEntityUUID(record);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID), anyString());
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testInitRepresentationIntellectualEntityUUIDWhenEmpty() {
        addDescription("Test the initRepresentationIntellectualEntityUUID mehtod, when the representation IE-UUID is the empty string");
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn("");
        
        CumulusPreservationUtils.initRepresentationIntellectualEntityUUID(record);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID));
        verify(record).setStringValueInField(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID), anyString());
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testInitRepresentationIntellectualEntityUUIDWhenItHasValue() {
        addDescription("Test the initRepresentationIntellectualEntityUUID mehtod, when the representation IE-UUID has a valid value");
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(UUID.randomUUID().toString());
        
        CumulusPreservationUtils.initRepresentationIntellectualEntityUUID(record);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID));
        verifyNoMoreInteractions(record);
    }


    @Test
    public void testInitIntellectualEntityUUIDWhenNull() {
        addDescription("Test the initIntellectualEntityUUID mehtod, when the IE-UUID is null");
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(null);
        
        CumulusPreservationUtils.initIntellectualEntityUUID(record);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).setStringValueInField(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY), anyString());
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testInitIntellectualEntityUUIDWhenEmpty() {
        addDescription("Test the initIntellectualEntityUUID mehtod, when the IE-UUID is the empty string");
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn("");
        
        CumulusPreservationUtils.initIntellectualEntityUUID(record);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).setStringValueInField(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY), anyString());
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testInitIntellectualEntityUUIDWhenItHasValue() {
        addDescription("Test the initRepresentationIntellectualEntityUUID mehtod, when the IE-UUID has a valid value");
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        
        CumulusPreservationUtils.initIntellectualEntityUUID(record);
        
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verifyNoMoreInteractions(record);
    }

    @Test
    public void testCreateIErawFileSuccess() throws Exception {
        addDescription("Test the createIErawFile method, when it successfully creates the file.");
        
        String ieUUID = UUID.randomUUID().toString();
        String metadataUUID = UUID.randomUUID().toString();
        String fileUUID = UUID.randomUUID().toString();
        
        File ieRawFile = new File(TestFileUtils.getTempDir(), ieUUID);
        Assert.assertFalse(ieRawFile.exists());
        CumulusPreservationUtils.createIErawFile(ieUUID, metadataUUID, fileUUID, ieRawFile);
        Assert.assertTrue(ieRawFile.exists());
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testCreateIErawFileFailure() throws Exception {
        addDescription("Test the createIErawFile method, when it fails.");
        
        String ieUUID = UUID.randomUUID().toString();
        String metadataUUID = UUID.randomUUID().toString();
        String fileUUID = UUID.randomUUID().toString();
        
        File ieRawFile = new File(TestFileUtils.getTempDir(), ieUUID);
        try {
            ieRawFile.getParentFile().setWritable(false);
            CumulusPreservationUtils.createIErawFile(ieUUID, metadataUUID, fileUUID, ieRawFile);
        } finally {
            ieRawFile.getParentFile().setWritable(true);            
        }
    }
}
