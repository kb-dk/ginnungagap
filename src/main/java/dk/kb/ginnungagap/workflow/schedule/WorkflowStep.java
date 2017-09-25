package dk.kb.ginnungagap.workflow.schedule;

/**
 * The interface for a step for a workflow.
 */
public interface WorkflowStep {
    /**
     * @return The name of this given step in the workflow.
     */
    String getName();
    
    /**
     * Perform the task wrapped in this step.
     * @throws Exception if the step failed or the workflow was aborted
     */
    void performStep() throws Exception;
}