package dk.kb.ginnungagap.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.workflow.steps.ImportationStep;

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
public class ImportWorkflow extends Workflow {
    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = "Performs the importation of Cumulus records "
            + "asset file from the archive.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Importation Workflow";
    
    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The Cumulus Server.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The Bitrepository archive.*/
    @Autowired
    protected ArchiveWrapper archive;
    
    @Override
    protected void initSteps() {
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            steps.add(new ImportationStep(cumulusWrapper.getServer(), archive, catalogName, conf.getWorkflowConf().getRetainDir()));
        }
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
