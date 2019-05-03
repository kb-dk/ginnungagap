package dk.kb.ginnungagap.workflow;

import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.ImportationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Workflow for importing the content file for Cumulus records from the archive.
 */
@Component
public class ImportWorkflow extends Workflow {
    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = "Performs the importation of Cumulus records "
            + "asset file from the archive.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Importation Workflow";
    
    /** The Cumulus Server.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The Bitrepository archive.*/
    @Autowired
    protected ArchiveWrapper archive;

    @Override
    protected Collection<WorkflowStep> createSteps() {
        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            steps.add(new ImportationStep(cumulusWrapper.getServer(), archive, catalogName, 
                    conf.getWorkflowConf().getRetainDir()));
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
