package dk.kb.ginnungagap.workflow.steps;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * Workflow step for preserving all the master asset records, to be used by the preservation workflow.
 */
public class PreserveMasterAssetStep extends AbstractPreservationStep {
    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformer The metadata transformer for transforming the metadata.
     * @param representationTransformer The metadata transformer for the representation metadata.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     * @param catalogName The name of the catalog for this step.
     */
    public PreserveMasterAssetStep(TransformationConfiguration transConf, CumulusServer server, MetadataTransformer transformer,
            MetadataTransformer representationTransformer, BitmagPreserver preserver, String catalogName) {
        super(transConf, server, transformer, representationTransformer, preserver, catalogName);
    }

    @Override
    public String getName() {
        return "Preserve Master Asset Step";
    }

    @Override
    protected CumulusQuery getQuery() {
        return CumulusQuery.getPreservationMasterAssetQuery(catalogName);
    }
}
