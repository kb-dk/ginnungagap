package dk.kb.ginnungagap.workflow.schedule;

import java.util.Date;
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
    private final Timer timer;
    /** The map between the running timertasks and their names. */
    private Map<String, SchedulableWorkflowTimerTask> intervalTasks = new HashMap<String, 
            SchedulableWorkflowTimerTask>();
    /** The default value for the schuler.*/
    public static final long SCHEDULE_INTERVAL = 60000;

    /** The name of the timer.*/
    private static final String TIMER_NAME = "Service Scheduler";
    /** Whether the timer is a daemon.*/
    private static final boolean TIMER_IS_DAEMON = true;
    /** A timer delay of 0 seconds.*/
    private static final Long NO_DELAY = 0L;

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
            scheduleJob(task);
        }

        intervalTasks.put(workflow.getJobID(), task);
    }

    /**
     * Reschedules the job to start now,
     * @param job the jobn to start
     * @return A string indicating the result of the attempt to start the job.
     */
    public String startJob(Workflow job) {
        log.debug("Starting job: " + job);
        if(job.currentState() != WorkflowState.NOT_RUNNING) {
            log.info("Cannot schedule job,'" + job.getJobID() + "', which is in state '" 
                    + job.currentState() + "'");
            return "Already running";
        }
        long timeBetweenRuns = -1;
        SchedulableWorkflowTimerTask oldTask = cancelJob(job.getJobID());
        if (oldTask != null) {
            timeBetweenRuns = oldTask.getIntervalBetweenRuns();
        }

        SchedulableWorkflowTimerTask task = new SchedulableWorkflowTimerTask(timeBetweenRuns, job);
        scheduleJob(task);
        intervalTasks.put(job.getJobID(), task);
        return "Job scheduled";
    }

    /**
     * @param jobId the indicated job
     * @return the date for the next run of the indicated job. Return null if the job isn't scheduled.
     */
    public Date getNextRun(String jobId) {
        if (intervalTasks.containsKey(jobId)) {
            return intervalTasks.get(jobId).getNextRun();
        } else return null;
    }

    /**
     * @param jobId the indicated job
     * @return the interval between runs for the indicated job. The interval is in milliseconds.
     */
    public long getRunInterval(String jobId) {
        if (intervalTasks.containsKey(jobId)) {
            return intervalTasks.get(jobId).getIntervalBetweenRuns();
        } else return -1;
    }

    /**
     * Cancels the job with the given name.
     *
     * @param jobID The ID of the job to cancel
     * @return The canceled JobTimerTask.
     */
    public SchedulableWorkflowTimerTask cancelJob(String jobID) {
        SchedulableWorkflowTimerTask task = intervalTasks.remove(jobID);
        if(task == null) {
            return null;
        }
        task.cancel();

        return task;
    }
    
    /**
     * Schedules a task. 
     * If the interval for the task is > 0, then it should be scheduled to run at fixed interval, 
     * but it if has a non-positive interval, then it should only be scheduled for one run.
     * @param task The task to schedule.
     */
    private void scheduleJob(SchedulableWorkflowTimerTask task) {
        if(task.getIntervalBetweenRuns() > 0) {
            timer.scheduleAtFixedRate(task, NO_DELAY, SCHEDULE_INTERVAL);            
        } else {
            timer.schedule(task, NO_DELAY);
        }
    }
}
