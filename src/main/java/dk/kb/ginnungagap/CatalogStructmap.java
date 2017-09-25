package dk.kb.ginnungagap;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagArchive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.archive.LocalArchive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;
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
 *   * Bitmag - for The BitRepository
 *   * Local - for placing the  
 * 5. [OPTIONAL] Intellecual entity id for the catalog
 * 
 * e.g.
 * dk.kb.ginningagap.CatalogStructmap conf/ginnungagap.yml CatalogName
 */
public class CatalogStructmap {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CatalogStructmap.class);

    /** Archive parameter for the local archive.*/
    public static final String ARCHIVE_LOCAL = "local";
    /** Archive parameter for the bitrepository archive.*/
    public static final String ARCHIVE_BITMAG = "bitmag";

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath;
        String catalogName = null;
        String preservationCollectionID = null;
        if(args.length < 3) {
            confPath = System.getenv("GINNUNGAGAP_CONF_FILE");
            if(confPath == null || confPath.isEmpty()) {
                failPrintErrorAndExit();
            }
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
            log.info("Creating a new Intellectuel entity id for the catalog: " + intellectualEntityID);
        }

        File confFile = new File(confPath);
        if(!confFile.isFile()) {
            System.err.println("Cannot find the configuration file '" + confFile.getAbsolutePath() + "'.");
            failPrintErrorAndExit();
        }
        Configuration conf = instantiateConfiguration(confFile, catalogName);
        File xsltFile = instantiateTransformationFile(conf);

        Archive archive = instantiateArchive(archiveType, conf);

        Cumulus.CumulusStart();
        try {
            CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
            MetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
            BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());

            System.out.println("Starting workflow");
            createCatalogStructmap(cumulusServer, transformer, preserver, conf, catalogName, 
                    preservationCollectionID, intellectualEntityID);
        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
            archive.shutdown();
        }
    }
    
    /**
     * Instantiates the configuration.
     * @param confFile The file with the configuration.
     * @param catalogName The name of the catalog, which must exist in the configuration.
     * @return The configuration.
     */
    protected static Configuration instantiateConfiguration(File confFile, String catalogName) {
        Configuration conf = new Configuration(confFile);
        
        if(!conf.getCumulusConf().getCatalogs().contains(catalogName)) {
            System.err.println("The catalog name '" + catalogName + "' must be the configuration.");
            failPrintErrorAndExit();
        }

        return conf;
    }
    
    /**
     * Instantiates the transformation file.
     * @param conf The configuration.
     * @return The transformation file.
     */
    protected static File instantiateTransformationFile(Configuration conf) {
        File xsltFile = new File(conf.getTransformationConf().getXsltDir(), "transformCatalogStructmap.xsl");
        if(!xsltFile.isFile()) {
            System.err.println("Missing transformation file '" + xsltFile.getAbsolutePath() + "'");
            failPrintErrorAndExit();
        }
        
        return xsltFile;
    }
    
    /**
     * Instantiates the Archive.
     * @param archiveType The archive-type text-argument.
     * @param conf The configuration.
     * @return The archive.
     */
    protected static Archive instantiateArchive(String archiveType, Configuration conf) {
        if(!archiveType.equalsIgnoreCase(ARCHIVE_LOCAL) && !archiveType.equalsIgnoreCase(ARCHIVE_BITMAG)) {
            System.err.println("Unable to comply with archive type '" + archiveType + "'. Only accepts '"
                    + ARCHIVE_LOCAL + "' or '" + ARCHIVE_BITMAG + "'.");
            failPrintErrorAndExit();
        }

        if(archiveType.equalsIgnoreCase(ARCHIVE_LOCAL)) {
            log.debug("Archiving locally");
            return new LocalArchive();
        } else {
            log.debug("Using Bitrepository as archive");
            return new BitmagArchive(conf.getBitmagConf());
        }
        
    }

    /**
     * Failure. Print argument requirements and exit.
     */
    protected static void failPrintErrorAndExit() {
        System.err.println("Missing arguments. At least two arguments:");
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
     */
    protected static void createCatalogStructmap(CumulusServer cumulusServer, MetadataTransformer transformer, 
            BitmagPreserver preserver, Configuration conf, String catalogName, String collectionID, 
            String intellectualEntityID) {
        CatalogStructMapWorkflow workflow = new CatalogStructMapWorkflow(conf, cumulusServer, preserver, transformer, 
                catalogName, collectionID, intellectualEntityID);

        workflow.run();

        preserver.uploadAll();
    }
}
