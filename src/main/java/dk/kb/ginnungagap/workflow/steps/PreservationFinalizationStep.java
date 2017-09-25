package dk.kb.ginnungagap.workflow.steps;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;

/**
 * The step for finalizing the preservation workflow.
 * Uploads all the WARC files, which has been created during the other steps of the preservation workflow. 
 */
public class PreservationFinalizationStep implements WorkflowStep {
    /** The Bitmag preservation component.*/
    protected final BitmagPreserver preserver;
    
    /**
     * Constructor.
     * @param preserver The Bitmag Preservation component.
     */
    public PreservationFinalizationStep(BitmagPreserver preserver) {
        this.preserver = preserver;
    }
    
    @Override
    public String getName() {
        return "Preservation Finalization Step";
    }

    @Override
    public void performStep() throws Exception {
        preserver.uploadAll();
    }
}
