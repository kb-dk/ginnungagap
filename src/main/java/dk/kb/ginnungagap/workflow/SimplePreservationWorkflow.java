package dk.kb.ginnungagap.workflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.record.CumulusRecord;
import dk.kb.ginnungagap.record.Record;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * Simple workflow for preserving Cumulus items.
 */
public class SimplePreservationWorkflow implements Workflow {
    /** The logger.*/
    private final static Logger log = LoggerFactory.getLogger(SimplePreservationWorkflow.class);

    /** Transformation configuration for the metadata.*/
    private final TransformationConfiguration conf;
    /** The Cumulus server.*/
    private final CumulusServer server;
    /** The metadata transformer.*/
    private final MetadataTransformer transformer;
    
    private final BitmagPreserver preserver;
    /** The warc writer.*/
//    private final WarcWriterWrapper warcWriter;
    
    /**
     * Constructor.
     * @param transConf The configuration for the transformation
     * @param server The Cumulus server.
     * @param bitrepository The client to the bitrepository.
     */
    public SimplePreservationWorkflow(TransformationConfiguration transConf, CumulusServer server,
            MetadataTransformer transformer, BitmagPreserver preserver) {
        this.conf = transConf;
        this.server = server;
        this.transformer = transformer;
        this.preserver = preserver;
//        this.warcWriter = WarcWriterWrapper.getWriter(path, uuid);
//        warcWriter.
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
     * 
     * @param catalogName
     */
    protected void runOnCatalog(String catalogName) {
        CumulusQuery query = CumulusQuery.getArchiveQuery(catalogName);
        
        RecordItemCollection items = server.getItems(catalogName, query);
        FieldExtractor fe = new FieldExtractor(items.getLayout());
        for(Item item : items) {
            log.debug("Initiating preservation on '" + item.getDisplayString() + "'");
            Record record = new CumulusRecord(fe, item);
            preserverveRecord(record);
        }
    }
    
    /**
     * Preserves a given record.
     * @param record 
     */
    protected void preserverveRecord(Record record) {
        try {
            record.validateRequiredFields(conf.getRequiredFields());
            File metadataFile = new File("tmp", record.getID());
            try (OutputStream os = new FileOutputStream(metadataFile)) {
                transformer.transformXmlMetadata(record.getMetadata(), os);
            }
            
            preserver.packRecord(record, metadataFile);
        } catch (Exception e) {
            log.warn("Preserving the record '" + record + "' failed.", e);
            record.setPreservationFailed("Failed to preservatin record '" + record.getID() + ": \n" + e.getMessage());
            // Send failures back.
        }
    }
    
    
    /**
     * Check the conditions, and upload if any of them has been met.
     */
    public synchronized void verifyConditions() {
//        if(warcWriter != null) {
//            if(warcWriter.getWarcFileSize() > context.getConfig().getWarcSizeLimit()) {
//                logger.info("Finished packaging WARC file. Uploading and cleaning up.");
//                uploadWarcFile();
//                cleanUp();
//            }
//        }
    }
//    protected WarcWriter get
}
