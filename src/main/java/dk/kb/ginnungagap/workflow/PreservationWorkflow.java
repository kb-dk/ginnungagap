package dk.kb.ginnungagap.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * Simple workflow for preserving Cumulus items.
 * 
 * It extracts the record from Cumulus, which match the preservation query.
 * Each Cumulus record will first be validated against its required fields, 
 * then all the metadata fields are extracted and transformed.
 * And finally the asset (content file) and transformed metadata will be packaged and sent to the bitrepository.
 */
public class PreservationWorkflow implements Workflow {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(PreservationWorkflow.class);

    /** Transformation configuration for the metadata.*/
    private final TransformationConfiguration conf;
    /** The Cumulus server.*/
    private final CumulusServer server;
    /** The metadata transformer.*/
    private final MetadataTransformer transformer;
    /** The bitrepository preserver.*/
    private final BitmagPreserver preserver;
    
    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server where the Cumulus records are extracted.
     * @param transformer The metadata transformer for transforming the metadata.
     * @param preserver the bitrepository preserver, for packaging and preserving the records.
     */
    public PreservationWorkflow(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformer transformer, BitmagPreserver preserver) {
        this.conf = transConf;
        this.server = server;
        this.transformer = transformer;
        this.preserver = preserver;
    }
    
    /**
     * Run the workflow!
     */
    public void run() {
        for(String catalogName : conf.getCatalogs()) {
            log.debug("Run '" + this.getClass().getName() + "' on catalog: " + catalogName);
            runOnCatalog(catalogName);
        }
    }
    
    /**
     * Run the extraction on a given catalog, and preserve each Cumulus record.
     * @param catalogName The name of the catalog.
     */
    protected void runOnCatalog(String catalogName) {
        CumulusQuery query = CumulusQuery.getPreservationQuery(catalogName);
        
        RecordItemCollection items = server.getItems(catalogName, query);
        log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " records to be preserved.");
        
        FieldExtractor fe = new FieldExtractor(items.getLayout());
        for(Item item : items) {
            log.debug("Initiating preservation on '" + item.getDisplayString() + "'");
            CumulusRecord record = new CumulusRecord(fe, item);
            preserverveRecord(record);
        }
    }
    
    /**
     * Preserves a given record.
     * @param record The given Cumulus record to preserve.
     */
    protected void preserverveRecord(CumulusRecord record) {
        try {
            record.validateRequiredFields(conf.getRequiredFields());
            File metadataFile = transformAndValidateMetadata(record);
            
            preserver.packRecord(record, metadataFile);
        } catch (Exception e) {
            log.warn("Preserving the record '" + record + "' failed.", e);
            record.setPreservationFailed("Failed to preservatin record '" + record.getID() + ": \n" + e.getMessage());
            // Send failures back.
            throw new IllegalStateException("Do not continue, when failure", e);
        }
    }
    
    /**
     * Transforms and validates the metadata from the Cumulus record.
     * 
     * @param record The record with the metadata to transform and validate.
     * @return The file containing the transformed metadata.
     * @throws IOException If an error occurs when reading or writing the metadata.
     */
    protected File transformAndValidateMetadata(CumulusRecord record) throws IOException {
        String metadataUUID = record.getMetadataGUID();
        File metadataFile = new File(conf.getMetadataTempDir(), metadataUUID);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            transformer.transformXmlMetadata(record.getMetadata(), os);
            os.flush();
        }
        
        transformer.validate(new FileInputStream(metadataFile));
        
        return metadataFile;
    }
}
