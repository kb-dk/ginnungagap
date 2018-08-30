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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
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
import dk.kb.ginnungagap.workflow.ImportWorkflow;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;
import dk.kb.ginnungagap.workflow.ValidationWorkflow;
import dk.kb.ginnungagap.workflow.Workflow;
import dk.kb.ginnungagap.workflow.schedule.WorkflowState;
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
//    
//    @Test(enabled = false)
//    public void testGinnungagap() {
//        Ginnungagap.main("src/test/resources/conf/ginnungagap.yml", "bitmag");
//    }
//
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testNoArguments() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            Ginnungagap.main();
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//    
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testInvalidArchiveType() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            Ginnungagap.main(testConf.getAbsolutePath(), "THIS_IS_NOT_A_VALID_ARCHIVE");
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//    
//    @Test(expectedExceptions = ExitTrappedException.class)
//    public void testMissingConfigurationFileFailure() throws Exception {
//        try {
//            TestSystemUtils.forbidSystemExitCall();
//            Ginnungagap.main(UUID.randomUUID().toString());
//        } finally {
//            TestSystemUtils.enableSystemExitCall();
//        }
//    }
//    
//    @Test(expectedExceptions = RuntimeException.class)
//    public void testFailure() throws Exception {
//        Ginnungagap.main(testConf.getAbsolutePath(), "local");
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
//        Collection<Workflow> workflows = Ginnungagap.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
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
//        Collection<Workflow> workflows = Ginnungagap.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
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
//        Collection<Workflow> workflows = Ginnungagap.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
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
//        Ginnungagap.instantiateWorkflows(conf, server, transformationHandler, preserver, archive);
//    }
//    
//    @Test
//    public void testRunningWorkflowsSucces() throws InterruptedException {
//        addDescription("Test running a workflow once.");
//        Workflow workflow = mock(Workflow.class);
//        
//        Ginnungagap.runWorkflows(Arrays.asList(workflow), -1);
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
//        Ginnungagap.extractFilesOnly(server, conf, transformer);
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
//        Ginnungagap.extractFilesOnly(server, conf, transformer);
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
//                    Ginnungagap.runWorkflows(Arrays.asList(w), 45000);
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
