package dk.kb.ginnungagap.workflow;

import java.util.ArrayList;
import java.util.List;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.workflow.schedule.AbstractWorkflow;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.ginnungagap.workflow.steps.PreservationFinalizationStep;
import dk.kb.ginnungagap.workflow.steps.PreserveAllStep;
import dk.kb.ginnungagap.workflow.steps.PreserveMasterAssetStep;
import dk.kb.ginnungagap.workflow.steps.PreserveSubAssetStep;

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
    /** The metadata transformer.*/
    private final MetadataTransformer transformer;
    /** The transformer for the representation transformer.*/
    private final MetadataTransformer representationTransformer;
    /** The bitrepository preserver.*/
    private final BitmagPreserver preserver;

    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformer The metadata transformer for transforming the metadata.
     * @param representationTransformer The metadata transformer for the representation metadata.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     */
    public PreservationWorkflow(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformer transformer, MetadataTransformer representationTransformer, 
            BitmagPreserver preserver) {
        this.conf = transConf;
        this.server = server;
        this.transformer = transformer;
        this.representationTransformer = representationTransformer;
        this.preserver = preserver;
        
        initialiseSteps();
    }
    
    /**
     * Initializes all the steps for this workflow.
     */
    protected void initialiseSteps() {
        List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
        for(String catalogName : server.getCatalogNames()) {
            steps.add(new PreserveSubAssetStep(conf, server, transformer, representationTransformer, preserver, 
                    catalogName));
            steps.add(new PreserveMasterAssetStep(conf, server, transformer, representationTransformer, preserver, 
                    catalogName));
            steps.add(new PreserveAllStep(conf, server, transformer, representationTransformer, preserver, 
                    catalogName));
        }
        steps.add(new PreservationFinalizationStep(preserver));
        setWorkflowSteps(steps);
    }
    
    @Override
    public String getDescription() {
        return "Preserves all ";
    }

    @Override
    public String getJobID() {
        return "Preservation Workflow";
    }
}
