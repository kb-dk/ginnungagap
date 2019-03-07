package dk.kb.ginnungagap.workflow.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The interface for a step for a workflow.
 */
public abstract class WorkflowStep {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(WorkflowStep.class);
    
    /** The initial status, before this step has run.*/
    protected static final String INIT_STATUS = "Not yet run.";
    /** The initial results, before this step has finished any run.*/
    protected static final String INIT_RESULTS = "Not yet run.";
    /** The initial value for the last run time, before it has run.*/
    protected static final Long INIT_LAST_RUN_TIME = -1L;
    /** The suffix for the last run time, when it is running. */
    protected static final String RUNNING_TIME_SUFFIX = "...";
    /** The status when the step is running.*/
    protected static final String STATUS_RUNNING = "Running";
    /** The status when the step is successfully finished.*/
    protected static final String STATUS_FINISHED = "Finished";
    /** The status when the step is failed.*/
    protected static final String STATUS_FAILED = "Failed";
    
    /** The status of the workflow.*/
    protected String status;
    /** The results of the last run.*/
    protected String resultsOfLastRun;
    /** The time it has taken for the last run, in millis.*/
    protected long timeForLastRun;
    /** The start time for the current run (0 when not running)*/
    protected long currentRunStart = 0L;
    /** The name of the catalog, which this steps runs for.*/
    protected final String catalog;

    /**
     * Constructor.
     * @param catalog The name of the catalog, which this step runs for.
     *                If set to null, then it runs for any catalog.
     */
    protected WorkflowStep(String catalog) {
        this.status = INIT_STATUS;
        this.resultsOfLastRun = INIT_RESULTS;
        this.timeForLastRun = INIT_LAST_RUN_TIME;
        this.catalog = catalog;
    }
    
    /** 
     * @param status The new status for this step.
     */
    protected void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * @return The status.
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * @return The results of the last run.
     */
    public String getResultOfLastRun() {
        return resultsOfLastRun;
    }
    
    /**
     * @param results The results for the step just finished.
     */
    public void setResultOfRun(String results) {
        resultsOfLastRun = results;
    }

    /**
     * @return The time it took for the latest run.
     */
    public String getExecutionTime() {
        if(currentRunStart > 0) {
            return "" + getCurrentRunTime() + RUNNING_TIME_SUFFIX;
        }
        return "" + timeForLastRun;
    }

    /**
     * @return The time for the last run.
     */
    public Long getTimeForLastRun() {
        return timeForLastRun;
    }
    
    /**
     * @return The current runtime in millis. 
     */
    public Long getCurrentRunTime() {
        return System.currentTimeMillis() - currentRunStart;
    }

    /**
     * Checks whether or not this step should be run for give catalog.
     * (thus if the given catalog name is identical to the catalog for this step, or either catalog
     * name is null).
     * @param catalogName The name of the catalog to run upon. Null if all catalogs.
     * @return Whether or not to run this step.
     */
    public boolean runForCatalog(String catalogName) {
        if(catalogName == null || this.catalog == null) {
            return true;
        }
        return catalogName.equalsIgnoreCase(this.catalog);
    }

    /**
     * @return The catalog for this catalog to run upon. Null if all catalogs.
     */
    public String getCatalogName() {
        return this.catalog;
    }

    /**
     * Run step.
     * Will keep track about the different states throughout running the step (running, fininshed and failure),
     * and it will keep track of the time the step has taken. 
     * The actual methods for the step will be implemented in the step themselves.
     */
    public void run() {
        currentRunStart = System.currentTimeMillis();
        timeForLastRun = -1;
        try {
            setStatus(STATUS_RUNNING);
            setResultOfRun("Running...");
            performStep();
            setStatus(STATUS_FINISHED);
        } catch (Throwable e) {
            log.error("Failure when running step: " + getName(), e);
            setStatus(STATUS_FAILED);
            setResultOfRun("Failure: " + e.getMessage());
        }
        timeForLastRun = System.currentTimeMillis() - currentRunStart;
        currentRunStart = 0L;
    }

    /**
     * @return The name of this given step in the workflow.
     */
    public abstract String getName();
    
    /**
     * Perform the task wrapped in this step.
     * @throws Exception if the step failed or the workflow was aborted
     */
    protected abstract void performStep() throws Exception;
}
