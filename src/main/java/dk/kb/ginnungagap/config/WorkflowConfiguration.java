package dk.kb.ginnungagap.config;

import java.io.File;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Class for the workflow configuration.
 */
public class WorkflowConfiguration {
    /** The time interval between running the interval.*/
    protected final int interval;
    /** The directory, where the retained files should be stored.*/
    protected final File retainDir;

    /** The default update retention; 180 days ~ 6 months.*/
    protected static final int DEFAULT_UPDATE_RETENTION = 180;

    /**
     * Constructor.
     * @param interval The interval for running the workflows.
     * @param retainDir The retain directory.
     */
    public WorkflowConfiguration(int interval, File retainDir) {
        ArgumentCheck.checkExistsDirectory(retainDir, "File retainDir");
        this.interval = interval;
        this.retainDir = retainDir;
    }

    /**
     * @return The interval between running the workflows.
     */
    public long getInterval() {
        return interval;
    }
    
    /**
     * @return The retain directory.
     */
    public File getRetainDir() {
        return retainDir;
    }
}
