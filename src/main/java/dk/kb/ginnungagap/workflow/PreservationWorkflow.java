package dk.kb.ginnungagap.workflow;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.PreservationFinalizationStep;
import dk.kb.ginnungagap.workflow.steps.PreservationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple workflow for preserving Cumulus items.
 * 
 * It extracts the record from Cumulus, which match the preservation query.
 * Each Cumulus record will first be validated against its required fields, 
 * then all the metadata fields are extracted and transformed.
 * And finally the asset (content file) and transformed metadata will be packaged and sent to the bitrepository.
 */
@Component
public class PreservationWorkflow extends Workflow {
    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = 
            "Preserves all the Cumulus records, which have been set to 'Send til Langtidsbevaring'.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Preservation Workflow";
    
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
        List<WorkflowStep> steps = new ArrayList<>();
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            steps.add(new PreservationStep(conf.getTransformationConf(), cumulusWrapper.getServer(), 
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
    public String getName() {
        return WORKFLOW_NAME;
    }

    @Override
    public Long getInterval() {
        return conf.getWorkflowConf().getInterval();
    }
}
