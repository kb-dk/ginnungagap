package dk.kb.ginnungagap;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Asset;
import com.canto.cumulus.CumulusException;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.exceptions.UnresolvableAssetReferenceException;
import com.canto.cumulus.fieldvalue.AssetReference;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.config.WorkflowConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollectionTest;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.workflow.ImportWorkflow;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;
import dk.kb.ginnungagap.workflow.ValidationWorkflow;
import dk.kb.ginnungagap.workflow.schedule.Workflow;
import junit.framework.Assert;

public class GinnungapTest extends ExtendedTestCase {

    File testConf;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        TestFileUtils.createTempConf();
        FileUtils.copyDirectory(new File("src/main/resources/scripts/xslt"), new File(TestFileUtils.getTempDir(), "scripts/xslt"));
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInstantiation() {
        Ginnungagap ginnungagap = new Ginnungagap();
        Assert.assertNotNull(ginnungagap);
        Assert.assertTrue(ginnungagap instanceof Ginnungagap);
    }
    
    @Test(enabled = false)
    public void testGinnungagap() {
        Ginnungagap.main("src/test/resources/conf/ginnungagap.yml", "bitmag");
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            Ginnungagap.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testInvalidArchiveType() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            Ginnungagap.main(testConf.getAbsolutePath(), "THIS_IS_NOT_A_VALID_ARCHIVE");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testMissingConfigurationFileFailure() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            Ginnungagap.main(UUID.randomUUID().toString());
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = RuntimeException.class)
    public void testFailure() throws Exception {
        Ginnungagap.main(testConf.getAbsolutePath(), "local");
    }
    
//    @Test
//    public void testInstantiateWorkflowsSuccessImportWorkflow() {
//        addDescription("Test the initiate workflows function, with success with the import workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, Arrays.asList(ImportWorkflow.class.getSimpleName()));
//        conf.setWorkflowConf(wConf);
//        
//        Collection<Workflow> workflows = Ginnungagap.instantiateWorkflows(conf, server, transformer, representationTransformer, preserver, archive);
//        Assert.assertNotNull(workflows);
//        Assert.assertEquals(workflows.size(), 1);
//        Assert.assertTrue(workflows.iterator().next() instanceof ImportWorkflow);
//    }
//    
//    @Test
//    public void testInstantiateWorkflowsSuccessValidationWorkflow() {
//        addDescription("Test the initiate workflows function, with success with the validation workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, Arrays.asList(ValidationWorkflow.class.getSimpleName()));
//        conf.setWorkflowConf(wConf);
//        
//        Collection<Workflow> workflows = Ginnungagap.instantiateWorkflows(conf, server, transformer, representationTransformer, preserver, archive);
//        Assert.assertNotNull(workflows);
//        Assert.assertEquals(workflows.size(), 1);
//        Assert.assertTrue(workflows.iterator().next() instanceof ValidationWorkflow);
//    }
//    
//    @Test
//    public void testInstantiateWorkflowsSuccessPreservationWorkflow() {
//        addDescription("Test the initiate workflows function, with success with the preservation workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, Arrays.asList(PreservationWorkflow.class.getSimpleName()));
//        conf.setWorkflowConf(wConf);
//        
//        Collection<Workflow> workflows = Ginnungagap.instantiateWorkflows(conf, server, transformer, representationTransformer, preserver, archive);
//        Assert.assertNotNull(workflows);
//        Assert.assertEquals(workflows.size(), 1);
//        Assert.assertTrue(workflows.iterator().next() instanceof PreservationWorkflow);
//    }
//    
//    @Test(expectedExceptions = IllegalStateException.class)
//    public void testInstantiateWorkflowsFailure() {
//        addDescription("Test the initiate workflows function, when the workflow-name does not match an actual workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        MetadataTransformer representationTransformer = mock(MetadataTransformer.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, Arrays.asList("THIS IS NOT A VALID WORKFLOW NAME"));
//        conf.setWorkflowConf(wConf);
//        
//        Ginnungagap.instantiateWorkflows(conf, server, transformer, representationTransformer, preserver, archive);
//    }
    
