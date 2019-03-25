package dk.kb.ginnungagap.workflow.reporting;

import dk.kb.ginnungagap.workflow.Workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for creating a catalog specific report for a workflow.
 * There should be one of these for every catalog in the workflow report.
 */
public class CatalogReport {

    /** The name of the catalog for this report.*/
    protected final String catalogName;
    /** The list of successfully handled records.*/
    protected final List<String> succesRecords = new ArrayList<>();
    /** The map between failed records and their exceptions.*/
    protected final Map<String, String> recordFailures = new HashMap<>();

    /**
     * Constructor.
     * @param catalogName The name of the catalog.
     */
    public CatalogReport(String catalogName) {
        this.catalogName = catalogName;
    }

    /**
     * Adds a successfully handled record.
     * @param recordID The ID of the record.
     */
    public void addSuccessRecord(String recordID) {
        succesRecords.add(recordID);
    }

    /**
     * @return The number of failed records.
     */
    public int getNumberOfSuccess() {
        return succesRecords.size();
    }

    /**
     * Adds a failed record.
     * @param recordID The ID of the record.
     * @param cause The cause for the failure.
     */
    public void addFailedRecord(String recordID, String cause) {
        recordFailures.put(recordID, cause);
    }

    /**
     * @return The number of failed records.
     */
    public int getNumberOfFailed() {
        return recordFailures.size();
    }

    /**
     * @return The name of the catalog for this report part.
     */
    public String getCatalogName() {
        return catalogName;
    }
}
