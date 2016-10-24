package dk.kb.ginnungagap;

import java.io.File;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagArchive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.archive.LocalArchive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
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
 */
public class Ginnungagap {

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
        if(args.length > 2) {
            System.out.println("Only handles one argument; the configuration file. "
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
            MetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
            //        Archive archive = new BitmagArchive(conf.getBitmagConf());
            // TODO: test with BitmagArchive
            Archive archive = new LocalArchive();
            BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());

            PreservationWorkflow workflow = new PreservationWorkflow(conf.getTransformationConf(), 
                    cumulusServer, transformer, preserver);
            
            System.out.println("Starting workflow");
            workflow.run();

            preserver.uploadAll();

        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
        }
    }
}
