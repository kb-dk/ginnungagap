package dk.kb.ginnungagap.workflow.schedule;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract workflow.
 */
public abstract class AbstractWorkflow implements Workflow {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(AbstractWorkflow.class);

    /** The jobID. */
    protected String jobID;
    /** The current state of the workflow. */
    private WorkflowState currentState = WorkflowState.NOT_RUNNING;
    /** The current step running.*/
    private WorkflowStep currentStep = null;

    /** The steps of this workflow.*/
    protected List<WorkflowStep> steps = null;
    
    /**
     * Sets the step for this workflow.
     * @param steps The steps for this workflow.
     */
    protected synchronized void setWorkflowSteps(List<WorkflowStep> steps) {
        this.steps = new ArrayList<WorkflowStep>(steps);
    }
    
    @Override
    public final synchronized void start() {
        if(steps == null || steps.isEmpty()) {
            log.warn("No workflow steps defined! Cannot run workflow: " + getJobID());
            return;
        }
        for(WorkflowStep step : steps) {
            performStep(step);
        }
    }

    /**
     * Initiates the given step and sets it to the current running step.
     * @param step The step to start.
     */
    protected void performStep(WorkflowStep step) {
        if(currentState != WorkflowState.ABORTED) {
            this.currentState = WorkflowState.RUNNING;
            this.currentStep = step;
            log.info("Starting step: '" + step.getName() + "'");
            try {
                step.performStep();
            } catch (Exception e) {
                log.error("Failure in step: '" + step.getName() + "'.", e);
                throw new RuntimeException("Failed to run step " + step.getName(), e);
            }
        }
    }
    
    /**
     * For telling that the workflow has finished its task.
     */
    protected void finish() {
        this.currentState = WorkflowState.NOT_RUNNING;
        this.currentStep = null;
    }
    
    @Override
    public WorkflowState currentState() {
        return currentState;
    }

    @Override
    public void setCurrentState(WorkflowState newState) {
        this.currentState = newState;
    }

    @Override
    public String getHumanReadableState() {
        if(currentStep == null) {
            return currentState.name();
        } else {
            return currentStep.getName();
        }
    }

    @Override
    public String getJobID() {
        return jobID;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(this == o) return true;
        if(o instanceof AbstractWorkflow) return false;
        AbstractWorkflow that = (AbstractWorkflow) o;

        if (!jobID.equals(that.getJobID())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return jobID.hashCode();
    }
}
