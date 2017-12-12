package dk.kb.ginnungagap.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * Workflow for creating a structmap for a given Cumulus catalog.
 */
public class CatalogStructMapWorkflow {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CatalogStructMapWorkflow.class);

    /** The configuration.*/
    protected final Configuration conf;
    /** The Cumulus server.*/
    protected final CumulusServer cumulusServer;
    /** The bitmag packer and preserver.*/
    protected final BitmagPreserver preserver;
    /** The transformer for the metadata.*/
    protected final MetadataTransformer transformer;
    /** The name of the catalog for the structmap.*/
    protected final String catalogName;
    /** The name of the Bitrepository collection, where the metadata will be preserved.*/
    protected final String collectionID;
    /** The intellectuel entity id for the catalog.*/
    protected final String intellectualEntityID;
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param cumulusServer The Cumulus server
     * @param preserver The bitmag packer and preserver.
     * @param transformer The transformer for the metadata.
     * @param catalogName The name of the catalog for the structmap.
     * @param collectionID The name of the Bitrepository collection, where the metadata will be preserved.
     * @param intellectualEntityID The id of the intellectuel entity for the catalog.
     */
    public CatalogStructMapWorkflow(Configuration conf, CumulusServer cumulusServer, BitmagPreserver preserver, 
            MetadataTransformer transformer, String catalogName, String collectionID, String intellectualEntityID) {
        this.conf = conf;
        this.cumulusServer = cumulusServer;
        this.preserver = preserver;
        this.transformer = transformer;
        this.catalogName = catalogName;
        this.collectionID = collectionID;
        this.intellectualEntityID = intellectualEntityID;
    }
    
    /**
     * Run this workflow.
     */
    public void run() {
        try {
            File extractFile = extractGuidsAndFileIDsForCatalog();
            File structmapFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                    UUID.randomUUID().toString());
            try (InputStream in = new FileInputStream(extractFile);
                    OutputStream out = new FileOutputStream(structmapFile)) {
                transformer.transformXmlMetadata(in, out);
            }
            preserver.packRepresentationMetadata(structmapFile, collectionID);
        } catch (IOException e) {
            throw new IllegalStateException("Could not extract Cumulus", e);
        }
    }
    
    /**
     * Extract the GUID and record name for all the records in the given catalog.
     * They will be put into a file, formatted in XML in the following way for each record:
     * <br/>
     * &lt;record&gt;<br/>
     * &nbsp;&nbsp;&lt;guid&gt;GUID&lt;/guid&gt;<br/>
     * &nbsp;&nbsp;&lt;name&gt;RECORD NAME&lt;/name&gt;<br/>
     * &lt;/record&gt;<br/>
     * 
     * @return An XML file with the GUID and Record name for all records in the Cumulus catalog.
     * @throws IOException If it fails to write the XML file.
     */
    protected File extractGuidsAndFileIDsForCatalog() throws IOException {
        CumulusQuery query = CumulusQuery.getQueryForAllInCatalog(catalogName);
        CumulusRecordCollection items = cumulusServer.getItems(catalogName, query);
        
        String uuid = UUID.randomUUID().toString();
        File res = new File(conf.getTransformationConf().getMetadataTempDir(), uuid + ".xml");
        try (FileOutputStream os = new FileOutputStream(res)) {
            boolean failure = false;
            os.write("<catalog>\n".getBytes(StandardCharsets.UTF_8));
            os.write("  <uuid>".getBytes(StandardCharsets.UTF_8));
            os.write(uuid.getBytes(StandardCharsets.UTF_8));
            os.write("</uuid>\n".getBytes(StandardCharsets.UTF_8));
            os.write("  <ie_uuid>".getBytes(StandardCharsets.UTF_8));
            os.write(intellectualEntityID.getBytes(StandardCharsets.UTF_8));
            os.write("</ie_uuid>\n".getBytes(StandardCharsets.UTF_8));
            os.write("  <catalogName>".getBytes(StandardCharsets.UTF_8));
            os.write(catalogName.getBytes(StandardCharsets.UTF_8));
            os.write("</catalogName>\n".getBytes(StandardCharsets.UTF_8));
            for(CumulusRecord record : items) {
                String recordName = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
                String recordIntellectualEntity = record.getFieldValueOrNull(
                        Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
                if(recordIntellectualEntity == null) {
                    failure = true;
                    log.warn("Failed extracing intellectual entity for record: " + recordName);
                    continue;
                }
                os.write("  <record>\n".getBytes(StandardCharsets.UTF_8));
                os.write("    <guid>".getBytes(StandardCharsets.UTF_8));
                os.write(recordIntellectualEntity.getBytes(StandardCharsets.UTF_8));
                os.write("</guid>\n".getBytes(StandardCharsets.UTF_8));
                os.write("    <name>".getBytes(StandardCharsets.UTF_8));
                os.write(recordName.getBytes(StandardCharsets.UTF_8));
                os.write("</name>\n".getBytes(StandardCharsets.UTF_8));
                os.write("  </record>\n".getBytes(StandardCharsets.UTF_8));
            }
            os.write("</catalog>\n".getBytes(StandardCharsets.UTF_8));

            if(failure) {
                throw new IllegalStateException("Failed to create the catalog structmap.");
            }
        }
        return res;
    }
}
