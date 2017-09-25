package dk.kb.ginnungagap.workflow.schedule;

/**
 * States for a workflow.
 */
public enum WorkflowState {
    /** When the workflow is not running.*/
    NOT_RUNNING("Not running"),
    /** When the workflow is waiting to be run.*/
    WAITING("Waiting"),
    /** When the workflow is running.*/
    RUNNING("Running"),
    /** When an execution of the workflow has been aborted. */
    ABORTED("Aborted"),
    /** When the workflow have finished. */
    SUCCEEDED("Succeeded");
    
    /**
     * Constructor.
     * @param humanName Human readable form of the enum.
     */
    WorkflowState(String humanName) {
        this.humanName = humanName;
    }
    
    /**
     * Human readable text of the enum.
     */
    private String humanName;
    
    @Override 
    public String toString() {
        return humanName;
    }
}
