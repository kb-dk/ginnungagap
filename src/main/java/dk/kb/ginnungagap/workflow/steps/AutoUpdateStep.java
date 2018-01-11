package dk.kb.ginnungagap.workflow.steps;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;

/**
 * The step for the automatically update of preservation of records.
 * This means it finds the records, which have been changed since their latest preservation.
 */
public class AutoUpdateStep extends PreservationStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(AutoUpdateStep.class);

    /** The suffix of the raw-files.*/
    protected static final String RAW_FILE_SUFFIX = "_raw.xml";
    
    /** The Cumulus server.*/
    protected final CumulusServer server;
    /** The name of the catalog to preserve.*/
    protected final String catalogName;
    /** */
    protected final Integer numberOfDaysForUpdate;

    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformationHandler The metadata transformer handler.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     * @param catalogName The name of the catalog for this step.
     * @param updateInterval The number of days between the updates.
     */
    public AutoUpdateStep(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformationHandler transformationHandler, BitmagPreserver preserver, String catalogName,
            Integer updateInterval) {
        super(transConf, server, transformationHandler, preserver, catalogName);
        this.server = server;
        this.catalogName = catalogName;
        this.numberOfDaysForUpdate = updateInterval;
    }

    @Override
    public String getName() {
        return "Preservation Update Step";
    }

    @Override
    public void performStep() throws Exception {
        CumulusQuery query = CumulusQuery.getPreservationUpdateQuery(catalogName, numberOfDaysForUpdate);
        CumulusRecordCollection items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getCount() + " records for preservation update.");
        preserveRecordItems(items, catalogName);
    }

    /**
     * The content-file of the given record will not be preserved during a preservation update.
     * @param record The given Cumulus record with the file to preserve.
     * @throws IOException If it fails to package the file.
     */
    @Override
    protected void preserveFile(CumulusRecord record) throws IOException {
        log.debug("Do not preserve the file again, when performing the preservation update.");
    }
}
