package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.List;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.workflow.schedule.AbstractWorkflow;
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
public class ValidationWorkflow extends AbstractWorkflow {
    /** The configuration.*/
    protected final Configuration conf;
    /** The Cumulus Server.*/
    protected final CumulusServer server;
    /** The Bitrepository archive.*/
    protected final Archive archive;
    
    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = "Performs the validation of Cumulus records, "
            + "regarding the state of their archived file:\n"
            + " - Simple validation, where it checks the checksum of the WARC file through the BitmagClient.\n"
            + " - Full validation, where it retrieves the WARC file and validates the WARC record, regarging "
            + "both WARC file checksum, WARC record size, and WARC record checksum.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Validation Workflow";
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param server The Cumulus server.
     * @param archive The Bitrepository archive.
     */
    public ValidationWorkflow(Configuration conf, CumulusServer server, Archive archive) {
        this.conf = conf;
        this.server = server;
        this.archive = archive;
        
        initialiseSteps();
    }
    
    /**
     * Initialize the steps of this workflow.
     */
    protected void initialiseSteps() {
        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            steps.add(new SimpleValidationStep(server, catalogName, archive));
            steps.add(new FullValidationStep(server, catalogName, archive, conf));
        }
        
        setWorkflowSteps(steps);
    }

    @Override
    public String getDescription() {
        return WORKFLOW_DESCRIPTION;
    }

    @Override
    public String getJobID() {
        return WORKFLOW_NAME;
    }
}
