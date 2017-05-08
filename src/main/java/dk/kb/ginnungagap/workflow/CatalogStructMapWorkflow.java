package dk.kb.ginnungagap.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * Workflow for creating a structmap for a given Cumulus catalog.
 */
public class CatalogStructMapWorkflow implements Workflow {

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
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param cumulusServer The Cumulus server
     * @param preserver The bitmag packer and preserver.
     * @param transformer The transformer for the metadata.
     * @param catalogName The name of the catalog for the structmap.
     * @param collectionID The name of the Bitrepository collection, where the metadata will be preserved.
     */
    public CatalogStructMapWorkflow(Configuration conf, CumulusServer cumulusServer, BitmagPreserver preserver, 
            MetadataTransformer transformer, String catalogName, String collectionID) {
        this.conf = conf;
        this.cumulusServer = cumulusServer;
        this.preserver = preserver;
        this.transformer = transformer;
        this.catalogName = catalogName;
        this.collectionID = collectionID;
    }
    
    public void run() {
        try {
            File extractFile = extractGuidsAndFileIDsForCatalog();
            File structmapFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                    UUID.randomUUID().toString());
            try (InputStream in = new FileInputStream(extractFile);
                    OutputStream out = new FileOutputStream(structmapFile)) {
                transformer.transformXmlMetadata(in, out);
            }
            preserver.packMetadataRecordWithoutCumulusReference(structmapFile, collectionID);
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
        RecordItemCollection items = cumulusServer.getItems(catalogName, query);
        FieldExtractor fieldExtractor = new FieldExtractor(items.getLayout());
        GUID uuidGuid = fieldExtractor.getFieldGUID("GUID");
        GUID recordNameGuid = fieldExtractor.getFieldGUID("Record Name");
        
        String uuid = UUID.randomUUID().toString();
        File res = new File(conf.getTransformationConf().getMetadataTempDir(), uuid + ".xml");
        try (FileOutputStream os = new FileOutputStream(res)) {
            os.write("<catalog>\n".getBytes());
            os.write("  <uuid>".getBytes());
            os.write(uuid.getBytes());
            os.write("</uuid>\n".getBytes());
            os.write("  <catalogName>".getBytes());
            os.write(catalogName.getBytes());
            os.write("</catalogName>\n".getBytes());
            for(Item item : items) {
                os.write("  <record>\n".getBytes());
                os.write("    <guid>".getBytes());
                os.write(item.getStringValue(uuidGuid).getBytes());
                os.write("</guid>\n".getBytes());
                os.write("    <name>".getBytes());
                os.write(item.getStringValue(recordNameGuid).getBytes());
                os.write("</name>\n".getBytes());
                os.write("  </record>\n".getBytes());
            }
            os.write("</catalog>\n".getBytes());
        }
        return res;
    }
}
