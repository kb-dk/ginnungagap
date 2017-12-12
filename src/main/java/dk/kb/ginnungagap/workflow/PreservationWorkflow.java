package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.List;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.workflow.schedule.AbstractWorkflow;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
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
public class PreservationWorkflow extends AbstractWorkflow {
    /** Transformation configuration for the metadata.*/
    private final TransformationConfiguration conf;
    /** The Cumulus server.*/
    private final CumulusServer server;
    /** The metadata transformer handler.*/
    private final MetadataTransformationHandler transformationHandler;
    /** The bitrepository preserver.*/
    private final BitmagPreserver preserver;

    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformationHandler The metadata transformation handler for transforming the 
     * different kinds of metadata.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     */
    public PreservationWorkflow(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformationHandler transformationHandler, BitmagPreserver preserver) {
        this.conf = transConf;
        this.server = server;
        this.transformationHandler = transformationHandler;
        this.preserver = preserver;
        
        initialiseSteps();
    }
    
    /**
     * Initializes all the steps for this workflow.
     */
    protected void initialiseSteps() {
        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        for(String catalogName : server.getCatalogNames()) {
            steps.add(new PreservationStep(conf, server, transformationHandler, preserver, catalogName));
        }
        steps.add(new PreservationFinalizationStep(preserver));
        setWorkflowSteps(steps);
    }
    
    @Override
    public String getDescription() {
        return "Preserves all the Cumulus records, which have been set to 'ready for long-term preservation'.";
    }

    @Override
    public String getJobID() {
        return "Preservation Workflow";
    }
}
