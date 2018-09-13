package dk.kb.ginnungagap.workflow.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.kb.ginnungagap.workflow.ImportWorkflow;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;
import dk.kb.ginnungagap.workflow.UpdatePreservationWorkflow;
import dk.kb.ginnungagap.workflow.ValidationWorkflow;

/**
 * The workflow scheduler for scheduling the workflows.
 * 
 * Wraps a ScheduledExecutorService, which checks whether to run any of workflows once every second.
 * It is the workflows themselves, who checks their conditions and performs their tasks if the conditions are met.
 */
@Service
public class WorkflowScheduler {
    /** The interval for the timer, so it .*/
    protected static final long TIMER_INTERVAL = 1000L;
    
    /** The preservation workflow.*/
    @Autowired
    PreservationWorkflow preservationWorkflow;
    /** The update preservation workflow.*/
    @Autowired
    UpdatePreservationWorkflow updateWorkflow;
    /** The validation workflow.*/
    @Autowired
    ValidationWorkflow validationWorkflow;
    /** The import workflow.*/
    @Autowired
    ImportWorkflow importWorkflow;
    
    /** The timer for running the TimerTasks.*/
    ScheduledExecutorService executorService;
    
    /**
     * Method for shutting down this service.
     */
    @PreDestroy
    public void shutDown() {
        preservationWorkflow.cancel();
        updateWorkflow.cancel();
        validationWorkflow.cancel();
        importWorkflow.cancel();
        
        executorService.shutdownNow();
    }
    
    /**
     * Scedules the workflows.
     */
    @PostConstruct
    public void scheduleWorkflows() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        
        executorService.scheduleAtFixedRate(preservationWorkflow, TIMER_INTERVAL, 
                TIMER_INTERVAL, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(updateWorkflow, TIMER_INTERVAL, 
                TIMER_INTERVAL, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(validationWorkflow, TIMER_INTERVAL, 
                TIMER_INTERVAL, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(importWorkflow, TIMER_INTERVAL, 
                TIMER_INTERVAL, TimeUnit.MILLISECONDS);
    }
}
