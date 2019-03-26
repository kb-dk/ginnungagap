package dk.kb.ginnungagap.workflow.reporting;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class CatalogReportTest extends ExtendedTestCase {

    @Test
    public void testEmptyReport() {
        addDescription("Test the catalog report when it is empty.");
        String catalogName = UUID.randomUUID().toString();
        CatalogReport report = new CatalogReport(catalogName);

        Assert.assertEquals(report.getCatalogName(), catalogName);
        Assert.assertTrue(report.recordFailures.isEmpty());
        Assert.assertTrue(report.succesRecords.isEmpty());
        Assert.assertEquals(report.getNumberOfSuccess(), 0);
        Assert.assertEquals(report.getNumberOfFailed(), 0);
    }

    @Test
    public void testAddSuccessRecord() {
        addDescription("Test the catalog report while adding success to the report");
        String catalogName = UUID.randomUUID().toString();
        CatalogReport report = new CatalogReport(catalogName);

        addStep("Add success record", "one success and no failures");
        report.addSuccessRecord(UUID.randomUUID().toString());
        Assert.assertEquals(report.getNumberOfSuccess(), 1);
        Assert.assertEquals(report.getNumberOfFailed(), 0);
    }

    @Test
    public void testAddFailedRecord() {
        addDescription("Test the catalog report while adding a failed record to the report");
        String catalogName = UUID.randomUUID().toString();
        CatalogReport report = new CatalogReport(catalogName);

        addStep("Add failed record", "no success and one failures");
        report.addFailedRecord(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Assert.assertEquals(report.getNumberOfSuccess(), 0);
        Assert.assertEquals(report.getNumberOfFailed(), 1);
    }
}
