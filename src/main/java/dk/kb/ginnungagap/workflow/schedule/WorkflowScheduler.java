package dk.kb.ginnungagap.workflow.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.bitrepository.common.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduler that uses Timer to run workflows.
 */
public class WorkflowScheduler {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(WorkflowScheduler.class);

    /** The timer that schedules events. */
    protected final Timer timer;
    /** The map between the running timertasks and their names. */
    protected Map<String, SchedulableWorkflowTimerTask> intervalTasks = new HashMap<String, 
            SchedulableWorkflowTimerTask>();
    /** The default value for the schuler.*/
    public static final long SCHEDULE_INTERVAL = 60000;

    /** The name of the timer.*/
    protected static final String TIMER_NAME = "Cumulus Bevaring Service Scheduler";
    /** Whether the timer is a daemon.*/
    protected static final boolean TIMER_IS_DAEMON = true;
    /** A timer delay of 0 seconds.*/
    protected static final Long NO_DELAY = 0L;

    /** Setup a timer task for running the workflows at requested interval.
     */
    public WorkflowScheduler() {
        timer = new Timer(TIMER_NAME, TIMER_IS_DAEMON);
    }

    /**
     * Adds a workflow for the scheduler to schedule.
     * @param workflow The job to schedule.
     * @param interval The interval for how often the job should be triggered.
     */
    public void schedule(Workflow workflow, Long interval) {
        log.debug("Scheduling job : " + workflow.getJobID() + " to run every " 
                + TimeUtils.millisecondsToHuman(interval));
        
        SchedulableWorkflowTimerTask task = new SchedulableWorkflowTimerTask(interval, workflow);
        if(interval > 0) {
            timer.scheduleAtFixedRate(task, NO_DELAY, SCHEDULE_INTERVAL);
        }

        intervalTasks.put(workflow.getJobID(), task);
    }
}
