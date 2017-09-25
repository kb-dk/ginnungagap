package dk.kb.ginnungagap.workflow.schedule;

/**
 * Interface for a workflow.
 * Should be something Workflow.
 */
public interface Workflow {
    /**
     * Start the workflow.
     */
    void start();
    
    /**
     * @return The current state of the workflow.
     */
    WorkflowState currentState();
    
    /**
     * @param newState The new state for the workflow.
     */
    void setCurrentState(WorkflowState newState);
    
    /**
     * @return A human readable text telling the current state of the workflow.
     */
    String getHumanReadableState();
    
    /**
     * @return Provides a human readable description of the workflow.
     */
    String getDescription();
    
    /**
     *  @return Provides an ID to identify the workflow on.
     */
    String getJobID();
}
