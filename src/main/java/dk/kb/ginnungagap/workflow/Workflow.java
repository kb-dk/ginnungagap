package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.utils.CalendarUtils;
import dk.kb.ginnungagap.workflow.schedule.WorkflowState;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;

/**
 * Abstract class for workflows.
 * Deals with the generic part of when the workflow should run.
 * @author jolf
 */
public abstract class Workflow extends TimerTask {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(Workflow.class);
    
    /** The date for the next run of the workflow.*/
    protected Date nextRun;
    /** The current state of the workflow.*/
    protected WorkflowState state = WorkflowState.NOT_RUNNING;
    /** The status of this workflow.*/
    protected String status = "Has not run yet";
    /** The current step running.*/
    protected WorkflowStep currentStep = null;
    /** The steps for the workflow.*/
    protected List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
    
    /** The configuration. Auto-wired.*/
    @Autowired
    protected Configuration conf;
    
    /**
     * Initialization
     */
    @PostConstruct
    protected void init() {
        initSteps();
        readyForNextRun();
    }
    
    /**
     * Initializes the steps for the workflow.
     */
    abstract void initSteps();
    
    /**
     * @return The interval for the workflow.
     */
    abstract Long getInterval();
    
    /**
     * @return The name of the workflow
     */
    abstract String getName();
    
    /**
     * @return The description of the workflow.
     */
    abstract String getDescription();
    
    @Override
    public void run() {
        if(state == WorkflowState.WAITING && nextRun.getTime() <= System.currentTimeMillis()) {
            try {
                state = WorkflowState.RUNNING;
                runWorkflowSteps();
                state = WorkflowState.SUCCEEDED;
            } catch (RuntimeException e) {
                log.warn("Failure while running the workflow '" + getName() + "'", e);
                state = WorkflowState.ABORTED;
            } finally {
                readyForNextRun();
            }
        }
    }
    
    /**
     * The method for actually running the workflow.
     * Goes through all steps and runs them one after the other.
     */
    protected void runWorkflowSteps() {
        try {
            for(WorkflowStep step : steps) {
                step.run();
            }
        } catch (Exception e) {
            log.error("Faild to run all the workflow steps.", e);
            status = "Failure during last run: " + e.getMessage();
        }
    }
    
    /**
     * Initiates the given step and sets it to the current running step.
     * @param step The step to start.
     */
    protected void performStep(WorkflowStep step) {
        if(state != WorkflowState.ABORTED) {
            this.state = WorkflowState.RUNNING;
            this.currentStep = step;
            log.info("Starting step: '" + step.getName() + "'");
            try {
                step.run();
            } catch (Exception e) {
                log.error("Failure in step: '" + step.getName() + "'.", e);
                throw new IllegalStateException("Failed to run step " + step.getName(), e);
            }
        }
    }
    
    /**
     * Start the workflow by setting the nextRun time to 'now'.
     * It will not actually start the workflow immediately, but it will trigger that the timertask is executed 
     * the next time it is executed by the scheduler.
     */
    public void startManually() {
        this.nextRun = new Date(System.currentTimeMillis());
        this.state = WorkflowState.WAITING;
    }
    
    /**
     * @return The current state of the workflow.
     */
    public WorkflowState getState() {
        return state;
    }
    
    /**
     * @return The date for the next time this workflow should be run.
     */
    public String getNextRunDate() {
        if(nextRun != null) {
            return CalendarUtils.dateToText(nextRun);
        } else {
            return "Must be run manual";
        }
    }
    
    /**
     * @return The steps of the workflow.
     */
    public List<WorkflowStep> getSteps() {
        return new ArrayList<WorkflowStep>(steps);
    }
    
    /**
     * Sets this workflow ready for the next run by setting the date for the next run and the state to 'waiting'.
     */
    protected void readyForNextRun() {
        if(getInterval() > 0) {
            Long time = System.currentTimeMillis() + getInterval(); 
            nextRun = new Date(time);
            state = WorkflowState.WAITING;
        }
    }
    
    /**
     * Retrieves the current state of the workflow.
     * @return The current state of the workflow.
     */
    public WorkflowState currentState() {
        return state;
    }
    
    /**
     * Sets a new state for the workflow.
     * @param newState The new state for the workflow.
     */
    public void setCurrentState(WorkflowState newState) {
        this.state = newState;
    }
    
    /**
     * @return The human readable text for the current state.
     */
    public String getHumanReadableState() {
        if(currentStep == null) {
            return state.name();
        } else {
            return currentStep.getName();
        }
    }
}
