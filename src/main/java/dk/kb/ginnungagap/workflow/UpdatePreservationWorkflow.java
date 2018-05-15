package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.List;

import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.workflow.schedule.AbstractWorkflow;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.UpdatePreservationStep;
import dk.kb.ginnungagap.workflow.steps.PreservationFinalizationStep;

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
public class UpdatePreservationWorkflow extends AbstractWorkflow {
    /** Transformation configuration for the metadata.*/
    private final TransformationConfiguration conf;
    /** The Cumulus server.*/
    private final CumulusServer server;
    /** The metadata transformer handler.*/
    private final MetadataTransformationHandler transformationHandler;
    /** The bitrepository preserver.*/
    private final BitmagPreserver preserver;

    /** The description of this workflow.*/
    protected static final String WORKFLOW_DESCRIPTION = "Sends new versions of packaged metadata to preservation, "
            + "for any ";
    /** The name of this workflow.*/
    protected static final String WORKFLOW_NAME = "Update Preservation Workflow";
    
    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformationHandler The metadata transformation handler for transforming the 
     * different kinds of metadata.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     */
    public UpdatePreservationWorkflow(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformationHandler transformationHandler, BitmagPreserver preserver) {
        super(WORKFLOW_NAME);
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
            steps.add(new UpdatePreservationStep(conf, server, transformationHandler, preserver, catalogName));
        }
        steps.add(new PreservationFinalizationStep(preserver));
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
