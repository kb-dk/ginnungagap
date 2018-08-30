package dk.kb.ginnungagap.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.workflow.steps.PreservationFinalizationStep;
import dk.kb.ginnungagap.workflow.steps.PreservationStep;

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
            "Preserves all the Cumulus records, which have been set to 'ready for long-term preservation'.";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Preservation Workflow";
    
    /** Transformation configuration for the metadata.*/
    @Autowired
    protected Configuration conf;
    /** The Cumulus server.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The metadata transformer handler.*/
    @Autowired
    protected MetadataTransformationHandler transformationHandler;
    /** The bitrepository preserver.*/
    @Autowired
    protected BitmagPreserver preserver;

    /**
     * Initializes all the steps for this workflow.
     */
    @Override
    protected void initSteps() {
        for(String catalogName : cumulusWrapper.getServer().getCatalogNames()) {
            steps.add(new PreservationStep(conf.getTransformationConf(), cumulusWrapper.getServer(), transformationHandler, 
                    preserver, catalogName));
        }
        steps.add(new PreservationFinalizationStep(preserver));
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
