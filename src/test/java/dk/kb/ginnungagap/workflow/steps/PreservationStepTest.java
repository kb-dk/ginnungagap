package dk.kb.ginnungagap.workflow.steps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

public class PreservationStepTest extends ExtendedTestCase {

    String recordGuid = "random-file-uuid";
    String warcFileChecksum = "5cbce357d343f30f2c215dcf1ee94c66";
    String warcRecordChecksum = "a2919627d81e5e53bf9e2bce13fa44ae";
    Long warcRecordSize = 36L;

    String testRecordMetadataPath = "src/test/resources/audio_example_1345.xml";

    Configuration conf;
    String collectionId = "test-collection-id-" + UUID.randomUUID().toString();
    String catalogName = "test-catalog-name";

    @BeforeClass
    public void setupClass() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }

    @AfterClass
    public void tearDownClass() {
        TestFileUtils.tearDown();
    }

    @Test
    public void testSendRecordToPreservationSuccessMaster() throws IOException {
        addDescription("Test the sendRecordToPreservation method for the success scenario for a master record.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);

        PreserveAllStep step = new PreserveAllStep(conf.getTransformationConf(), server, transformer, representationTransformer, preserver, catalogName);

        CumulusRecord record = mock(CumulusRecord.class);

        when(record.getMetadataGUID()).thenReturn(recordGuid);
        when(record.getMetadata(any(File.class))).thenReturn(new FileInputStream(new File(testRecordMetadataPath)));
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID))).thenReturn(collectionId);

        when(record.isMasterAsset()).thenReturn(true);

        step.sendRecordToPreservation(record);

        verify(record).initFields();
        verify(record).resetMetadataGuid();
        verify(record).validateRequiredFields(any(RequiredFields.class));
        verify(record, times(2)).getMetadataGUID();
        verify(record).getFieldValue(eq(Constants.PreservationFieldNames.COLLECTIONID));
        verify(record, times(2)).getMetadata(any(File.class));
        verify(record).isMasterAsset();
        verify(record).setStringValueInField(eq(Constants.PreservationFieldNames.METADATA_GUID), anyString());
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).setStringValueInField(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY), anyString());
        verifyNoMoreInteractions(record);

        verify(preserver).packRecord(eq(record), any(File.class));
        verify(preserver).packMetadataRecordWithoutCumulusReference(any(File.class), eq(collectionId));
        verifyNoMoreInteractions(preserver);

        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verify(transformer).validate(any(InputStream.class));
        verifyNoMoreInteractions(transformer);

        verify(representationTransformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
        verify(representationTransformer).validate(any(InputStream.class));        
        verifyNoMoreInteractions(representationTransformer);

        verifyZeroInteractions(server);
    }
    

    
    @Test
    public void testPerformStepWithNoRecords() throws Exception {
        addDescription("Test the importation of a record, when it cannot retrieve the WARC file from the archive.");
        CumulusServer server = mock(CumulusServer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        MetadataTransformer transformer = mock(MetadataTransformer.class);
        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);

        PreserveAllStep step = new PreserveAllStep(conf.getTransformationConf(), server, transformer, representationTransformer, preserver, catalogName);
        
        RecordItemCollection ric = mock(RecordItemCollection.class);
        Layout layout = mock(Layout.class);
        when(ric.getLayout()).thenReturn(layout);
        when(ric.iterator()).thenReturn((new ArrayList<Item>()).iterator());
        
        when(server.getItems(eq(catalogName), any(CumulusQuery.class))).thenReturn(ric);
        
        step.performStep();
        
        verify(server).getItems(eq(catalogName), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verifyZeroInteractions(preserver);
        verifyZeroInteractions(transformer);
        verifyZeroInteractions(representationTransformer);
    }
}
