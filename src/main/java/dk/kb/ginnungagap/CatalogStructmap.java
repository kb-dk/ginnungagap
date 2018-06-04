package dk.kb.ginnungagap;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.workflow.CatalogStructMapWorkflow;

/**
 * Class for instantiating the Ginnungagap Catalog Structmap preservation.
 * 
 * It extracts the file-names and GUIDs for all the records within a given catalog.
 * These are transformed into a METS structmap, packaged into a WARC file, and sent to preservation.
 * 
 * It requires the argument for the path to the configuration file, and which catalog to create the 
 * 'catalog structmap'.
 * The archive-type is an optional third argument; whether the WARC file is preserved in the BitRepository
 * or locally in a subfolder in the installation directory. Default is the Bitrepository. 
 * 
 * Receives the following arguments:
 * 1. Configuration file.
 * 2. Catalog name
 * 3. Preservation Collection ID
 * 4. [OPTIONAL] Archive type
 *   * Bitmag - for the BitRepository
 *   * Local - for placing the file in a local folder
 * 5. [OPTIONAL] Intellecual entity id for the catalog
 *   * default: a new IE guid is created for it.
 * 
 * e.g.
 * dk.kb.ginningagap.CatalogStructmap conf/ginnungagap.yml CatalogName
 */
public class CatalogStructmap extends AbstractMain {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CatalogStructmap.class);

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * It requires three argument is required; the configuration file, the catalog name,
     *  and the preservation collection id.
     * And it has two optional arguments: archive type, and intellectual entity ID.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath = null;
        String catalogName = null;
        String preservationCollectionID = null;
        if(args.length < 3) {
            failPrintErrorAndExit();
        } else {
            confPath = args[0];
            catalogName = args[1];
            preservationCollectionID = args[2];
        }
        String archiveType = ARCHIVE_BITMAG;
        if(args.length > 3) {
            archiveType = args[3];
        }
        String intellectualEntityID;
        if(args.length > 4) {
            intellectualEntityID = args[4];
        } else {
            intellectualEntityID = UUID.randomUUID().toString();
            log.info("Newly created Intellectuel entity id for the catalog '" + catalogName + "': " 
                    + intellectualEntityID);
        }

        try {
            Configuration conf = instantiateConfiguration(confPath);
            checkCatalogInConfiguration(conf, catalogName);

            MetadataTransformer transformer = instantiateTransformer(conf, 
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP);

            try (Archive archive = instantiateArchive(archiveType, conf);
                    CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf())) {
                BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());

                System.out.println("Starting workflow");
                createCatalogStructmap(cumulusServer, transformer, preserver, conf, catalogName, 
                        preservationCollectionID, intellectualEntityID);
            } finally {
                System.out.println("Finished!");
            }
        } catch (ArgumentCheck | IllegalArgumentException | IOException e) {
            log.warn("Argument failure.", e);
            failPrintErrorAndExit();
        }
    }

    /**
     * Failure. Print argument requirements and exit.
     */
    protected static void failPrintErrorAndExit() {
        System.err.println("Missing arguments. At least three arguments:");
        System.err.println(" 1. Config file");
        System.err.println(" 2. Catalog name");
        System.err.println(" 3. Bitrepository Collection ID");
        System.err.println(" 4. [OPTIONAL] Archive type (bitmag or local)");
        System.err.println(" 5. [OPTIONAL] Intellecual entity id for the catalog");
        System.exit(-1);        
    }

    /**
     * Create the catalog structmap.
     * @param cumulusServer Access to the cumulus server.
     * @param transformer The transformer for transforming the extracted metadata into the wanted METS structure.
     * @param preserver The preserver for packing it in WARC and sending it to the Bitrepository.
     * @param conf The configuration.
     * @param catalogName The name of the catalog to handle.
     * @param collectionID The id for the Bitrepository collection to preserve the structmap.
     * @param intellectualEntityID The intellectual entity for this catalog. 
     */
    public static void createCatalogStructmap(CumulusServer cumulusServer, MetadataTransformer transformer, 
            BitmagPreserver preserver, Configuration conf, String catalogName, String collectionID, 
            String intellectualEntityID) {
        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, 
                catalogName, collectionID, intellectualEntityID);

        workflow.run();

        preserver.uploadAll();
    }
}
