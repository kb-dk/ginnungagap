package dk.kb.ginnungagap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Cumulus;
import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagArchive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.archive.LocalArchive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;

/**
 * Class for instantiating the Ginnungagap workflow.
 * 
 * Only argument required is the path to the configuration directory. It only have to be the relative path.
 * It can either be given through the environment variable, GINNUNGAGAP_CONF_DIR,
 * or given as commandline argument to the main class. 
 * If both arguments are given, then the commandline is used.
 * 
 * Will exit if any of the required files are missing.
 * And will throw exception if something is wrong configured wrongly (or if there is bugs in the code :-P ).
 * 
 * TODO: make scheduler.
 * 
 * Receives the following arguments:
 * 1. Configuration file.
 * 2. Archive - must be local or bitmag (default)
 * 3. retrieve only file and metadata - no packaging or preservation (yes/no; default: no)
 * 
 * e.g.
 * dk.kb.ginningagap.Ginnungagap conf/ginnungagap.yml local
 */
public class Ginnungagap {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(Ginnungagap.class);

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
        if(args.length < 1) {
            confPath = System.getenv("GINNUNGAGAP_CONF_FILE");
            if(confPath == null || confPath.isEmpty()) {
                System.err.println("Missing argument with configuration file.");
                printParametersAndExit();
            }
        } else {
            confPath = args[0];
        }
        String archiveType = ARCHIVE_BITMAG;
        boolean fileOnly = false;
        if(args.length > 1) {
            archiveType = args[1];
            if(!archiveType.equalsIgnoreCase(ARCHIVE_LOCAL) && !archiveType.equalsIgnoreCase(ARCHIVE_BITMAG)) {
                System.err.println("Unable to comply with archive type '" + archiveType + "'. Only accepts '"
                        + ARCHIVE_LOCAL + "' or '" + ARCHIVE_BITMAG + "'.");
                printParametersAndExit();
            }
        }
        if(args.length > 2) {
            if(args[2].startsWith("y") || args[2].startsWith("Y")) {
                fileOnly = true;
            }
        }
        if(args.length > 4) {
            System.out.println("Maximum 3 arguments; the configuration file, archive-type, file-only. "
                    + "All the other arguments are ignored!");
        }

        File confFile = new File(confPath);
        if(!confFile.isFile()) {
            System.err.println("Cannot find the configuration file '" + confFile.getAbsolutePath() + "'.");
            printParametersAndExit();
        }

        Configuration conf = new Configuration(confFile);
        File xsltFile = new File(conf.getTransformationConf().getXsltDir(), "transformToMets.xsl");
        if(!xsltFile.isFile()) {
            System.err.println("Missing transformation file '" + xsltFile.getAbsolutePath() + "'");
            printParametersAndExit();
        }

        Archive archive;
        if(archiveType.equalsIgnoreCase(ARCHIVE_LOCAL)) {
            log.debug("Archiving locally");
            archive = new LocalArchive();
        } else {
            archive = new BitmagArchive(conf.getBitmagConf());
            log.debug("Using Bitrepository as archive");
        }
        
        Cumulus.CumulusStart();
        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
        try {
            MetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
            MetadataTransformer representationTransformer = new XsltMetadataTransformer(
                    new File(conf.getTransformationConf().getXsltDir(), "transformToRepresentationMets.xsl"));
                    
            if(fileOnly) {
                extractFilesOnly(cumulusServer, conf, transformer);
            } else {
                BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());

                PreservationWorkflow workflow = new PreservationWorkflow(conf.getTransformationConf(), 
                        cumulusServer, transformer, representationTransformer, preserver);

                System.out.println("Starting workflow");
                workflow.run();

                preserver.uploadAll();
            }
        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
            archive.shutdown();
        }
    }
    
    /**
     * Prints the parameters for the main class, and then exit.
     * Should only be used, when it has failed on a parameter.
     */
    protected static void printParametersAndExit() {
        System.err.println("Have the following arguments: ");
        System.err.println(" 1. Configuration file.");
        System.err.println(" 2. Archive - must be local or bitmag (default)");
        System.err.println(" 3. retrieve only file and metadata - no packaging or preservation (yes/no; default: no)");
        System.exit(-1);
    }
    
    /**
     * Extracts only the Cumulus objects as files. 
     * Does not do any kind of transformation or preservation.
     * @param server The cumulus server.
     * @param conf The configuration.
     */
    protected static void extractFilesOnly(CumulusServer server, Configuration conf, MetadataTransformer transformer) {
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            log.info("Extracting files for catalog '" + catalogName + "'.");
            CumulusQuery query = CumulusQuery.getPreservationAllQuery(catalogName);
            RecordItemCollection items = server.getItems(catalogName, query);
            
            log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " records to be preserved.");
            FieldExtractor fe = new FieldExtractor(items.getLayout(), server, catalogName);
            for(Item item : items) {
                try {
                    CumulusRecord record = new CumulusRecord(fe, item);
                    String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
                    File metadataOutputFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                            filename + ".xml");
                    record.getMetadata(metadataOutputFile);
                    File transformedMetadataOutputFile = new File(conf.getTransformationConf().getMetadataTempDir(),
                            record.getMetadataGUID());
                    transformer.transformXmlMetadata(new FileInputStream(metadataOutputFile), 
                            new FileOutputStream(transformedMetadataOutputFile));
                    
                    File cumulusFile = record.getFile();
                    File outputFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                            cumulusFile.getName());
                    FileUtils.copyFile(cumulusFile, outputFile);
                } catch (Exception e) {
                    log.error("Runtime exception caught while trying to handle Cumulus item with ID '" + item.getID()
                            + "'. ", e);
                }
            }
        }
    }
}
