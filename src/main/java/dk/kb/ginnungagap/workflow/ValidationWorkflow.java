package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.FullValidationStep;
import dk.kb.ginnungagap.workflow.steps.SimpleValidationStep;

/**
 * Workflow for validating the records.
 * There are two types of validation:
 * Simple validation, and full validation.
 * 
 * The simple validation just checks the default checksum for the WARC file.
 * 
 * The full validation retrieves the file and validates the specific WARC-record.
 */
@Component
public class ValidationWorkflow extends Workflow {
    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = "Performs the validation of Cumulus records, "
            + "regarding the state of their archived file:\n"
            + " - Simple validation, where it checks the checksum of the WARC file through the BitmagClient.\n"
            + " - Full validation, where it retrieves the WARC file and validates the WARC record, regarging "
            + "both WARC file checksum, WARC record size, and WARC record checksum.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Validation Workflow";
    
    /** The Cumulus Server.*/
    @Autowired
    protected CumulusWrapper server;
    /** The Bitrepository archive.*/
    @Autowired
    protected ArchiveWrapper archive;
    
    @Override
    protected Collection<WorkflowStep> createSteps() {
        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            steps.add(new SimpleValidationStep(server.getServer(), catalogName, archive));
            steps.add(new FullValidationStep(server.getServer(), catalogName, archive, conf));
        }
        return steps;
    }

    @Override
    public String getDescription() {
        return WORKFLOW_DESCRIPTION;
    }

    @Override
    public Long getInterval() {
        return -1L;
    }

    @Override
    public String getName() {
        return WORKFLOW_NAME;
    }
}
