package dk.kb.ginnungagap.workflow.reporting;

import dk.kb.ginnungagap.workflow.Workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for creating a report for a workflow.
 */
public class WorkflowReport {

    /** The name of the workflow to report from.*/
    protected final String workflowName;
    /** The list of catalog reports.*/
    protected final Map<String, CatalogReport> catalogReports = new HashMap<>();
    /** Any workflow specific failure.*/
    protected final List<String> otherFailure = new ArrayList<>();

    /**
     * Constructor.
     * @param w The workflow to report for.
     */
    public WorkflowReport(Workflow w) {
        this.workflowName = w.getName();
    }

    /**
     * Adds a successfully handled record.
     * @param recordID The ID of the record.
     * @param catalogName The name of the catalog for the record.
     */
    public void addSuccessRecord(String recordID, String catalogName) {
        getCatalogReport(catalogName).addSuccessRecord(recordID);
    }

    /**
     * Adds a failed record.
     * @param recordID The ID of the record.
     * @param cause The cause for the failure.
     * @param catalogName The name of the catalog for the record.
     */
    public void addFailedRecord(String recordID, String cause, String catalogName) {
        getCatalogReport(catalogName).addFailedRecord(recordID, cause);
    }

    /**
     * Adds a failure for the entire workflow.
     * @param cause The description of the failure of the workflow.
     */
    public void addWorkflowFailure(String cause) {
        otherFailure.add(cause);
    }

    /**
     * Creates a subject for a mail report.
     * @return The mail subject description for this report.
     */
    public String getMailSubject() {
        return "Cumulus Preservation Service workflow report: " + workflowName;
    }

    /**
     * Creates the mail content.
     * @return The mail content for this report.
     */
    public String getMainContentForMail() {
        StringBuffer res = new StringBuffer();
        res.append("Report for workflow: ");
        res.append(workflowName);
        res.append("\n\n");

        res.append("Total number of successful records: ");
        res.append(getNumberOfSuccesses());
        res.append("\n\n");
        for(Map.Entry<String, CatalogReport> entry : catalogReports.entrySet()) {
            res.append(entry.getKey());
            res.append(" : ");
            res.append(entry.getValue().getNumberOfSuccess());
            res.append("\n");
        }

        res.append("\n\nNumber of failed records: ");
        res.append(getNumberOfFailures());
        res.append("\n\n");
        for(Map.Entry<String, CatalogReport> entry : catalogReports.entrySet()) {
            res.append(entry.getKey());
            res.append(" : ");
            res.append(entry.getValue().getNumberOfFailed());
            res.append("\n");
        }

        res.append("\n\nOhter failures: ");
        res.append(otherFailure.size());
        res.append("\n");
        for(String failure : otherFailure) {
            res.append(failure);
            res.append("\n");
        }

        return res.toString();
    }

    /**
     * @return The success content for the mail (to be an attachment).
     */
    public String getSuccessContentForMail() {
        StringBuffer res = new StringBuffer();
        for(Map.Entry<String, CatalogReport> entry : catalogReports.entrySet()) {
            res.append("Successes for catalog: ");
            res.append(entry.getKey());
            res.append(" : ");
            res.append(entry.getValue().getNumberOfSuccess());
            res.append("\n");

            for(String id : entry.getValue().succesRecords) {
                res.append(id);
                res.append("\n");
            }
            res.append("\n");
        }

        return res.toString();
    }

    /**
     * @return The failure content for the mail (to be an attachment).
     */
    public String getFailedContentForMail() {
        StringBuffer res = new StringBuffer();
        for(Map.Entry<String, CatalogReport> entry : catalogReports.entrySet()) {
            res.append("Failures for catalog: ");
            res.append(entry.getKey());
            res.append(" : ");
            res.append(entry.getValue().getNumberOfFailed());
            res.append("\n");

            for(Map.Entry<String, String> failure : entry.getValue().recordFailures.entrySet()) {
                res.append(failure.getKey());
                res.append(" : ");
                res.append(failure.getValue());
                res.append("\n");
            }
            res.append("\n");
        }

        return res.toString();
    }

    /**
     * @return Whether or not any incidents have been reported.
     */
    public boolean hasContent() {
        return !catalogReports.isEmpty() || !otherFailure.isEmpty();
    }

    /**
     * @return The name of the workflow for this report.
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * Retrieves the catalog report for the given catalog. If a catalog report does not yet exist for the catalog name,
     * then a new one is created.
     * @param catalogName The name of the catalog whose report should be retrieved.
     * @return The catalog report for the catalog.
     */
    protected CatalogReport getCatalogReport(String catalogName) {
        if(!catalogReports.containsKey(catalogName)) {
            catalogReports.put(catalogName, new CatalogReport(catalogName));
        }
        return catalogReports.get(catalogName);
    }

    /**
     * @return The number of successes.
     */
    public long getNumberOfSuccesses() {
        return catalogReports.values().stream().collect(
                Collectors.summarizingInt(CatalogReport::getNumberOfSuccess)).getSum();
    }

    /**
     * @return The number of failures.
     */
    public long getNumberOfFailures() {
        return catalogReports.values().stream().collect(
                Collectors.summarizingInt(CatalogReport::getNumberOfFailed)).getSum();
    }
}
