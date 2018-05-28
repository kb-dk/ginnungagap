package dk.kb.ginnungagap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.CumulusException;
import com.canto.cumulus.exceptions.UnresolvableAssetReferenceException;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.config.WorkflowConfiguration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils.ExitTrappedException;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.ginnungagap.workflow.ImportWorkflow;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;
import dk.kb.ginnungagap.workflow.ValidationWorkflow;
import dk.kb.ginnungagap.workflow.schedule.Workflow;
import dk.kb.ginnungagap.workflow.schedule.WorkflowState;
import junit.framework.Assert;

public class MetadataExtractionTest extends ExtendedTestCase {

    File testConf;
    TestConfiguration conf;
    String guid;
    String catalogName;
    String recordName;
    
    String warcRecordId = "random-file-uuid";
    File warcFile;
    
    @BeforeClass
    public void setup() throws Exception {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        testConf = new File("src/test/resources/conf/ginnungagap.yml");
        warcFile = TestFileUtils.copyFileToTemp(new File("src/test/resources/warc/warcexample.warc"));
        guid = UUID.randomUUID().toString();
        catalogName = UUID.randomUUID().toString();
        recordName = UUID.randomUUID().toString();
    }
    
    @AfterClass
    public void teardown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInstantiation() {
        MetadataExtraction metadataExtract = new MetadataExtraction();
        Assert.assertNotNull(metadataExtract);
        Assert.assertTrue(metadataExtract instanceof MetadataExtraction);
    }

    @Test(expectedExceptions = ExitTrappedException.class)
    public void testNoArguments() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            MetadataExtraction.main();
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testInvalidIdType() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            MetadataExtraction.main(testConf.getAbsolutePath(), guid, catalogName, "THIS_IS_NOT_A_ID_TYPE");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testMissingConfigurationFileFailure() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            MetadataExtraction.main(UUID.randomUUID().toString(), guid, catalogName);
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testAllArgumentsWithIncorrectCatalogName() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            MetadataExtraction.main(testConf.getAbsolutePath(), guid, catalogName, "GUID", 
                    TestFileUtils.getTempDir().getAbsolutePath(), "yes", AbstractMain.ARCHIVE_LOCAL, "extra unused variable");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test(expectedExceptions = ExitTrappedException.class)
    public void testRecordNameIdentifierArgumentWithIncorrectCatalogName() throws Exception {
        try {
            TestSystemUtils.forbidSystemExitCall();
            MetadataExtraction.main(testConf.getAbsolutePath(), guid, catalogName, "Record Name", 
                    TestFileUtils.getTempDir().getAbsolutePath(), "yes", AbstractMain.ARCHIVE_LOCAL, "extra unused variable");
        } finally {
            TestSystemUtils.enableSystemExitCall();
        }
    }
    
    @Test
    public void testGetRecordSuccessWithUUID() {
        addDescription("Test the getRecord method, when it successfully retrieves the record of an UUID.");
        CumulusServer server = mock(CumulusServer.class);
        CumulusRecord expectedRecord = mock(CumulusRecord.class);
        boolean isGuid = true;
        
        when(server.findCumulusRecord(eq(catalogName), eq(guid))).thenReturn(expectedRecord);
        
        CumulusRecord actualRecord = MetadataExtraction.getRecord(server, catalogName, guid, isGuid);
        
        Assert.assertEquals(expectedRecord, actualRecord);
        
        verify(server).findCumulusRecord(eq(catalogName), eq(guid));
        verifyNoMoreInteractions(server);
        verifyZeroInteractions(expectedRecord);
    }
    
