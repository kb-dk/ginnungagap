package dk.kb.ginnungagap;

import java.io.File;

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
 * 2. Archive - must be local (default) or bitmag
 * 3. retrieve only file (default: no)
 * 
 * e.g.
 * dk.kb.ginningagap.Ginnungagap conf/ginnungagap.yml local
 */
public class Ginnungagap {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(Ginnungagap.class);

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String[] args) {
        // How do you instantiate the primordial void ??

        String confPath;
        if(args.length < 1) {
            confPath = System.getenv("GINNUNGAGAP_CONF_FILE");
            if(confPath == null || confPath.isEmpty()) {
                System.err.println("Missing argument with configuration file.");
                System.exit(-1);
            }
        } else {
            confPath = args[0];
        }
        String archiveType = "local";
        boolean fileOnly = false;
        if(args.length > 2) {
            archiveType = args[1];
        }
        if(args.length > 3) {
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
            System.exit(-1);
        }

        Configuration conf = new Configuration(confFile);
        File xsltFile = new File(conf.getTransformationConf().getXsltDir(), "transformToMets.xsl");
        if(!xsltFile.isFile()) {
            System.err.println("Missing transformation file '" + xsltFile.getAbsolutePath() + "'");
            System.exit(-1);
        }

        Cumulus.CumulusStart();

        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
        try {
            //        Archive archive = new BitmagArchive(conf.getBitmagConf());
            // TODO: test with BitmagArchive
            Archive archive;
            if(archiveType.equalsIgnoreCase("local")) {
            archive = new LocalArchive();
            } else {
                archive = new BitmagArchive(conf.getBitmagConf());
            }
            
            if(fileOnly) {
                extractFilesOnly(cumulusServer, conf);
            } else {
                MetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
                BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());

                PreservationWorkflow workflow = new PreservationWorkflow(conf.getTransformationConf(), 
                        cumulusServer, transformer, preserver);

                System.out.println("Starting workflow");
                workflow.run();

                preserver.uploadAll();
            }
        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
        }
    }
    
    /**
     * Extracts only the Cumulus objects as files. 
     * Does not do any kind of transformation or preservation.
     * @param server The cumulus server.
     * @param conf The configuration.
     */
    protected static void extractFilesOnly(CumulusServer server, Configuration conf) {
        for(String catalogName : conf.getTransformationConf().getCatalogs()) {
            log.info("Extracting files for catalog '" + catalogName + "'.");
            CumulusQuery query = CumulusQuery.getPreservationQuery(catalogName);
            RecordItemCollection items = server.getItems(catalogName, query);
            
            log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " records to be preserved.");
            FieldExtractor fe = new FieldExtractor(items.getLayout());
            for(Item item : items) {
                try {
                    CumulusRecord record = new CumulusRecord(fe, item);
                    File cumulusFile = record.getFile();
                    File outputFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                            cumulusFile.getName());
                    FileUtils.copyFile(cumulusFile, outputFile);
                    
                    record.getMetadata(new File(conf.getTransformationConf().getMetadataTempDir(), 
                            cumulusFile.getName() + "_fields.xml"));
                } catch (Exception e) {
                    log.error("Runtime exception caught while trying to handle Cumulus item with ID '" + item.getID()
                            + "'. ", e);
                }
            }
        }
    }
}
