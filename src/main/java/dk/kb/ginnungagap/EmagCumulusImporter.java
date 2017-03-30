package dk.kb.ginnungagap;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Cumulus;
import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.emagasin.EmagImportation;

/**
 * Class for instantiating the conversion from E-magasinet, by reimporting the content-files into Cumulus again.
 * It extracts the digital-objects of the ARC-files in E-magasinet, finds the given Cumulus record and places them a the pre-ingest area.
 * 
 * This takes the following arguments:
 * 1. Configuration file
 * 2. File with list of ARC-files to convert.
 *   * This file must formatted where each (non-empty) line contains the name of one ARC-file to convert.
 * 3. Name of the catalog with the Cumulus record corresponding to the digital-objects in the ARC files.
 * 4. [OPTIONAL] 
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagConversion conf/ginnungagap.yml arc-list.txt Catalog
 */
public class EmagCumulusImporter {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagCumulusImporter.class);

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath = null;
        String arcListPath = null;
        String catalogName = null;
        if(args.length < 3) {
            System.err.println("Missing arguments. Requires the following arguments:");
            System.err.println("  1. Configuration file.");
            System.err.println("  2. File with list of ARC-files to convert.");
            System.err.println("  3. Name of Cumulus catalog.");
            System.exit(-1);
        } else {
            confPath = args[0];
            arcListPath = args[1];
            catalogName = args[2];
        }

        File confFile = new File(confPath);
        if(!confFile.isFile()) {
            System.err.println("Cannot find the configuration file '" + confFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        File arcListFile = new File(arcListPath);
        if(!arcListFile.isFile()) {
            System.err.println("Cannot find the ARC list file '" + arcListFile.getAbsolutePath() + ".");
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
            // TODO!!!
            EmagImportation converter= new EmagImportation(conf, cumulusServer, catalogName);
//            converter.
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