    @Test
    public void testGetRecordSuccessWithRecordName() {
        addDescription("Test the getRecord method, when it successfully retrieves the record of an Record Name.");
        CumulusServer server = mock(CumulusServer.class);
        CumulusRecord expectedRecord = mock(CumulusRecord.class);
        boolean isGuid = false;
        
        when(server.findCumulusRecordByName(eq(catalogName), eq(recordName))).thenReturn(expectedRecord);
        
        CumulusRecord actualRecord = MetadataExtraction.getRecord(server, catalogName, recordName, isGuid);
        
        Assert.assertEquals(expectedRecord, actualRecord);
        
        verify(server).findCumulusRecordByName(eq(catalogName), eq(recordName));
        verifyNoMoreInteractions(server);
        verifyZeroInteractions(expectedRecord);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetRecordFailure() {
        addDescription("Test the getRecord method, when it fails retrieves the record.");
        CumulusServer server = mock(CumulusServer.class);
        when(server.findCumulusRecord(eq(catalogName), eq(guid))).thenReturn(null);
        boolean isGuid = true;
        
        MetadataExtraction.getRecord(server, catalogName, guid, isGuid);
    }
    
    @Test
    public void testGetCurrentMetadataWhenOnlyMetadata() throws Exception {
        addDescription("Test the getCurrentMetadata method, for the success case, when it has no representation. It must only retrieve the Metadata GUID record");
        CumulusRecord record = mock(CumulusRecord.class);
        File destination = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(warcRecordId);
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(null);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(null);
        
        MetadataExtraction.getCurrentMetadata(warcFile, record, destination);

        Assert.assertTrue(new File(destination, warcRecordId).exists());

        verify(record).getFieldValue(eq(Constants.FieldNames.METADATA_GUID));
        verify(record).getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID));
        verify(record).getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID));
        verifyNoMoreInteractions(record);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetCurrentMetadataWhenOnlyIeMetadata() throws Exception {
        addDescription("Test the getCurrentMetadata method, for the success case, when it has no representation. It must only retrieve the IE record");
        CumulusRecord record = mock(CumulusRecord.class);
        File destination = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(warcRecordId);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(null);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(null);
        
        try {
            MetadataExtraction.getCurrentMetadata(warcFile, record, destination);
        } catch (IllegalStateException e) {
            Assert.assertTrue(new File(destination, warcRecordId).exists());
            throw e;
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetCurrentMetadataWhenOnlyRepMetadata() throws Exception {
        addDescription("Test the getCurrentMetadata method, for the success case, when it has no representation. It must only retrieve the Representation metadata record");
        CumulusRecord record = mock(CumulusRecord.class);
        File destination = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());

        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(warcRecordId);
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(UUID.randomUUID().toString());
        
        try {
            MetadataExtraction.getCurrentMetadata(warcFile, record, destination);
        } catch (IllegalStateException e) {
            Assert.assertTrue(new File(destination, warcRecordId).exists());
            throw e;
        }
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testGetCurrentMetadataWhenOnlyRepIeMetadata() throws IOException {
        addDescription("Test the getCurrentMetadata method, for the success case, when it has no representation. It must only retrieve the Representation IE record");
        CumulusRecord record = mock(CumulusRecord.class);
        File destination = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());

        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValue(eq(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
        when(record.getFieldValueOrNull(eq(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID))).thenReturn(warcRecordId);
        
        try {
            MetadataExtraction.getCurrentMetadata(warcFile, record, destination);
        } catch (IllegalStateException e) {
            Assert.assertTrue(new File(destination, warcRecordId).exists());
            throw e;
        }
    }
    
    @Test
    public void testRetrieveFileRecord() throws IOException {
        addDescription("Test the retrieveFileRecord method");
        CumulusRecord record = mock(CumulusRecord.class);
        Archive archive = mock(Archive.class);
        File destinationDir = FileUtils.getDirectory(TestFileUtils.getTempDir(), UUID.randomUUID().toString());
        
        String warcName = UUID.randomUUID().toString();
        String collectionId = UUID.randomUUID().toString();
        
        when(record.getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID))).thenReturn(warcName);
        when(record.getFieldValue(eq(Constants.FieldNames.COLLECTION_ID))).thenReturn(collectionId);
        when(record.getUUID()).thenReturn(warcRecordId);
        when(archive.getFile(eq(warcName), eq(collectionId))).thenReturn(warcFile);
        
        MetadataExtraction.retrieveFileRecord(record, archive, destinationDir);
        
        Assert.assertTrue(new File(destinationDir, warcRecordId).exists());
        
        verify(record).getFieldValue(eq(Constants.FieldNames.RESOURCE_PACKAGE_ID));
        verify(record).getFieldValue(eq(Constants.FieldNames.COLLECTION_ID));
        verify(record).getUUID();
        verifyZeroInteractions(record);
        verify(archive).getFile(eq(warcName), eq(collectionId));
        verifyZeroInteractions(archive);
    }
    
//    @Test(expectedExceptions = RuntimeException.class)
//    public void testFailure() throws Exception {
//        MetadataExtraction.main(testConf.getAbsolutePath(), "local");
//    }
//    
//    @Test
//    public void testInstantiateWorkflowsSuccessImportWorkflow() {
//        addDescription("Test the initiate workflows function, with success with the import workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, 180, TestFileUtils.getTempDir(), Arrays.asList(ImportWorkflow.class.getSimpleName()));
//        conf.setWorkflowConf(wConf);
//        
//        Collection<Workflow> workflows = MetadataExtraction.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
//        Assert.assertNotNull(workflows);
//        Assert.assertEquals(workflows.size(), 1);
//        Assert.assertTrue(workflows.iterator().next() instanceof ImportWorkflow);
//    }
//    
//    @Test
//    public void testInstantiateWorkflowsSuccessValidationWorkflow() {
//        addDescription("Test the initiate workflows function, with success with the validation workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, 180, TestFileUtils.getTempDir(), Arrays.asList(ValidationWorkflow.class.getSimpleName()));
//        conf.setWorkflowConf(wConf);
//        
//        Collection<Workflow> workflows = MetadataExtraction.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
//        Assert.assertNotNull(workflows);
//        Assert.assertEquals(workflows.size(), 1);
//        Assert.assertTrue(workflows.iterator().next() instanceof ValidationWorkflow);
//    }
//    
//    @Test
//    public void testInstantiateWorkflowsSuccessPreservationWorkflow() {
//        addDescription("Test the initiate workflows function, with success with the preservation workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, 180, TestFileUtils.getTempDir(), Arrays.asList(PreservationWorkflow.class.getSimpleName()));
//        conf.setWorkflowConf(wConf);
//        
//        Collection<Workflow> workflows = MetadataExtraction.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
//        Assert.assertNotNull(workflows);
//        Assert.assertEquals(workflows.size(), 1);
//        Assert.assertTrue(workflows.iterator().next() instanceof PreservationWorkflow);
//    }
//    
//    @Test(expectedExceptions = IllegalStateException.class)
//    public void testInstantiateWorkflowsFailure() {
//        addDescription("Test the initiate workflows function, when the workflow-name does not match an actual workflow.");
//        CumulusServer server = mock(CumulusServer.class);
//        MetadataTransformationHandler transformationHandler = mock(MetadataTransformationHandler.class);
//        BitmagPreserver preserver = mock(BitmagPreserver.class);
//        Archive archive = mock(Archive.class);
//
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        WorkflowConfiguration wConf = new WorkflowConfiguration(-1, 180, TestFileUtils.getTempDir(), Arrays.asList("THIS IS NOT A VALID WORKFLOW NAME"));
//        conf.setWorkflowConf(wConf);
//        
//        MetadataExtraction.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
//    }
//    
//    @Test
//    public void testRunningWorkflowsSucces() throws InterruptedException {
//        addDescription("Test running a workflow once.");
//        Workflow workflow = mock(Workflow.class);
//        
//        MetadataExtraction.runWorkflows(Arrays.asList(workflow), -1);
//        
//        verify(workflow).start();
//        verify(workflow).getDescription();
//        verify(workflow).getJobID();
//        verifyNoMoreInteractions(workflow);
//    }
//    
//    @Test
//    public void testExtractFilesOnly() throws Exception {
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        CumulusServer server = mock(CumulusServer.class);
//        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
//        CumulusRecord record = mock(CumulusRecord.class);
//        
//        File resourceFile = new File("src/test/resources/test-resource.txt");
//        
//        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(items);
//        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
//        when(items.getCount()).thenReturn(1);
//        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenReturn(UUID.randomUUID().toString());
//        when(record.getFieldValue(eq(Constants.FieldNames.METADATA_GUID))).thenReturn(UUID.randomUUID().toString());
//        when(record.getFile()).thenReturn(resourceFile);
//        
//        doAnswer(new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                OutputStream out = (OutputStream) args[0];
//                out.write(UUID.randomUUID().toString().getBytes());
//                out.flush();
//                return null;
//            }
//        }).when(record).writeFieldMetadata(any(OutputStream.class));
//        
//        MetadataExtraction.extractFilesOnly(server, conf, transformer);
//        
//        verify(transformer).transformXmlMetadata(any(InputStream.class), any(OutputStream.class));
//        verifyNoMoreInteractions(transformer);
//        
//        verify(server).getItems(anyString(), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(items).iterator();
//        verify(items).getCount();
//        verifyNoMoreInteractions(items);
//        
//        verify(record).writeFieldMetadata(any(OutputStream.class));
//        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
//        verify(record).getFieldValue(eq(Constants.FieldNames.METADATA_GUID));
//        verify(record).getFile();
//        verifyNoMoreInteractions(record);
//    }
//    
//    @Test
//    public void testExtractFilesOnlyFailure() throws Exception {
//        TestConfiguration conf = TestFileUtils.createTempConf();
//        MetadataTransformer transformer = mock(MetadataTransformer.class);
//        CumulusServer server = mock(CumulusServer.class);
//        CumulusRecordCollection items = mock(CumulusRecordCollection.class);
//        CumulusRecord record = mock(CumulusRecord.class);
//        
//        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(items);
//        when(items.iterator()).thenReturn(Arrays.asList(record).iterator());
//        when(items.getCount()).thenReturn(1);
//        when(record.getFieldValue(eq(Constants.FieldNames.RECORD_NAME))).thenThrow(new RuntimeException("FAIL!!!"));
//
//        doAnswer(new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                OutputStream out = (OutputStream) args[0];
//                out.write(UUID.randomUUID().toString().getBytes());
//                out.flush();
//                return null;
//            }
//        }).when(record).writeFieldMetadata(any(OutputStream.class));
//        
//        MetadataExtraction.extractFilesOnly(server, conf, transformer);
//        
//        verifyZeroInteractions(transformer);
//        
//        verify(server).getItems(anyString(), any(CumulusQuery.class));
//        verifyNoMoreInteractions(server);
//        
//        verify(items).iterator();
//        verify(items).getCount();
//        verifyNoMoreInteractions(items);
//        
//        verify(record).getFieldValue(eq(Constants.FieldNames.RECORD_NAME));
//        verifyNoMoreInteractions(record);
//    }
//    
//    // TAKES MORE THAN 1 MINUTE
//    @Test(enabled = false)
//    public void testRunWorkflows() throws InterruptedException {
//        addDescription("Test running the workflows");
//        
//        Workflow w = mock(Workflow.class);
//        when(w.getJobID()).thenReturn("TEST JOB ID");
//        when(w.getDescription()).thenReturn("TEST JOB DESCRIPTION");
//        when(w.currentState()).thenReturn(WorkflowState.NOT_RUNNING);
//        
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    MetadataExtraction.runWorkflows(Arrays.asList(w), 45000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException("interrupted", e);
//                }
//            }
//        };
//        Thread t = new Thread(r);
//        
//        t.start();
//        synchronized(w) {
//            w.wait(62000);
//            t.interrupt();
//        }
//        
//        verify(w, times(4)).getJobID();
//        verify(w).getDescription();
//        verify(w).start();
//        verify(w).currentState();
//        verify(w).setCurrentState(any(WorkflowState.class));
//        verifyNoMoreInteractions(w);
//    }
}
