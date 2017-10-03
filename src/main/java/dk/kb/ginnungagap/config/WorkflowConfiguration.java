package dk.kb.ginnungagap.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Class for the workflow configuration.
 */
public class WorkflowConfiguration {
    /** The time interval between running the interval.*/
    protected final int interval;
    /** The list of workflow names.*/
    protected final List<String> workflows;
    
    /**
     * Constructor.
     * @param workflows The names of the workflows.
     * @param interval The interval for running the workflows.
     */
    public WorkflowConfiguration(int interval, Collection<String> workflows) {
        this.workflows = Collections.unmodifiableList(new ArrayList<String>(workflows));
        this.interval = interval;
    }
        
    /**
     * @return The interval between running the workflows.
     */
    public long getInterval() {
        return interval;
    }

    /**
     * @return The names of the workflows.
     */
    public List<String> getWorkflows() {
        return workflows;
    }
}