    @Test
    public void testRunningWorkflowsSucces() throws InterruptedException {
        addDescription("Test running a workflow once.");
        Workflow workflow = mock(Workflow.class);
        
        Ginnungagap.runWorkflows(Arrays.asList(workflow), -1);
        
        verify(workflow).start();
        verify(workflow).getDescription();
        verify(workflow).getJobID();
        verifyNoMoreInteractions(workflow);
    }
//    
//    @Test
//    public void testExtractFilesOnly() throws CumulusException, UnresolvableAssetReferenceException {
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        CumulusServer server = mock(CumulusServer.class);
//        
//        RecordItemCollection items = mock(RecordItemCollection.class);
//        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(new CumulusRecordCollection(items, server, conf.getCumulusConf().getCatalogs().get(0)));
//        
//        Layout layout = mock(Layout.class);
//        
//        FieldDefinition metadataGuidField = mock(FieldDefinition.class);
//        GUID metadataGuidGuid = mock(GUID.class);
//        when(metadataGuidField.getName()).thenReturn(Constants.PreservationFieldNames.METADATA_GUID);
//        when(metadataGuidField.getFieldUID()).thenReturn(metadataGuidGuid);
//
//        FieldDefinition recordNameField = mock(FieldDefinition.class);
//        GUID recordNameGuid = mock(GUID.class);
//        when(recordNameField.getName()).thenReturn(Constants.FieldNames.RECORD_NAME);
//        when(recordNameField.getFieldUID()).thenReturn(recordNameGuid);
//
//        Collection<FieldDefinition> fields = Arrays.asList(metadataGuidField, recordNameField);
//        when(layout.iterator()).thenReturn(fields.iterator()).thenReturn(fields.iterator()).thenReturn(fields.iterator()).thenReturn(fields.iterator());
//        
//        when(items.getLayout()).thenReturn(layout);
//        when(items.getItemCount()).thenReturn(1);
//        
//        Item item = mock(Item.class);
//        when(item.getStringValue(recordNameGuid)).thenReturn("Record_name");
//        
//        AssetReference fileAssetReference = mock(AssetReference.class);
//        Asset fileAsset = mock(Asset.class);
//        when(fileAsset.getAsFile()).thenReturn(testConf);
//        when(fileAssetReference.getAsset(eq(false))).thenReturn(fileAsset);
//        when(item.getAssetReferenceValue(any(GUID.class))).thenReturn(fileAssetReference);
//        
//        when(items.iterator()).thenReturn(Arrays.asList(item).iterator());
//
//        Ginnungagap.extractFilesOnly(server, conf, transformer);
//        
//        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
//        verifyNoMoreInteractions(transformer);
//        
//        verify(server).getItems(anyString(), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(items).getLayout();
//        verify(items).getItemCount();
//        verify(items).iterator();
//        verifyNoMoreInteractions(items);
//        
//        verify(layout, times(4)).iterator();
//        verifyNoMoreInteractions(layout);
//        
//        verify(metadataGuidField, times(3)).getName();
//        verify(metadataGuidField, times(4)).getFieldUID();
//        verifyNoMoreInteractions(metadataGuidField);
//        
//        verifyZeroInteractions(metadataGuidGuid);
//        
//        verify(recordNameField).getName();
//        verify(recordNameField, times(3)).getFieldUID();
//        verifyNoMoreInteractions(recordNameField);
//        
//        verifyZeroInteractions(recordNameGuid);
//        
//        verify(item).getStringValue(any(GUID.class));
//        verify(item, times(3)).hasValue(any(GUID.class));
//        verify(item).setStringValue(any(GUID.class), anyString());
//        verify(item).getAssetReferenceValue(any(GUID.class));
//        verify(item).save();
//        verifyNoMoreInteractions(item);
//        
//        verify(fileAssetReference).getAsset(eq(false));
//        verifyNoMoreInteractions(fileAssetReference);
//        
//        verify(fileAsset).getAsFile();
//        verifyNoMoreInteractions(fileAsset);
//    }
}
