package dk.kb.ginnungagap.workflow.reporting;

import dk.kb.ginnungagap.workflow.Workflow;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkflowReportTest extends ExtendedTestCase {


    @Test
    public void testEmptyReport() {
        addDescription("Test the workflow report when it is empty.");
        String workflowName = UUID.randomUUID().toString();
        Workflow w = mock(Workflow.class);
        when(w.getName()).thenReturn(workflowName);
        WorkflowReport report = new WorkflowReport(w);

        Assert.assertEquals(report.getWorkflowName(), workflowName);
        Assert.assertEquals(report.getNumberOfFailures(), 0);
        Assert.assertEquals(report.getNumberOfSuccesses(), 0);
        Assert.assertTrue(report.catalogReports.isEmpty());
        Assert.assertTrue(report.otherFailure.isEmpty());

        Assert.assertTrue(report.getMailSubject().contains(workflowName));
        Assert.assertFalse(report.hasContent());

        Assert.assertEquals(report.getSuccessContentForMail(), "");
        Assert.assertEquals(report.getFailedContentForMail(), "");
    }

    @Test
    public void testSuccess() {
        addDescription("Test the workflow report when successes are added");
        String workflowName = UUID.randomUUID().toString();
        Workflow w = mock(Workflow.class);
        when(w.getName()).thenReturn(workflowName);
        WorkflowReport report = new WorkflowReport(w);

        String record1 = UUID.randomUUID().toString();
        String catalogName = UUID.randomUUID().toString();

        Assert.assertFalse(report.getMainContentForMail().contains(catalogName));

        addStep("Add success", "Has a success");
        report.addSuccessRecord(record1, catalogName);

        Assert.assertEquals(report.getNumberOfFailures(), 0);
        Assert.assertEquals(report.getNumberOfSuccesses(), 1);
        Assert.assertFalse(report.catalogReports.isEmpty());
        Assert.assertTrue(report.otherFailure.isEmpty());

        Assert.assertTrue(report.hasContent());
        Assert.assertTrue(report.getMainContentForMail().contains(catalogName));

        Assert.assertTrue(report.getSuccessContentForMail().contains(record1));
        Assert.assertFalse(report.getFailedContentForMail().contains(record1));

        addStep("Add another success", "Has two successes");
        String record2 = UUID.randomUUID().toString();
        report.addSuccessRecord(record2, catalogName);

        Assert.assertEquals(report.getNumberOfFailures(), 0);
        Assert.assertEquals(report.getNumberOfSuccesses(), 2);
        Assert.assertFalse(report.catalogReports.isEmpty());
        Assert.assertTrue(report.otherFailure.isEmpty());

        Assert.assertTrue(report.hasContent());

        Assert.assertTrue(report.getSuccessContentForMail().contains(record2));
        Assert.assertFalse(report.getFailedContentForMail().contains(record2));
    }

    @Test
    public void testFailure() {
        addDescription("Test the workflow report when failures are added");
        String workflowName = UUID.randomUUID().toString();
        Workflow w = mock(Workflow.class);
        when(w.getName()).thenReturn(workflowName);
        WorkflowReport report = new WorkflowReport(w);

        String record1 = UUID.randomUUID().toString();
        String catalogName = UUID.randomUUID().toString();

        Assert.assertFalse(report.getMainContentForMail().contains(catalogName));

        addStep("Add failure", "Has a failure");
        report.addFailedRecord(record1, UUID.randomUUID().toString(), catalogName);

        Assert.assertEquals(report.getNumberOfFailures(), 1);
        Assert.assertEquals(report.getNumberOfSuccesses(), 0);
        Assert.assertFalse(report.catalogReports.isEmpty());
        Assert.assertTrue(report.otherFailure.isEmpty());

        Assert.assertTrue(report.hasContent());
        Assert.assertTrue(report.getMainContentForMail().contains(catalogName));

        Assert.assertTrue(report.getFailedContentForMail().contains(record1));
        Assert.assertFalse(report.getSuccessContentForMail().contains(record1));

        addStep("Add another failure", "Has two failures");
        String record2 = UUID.randomUUID().toString();
        report.addFailedRecord(record2, UUID.randomUUID().toString(), catalogName);

        Assert.assertEquals(report.getNumberOfFailures(), 2);
        Assert.assertEquals(report.getNumberOfSuccesses(), 0);
        Assert.assertFalse(report.catalogReports.isEmpty());
        Assert.assertTrue(report.otherFailure.isEmpty());

        Assert.assertTrue(report.hasContent());

        Assert.assertTrue(report.getFailedContentForMail().contains(record2));
        Assert.assertFalse(report.getSuccessContentForMail().contains(record2));
    }

    @Test
    public void testOtherFailures() {
        addDescription("Test the workflow report when failures are added");
        String workflowName = UUID.randomUUID().toString();
        Workflow w = mock(Workflow.class);
        when(w.getName()).thenReturn(workflowName);
        WorkflowReport report = new WorkflowReport(w);

        String failure = UUID.randomUUID().toString();

        Assert.assertFalse(report.getMainContentForMail().contains(failure));
        Assert.assertFalse(report.hasContent());

        addStep("Add an other failure", "");
        report.addWorkflowFailure(failure);
        Assert.assertTrue(report.getMainContentForMail().contains(failure));
        Assert.assertTrue(report.hasContent());
    }
}
