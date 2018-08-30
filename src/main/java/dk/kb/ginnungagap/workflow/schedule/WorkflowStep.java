package dk.kb.ginnungagap.workflow.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The interface for a step for a workflow.
 */
public abstract class WorkflowStep {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(WorkflowStep.class);
    
    /** The status of the workflow.*/
    protected String status;
    /** The results of the last run.*/
    protected String resultsOfLastRun;
    /** The time it has taken for the last run, in millis.*/
    protected long timeForLastRun;
    /** The start time for the current run (0 when not running)*/
    protected long currentRunStart = 0L;

    /**
     * Constructor.
     */
    protected WorkflowStep() {
        this.status = "Not yet run.";
        this.resultsOfLastRun = "Not yet run.";
        this.timeForLastRun = -1L;
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
            return "" + getCurrentRunTime() + "...";
        }
        return "" + timeForLastRun;
    }
    
    /**
     * @return The current runtime in millis. 
     */
    public Long getCurrentRunTime() {
        return System.currentTimeMillis() - currentRunStart;
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
            setStatus("Running");
            setResultOfRun("Running...");
            performStep();
            setStatus("Finished");
        } catch (Throwable e) {
            log.error("Failure when running step: " + getName(), e);
            setStatus("Failed");
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
