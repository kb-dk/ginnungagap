package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Class for the workflow configuration.
 */
public class WorkflowConfiguration {
    /** The time interval between running the interval.*/
    protected final int interval;
    /** The update preservation retention period in days.*/
    protected final int updateRetentionInDays;
    /** The directory, where the retained files should be stored.*/
    protected final File retainDir;
    /** The list of workflow names.*/
    protected final List<String> workflows;

    /** The default update retention; 180 days ~ 6 months.*/
    protected static final int DEFAULT_UPDATE_RETENTION = 180;

    /**
     * Constructor.
     * @param interval The interval for running the workflows.
     * @param updateRetentionInDays The update preservation retention period in days.
     * This may be null, but it will then have the default value.
     * @param retainDir The retain directory.
     * @param workflows The names of the workflows.
     */
    public WorkflowConfiguration(int interval, Integer updateRetentionInDays, File retainDir, 
            Collection<String> workflows) {
        ArgumentCheck.checkNotNullOrEmpty(workflows, "List<String> workflows");
        this.workflows = Collections.unmodifiableList(new ArrayList<String>(workflows));
        this.interval = interval;
        this.retainDir = retainDir;
        if(updateRetentionInDays != null && updateRetentionInDays > 0) {
            this.updateRetentionInDays = updateRetentionInDays;
        } else {
            this.updateRetentionInDays = DEFAULT_UPDATE_RETENTION;
        }
    }

    /**
     * @return The interval between running the workflows.
     */
    public long getInterval() {
        return interval;
    }

    /**
     * @return The update preservation retention period in days.
     */
    public int getUpdateRetentionInDays() {
        return updateRetentionInDays;
    }
    
    /**
     * @return The retain directory.
     */
    public File getRetainDir() {
        return retainDir;
    }

    /**
     * @return The names of the workflows.
     */
    public List<String> getWorkflows() {
        return workflows;
    }
}
