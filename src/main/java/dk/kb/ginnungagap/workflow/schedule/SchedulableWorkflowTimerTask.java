package dk.kb.ginnungagap.workflow.schedule;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TimerTask wrapper for workflows so they can be scheduled in a service.
 */
public class SchedulableWorkflowTimerTask extends TimerTask {

    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(SchedulableWorkflowTimerTask.class);

    /** The workflow to schedule.*/
    private final Workflow workflow;

    /** The date for the next run of the workflow.*/
    private Date nextRun;
    /** The interval between triggers. */
    private final long interval;

    /**
     * Constructor.
     * @param interval The interval between running the workflow.
     * @param workflow 
     */
    public SchedulableWorkflowTimerTask(long interval, Workflow workflow) {
        this.interval = interval;
        this.workflow = workflow;
        nextRun = new Date(System.currentTimeMillis() + interval);
    }

    /**
     * @return The date for the next time the encapsulated workflow should run.
     */
    public Date getNextRun() {
        if(interval > 0) {
            return new Date(nextRun.getTime());            
        } else {
            return null;
        }
    }

    /**
     * @return The interval between the runs in millis.
     */
    public long getIntervalBetweenRuns() {
        return interval;
    }

    /**
     * @return The description of this timertask.
     */
    public String getDescription() {
        return workflow.getDescription();
    }

    /**
     * Runs the job.
     * Resets the date for the next run of the job.
     */
    public void runJob() {
        try {
            if (workflow.currentState().equals(WorkflowState.NOT_RUNNING)) {
                log.info("Starting job: " + workflow.getJobID());
                workflow.setCurrentState(WorkflowState.WAITING);
                workflow.start();
                if (interval > 0) {
                    nextRun = new Date(System.currentTimeMillis() + interval);
                }
                return;
            } else {
                log.info("Ignoring start request for " + workflow.getJobID() + " the job is already running");
                return;
            }
        } catch (Throwable e) {
            log.error("Fault barrier for '" + workflow.getJobID() + "' caught unexpected exception.", e);
        }
    }

    /**
     * @return The name of the job.
     */
    public String getName() {
        return workflow.getJobID().toString();
    }

    /**
     * @return The id of the workflow.
     */
    public String getWorkflowID() {
        return workflow.getJobID();
    }

    @Override
    public void run() {
        if(nextRun != null && (getNextRun() == null || getNextRun().getTime() <= System.currentTimeMillis())) {
            runJob();
        }
    }
}
