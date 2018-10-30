package dk.kb.ginnungagap.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.FullValidationStep;
import dk.kb.ginnungagap.workflow.steps.SimpleValidationStep;

public class ValidationWorkflowTest extends ExtendedTestCase {

    TestConfiguration conf;
    File contentFile;
    String catalogName = "catalog";
    String collectionID = "collection-id-" + UUID.randomUUID().toString();
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        contentFile = TestFileUtils.createFileWithContent("This is the content");        
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }

    @Test
    public void testWorkflowInstantiation() {
        CumulusWrapper cumulusWrapper = Mockito.mock(CumulusWrapper.class);
        ArchiveWrapper archive = Mockito.mock(ArchiveWrapper.class);
        ValidationWorkflow workflow = new ValidationWorkflow();
        workflow.conf = conf;
        workflow.server = cumulusWrapper;
        workflow.archive = archive;
        
        Assert.assertEquals(workflow.getName(), ValidationWorkflow.WORKFLOW_NAME);
        Assert.assertEquals(workflow.getDescription(), ValidationWorkflow.WORKFLOW_DESCRIPTION);
        Assert.assertEquals(workflow.getInterval().longValue(), -1L);
        
        Mockito.verifyZeroInteractions(cumulusWrapper);
        Mockito.verifyZeroInteractions(archive);
    }
    
    @Test
    public void testCreateSteps() {
        CumulusWrapper cumulusWrapper = Mockito.mock(CumulusWrapper.class);
        ArchiveWrapper archive = Mockito.mock(ArchiveWrapper.class);
        ValidationWorkflow workflow = new ValidationWorkflow();
        workflow.conf = conf;
        workflow.server = cumulusWrapper;
        workflow.archive = archive;

        List<WorkflowStep> steps = (List<WorkflowStep>) workflow.createSteps();
        
        Assert.assertEquals(steps.size(), 2*conf.getCumulusConf().getCatalogs().size());
        Assert.assertTrue(steps.get(0) instanceof SimpleValidationStep);
        Assert.assertTrue(steps.get(1) instanceof FullValidationStep);
        
        Mockito.verifyZeroInteractions(archive);
        
        Mockito.verify(cumulusWrapper, Mockito.times(2)).getServer();
        Mockito.verifyNoMoreInteractions(cumulusWrapper);
    }
}
