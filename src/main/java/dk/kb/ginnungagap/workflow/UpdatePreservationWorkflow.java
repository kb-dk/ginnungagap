package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.PreservationFinalizationStep;
import dk.kb.ginnungagap.workflow.steps.UpdatePreservationStep;

/**
 * The workflow for performing the preservation update of Cumulus items.
 * 
 * It extracts the record from Cumulus, which match the update preservation query.
 * Like the preservation workflow, each Cumulus record will first be validated against its required fields, 
 * then all the metadata fields are extracted and transformed.
 * And finally transformed metadata will be packaged and sent to the bitrepository.
 * 
 * This workflow will not preserve the Asset Reference (the content file) of the Cumulus items.
 */
@Component
public class UpdatePreservationWorkflow extends Workflow {
    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = "Sends new versions of packaged metadata to preservation, "
            + "for records with new/changed metadata, which is ready for preservation update.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Update Preservation Workflow";
    
    /** The Cumulus server.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The metadata transformer handler.*/
    @Autowired
    protected MetadataTransformationHandler transformationHandler;
    /** The bitrepository preserver.*/
    @Autowired
    protected BitmagPreserver preserver;
    
    @Override
    protected Collection<WorkflowStep> createSteps() {
        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        for(String catalogName : cumulusWrapper.getServer().getCatalogNames()) {
            steps.add(new UpdatePreservationStep(conf.getTransformationConf(), cumulusWrapper.getServer(), 
                    transformationHandler, preserver, catalogName));
        }
        steps.add(new PreservationFinalizationStep(preserver));
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
