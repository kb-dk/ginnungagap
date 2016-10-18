package dk.kb.ginnungagap;

import java.io.File;

import dk.kb.ginnungagap.archive.BitmagArchive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;
import dk.kb.ginnungagap.workflow.SimplePreservationWorkflow;

/**
 * Class for instantiating the Ginnungagap workflow.
 * 
 * Will exit if any of the required files are missing.
 * And will throw exception if something is wrong configured wrongly (or if there is bugs in the code :-P ).
 * 
 * TODO: make scheduler.
 */
public class ginnungagap {

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String[] args) {
        // How do you instantiate the primordial void ??
        if(args.length < 1) {
            System.err.println("Missing argument with configuration file.");
            System.exit(-1);
        }
        if(args.length > 2) {
            System.out.println("Only handles one argument; the configuration file. "
                    + "All the other arguments are ignored!");
        }
        
        File confFile = new File(args[0]);
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
        
        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
        MetadataTransformer transformer = new XsltMetadataTransformer(xsltFile);
        BitmagArchive archive = new BitmagArchive(conf.getBitmagConf());
        BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());
        
        SimplePreservationWorkflow workflow = new SimplePreservationWorkflow(conf.getTransformationConf(), 
                cumulusServer, transformer, preserver);
        workflow.run();
        
        preserver.uploadAll();
    }
}
