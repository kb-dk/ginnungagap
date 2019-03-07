package dk.kb.ginnungagap.workflow.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;

/**
 * The step for finalizing the preservation workflow.
 * Uploads all the WARC files, which has been created during the other steps of the preservation workflow. 
 */
public class PreservationFinalizationStep extends WorkflowStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(PreservationFinalizationStep.class);
    /** The Bitmag preservation component.*/
    protected final BitmagPreserver preserver;
    
    /**
     * Constructor.
     * @param preserver The Bitmag Preservation component.
     */
    public PreservationFinalizationStep(BitmagPreserver preserver) {
        super(null);
        this.preserver = preserver;
    }
    
    @Override
    public String getName() {
        return "Preservation Finalization Step";
    }

    @Override
    public void performStep() throws Exception {
        try {
            preserver.uploadAll();
            setResultOfRun("Uploaded all WARC files");
        } catch (Throwable e) {
            log.error("Failed to update the packaged files.", e);
            throw new IllegalStateException("Failed to finalize the preservation.", e);
        }
    }
}
