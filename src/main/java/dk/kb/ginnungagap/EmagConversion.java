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
 * Class for instantiating the conversion from E-magasinet.
 * It only converts the digital-objects of the ARC-files in E-magasinet.
 * 
 * This can either be conversion from ARC to WARC, where the Cumulus record will be used for creating the 
 * metadata record for the WARC packaging;
 * Or it can import the digital-object as the Asset for the Cumulus record, an it can then later be 
 * preserved through the general Ginnungagap workflow.
 * 
 * This takes the following arguments:
 * 1. Configuration file
 * 2. Whether to convert into WARC files or import into Cumulus.
 *   * Use 'import' or 'WARC'
 * 3. File with list of ARC-files to convert.
 *   * This file must formatted where each (non-empty) line contains the name of one ARC-file to convert.
 * 4. Name of the catalog with the Cumulus record corresponding to the digital-objects in the ARC files.
 * 5. (optional) Whether to preserve locally or in the Bitrepository (default).
 *   * E.g. either 'bitmag' or 'local'.
 * 
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagConversion conf/ginnungagap.yml arc-list.txt Catalog WARC local
 */
public class EmagConversion {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagConversion.class);

    /** Archive parameter for the local archive.*/
    public static final String ARCHIVE_LOCAL = "local";
    /** Archive parameter for the bitrepository archive.*/
    public static final String ARCHIVE_BITMAG = "bitmag";
    
    /** The */
    public static final String CONVERSION_WARC = "warc";
    public static final String CONVERSION_IMPORT = "import";
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath = null;
        String conversionType = null;;
        String arcListPath = null;
        String catalogName = null;
        if(args.length < 4) {
            System.err.println("Missing argument with configuration file.");
            System.exit(-1);
        } else {
            confPath = args[0];
            conversionType = args[1];
            arcListPath = args[2];
            catalogName = args[3];
        }
        String archiveType = ARCHIVE_BITMAG;
        if(args.length > 4) {
            archiveType = args[4];
            if(!archiveType.equalsIgnoreCase(ARCHIVE_LOCAL) && !archiveType.equalsIgnoreCase(ARCHIVE_BITMAG)) {
                System.err.println("Unable to comply with archive type '" + archiveType + "'. Only accepts '"
                        + ARCHIVE_LOCAL + "' or '" + ARCHIVE_BITMAG + "'.");
                System.exit(-1);
            }
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

        Archive archive;
        if(archiveType.equalsIgnoreCase(ARCHIVE_LOCAL)) {
            log.debug("Archiving locally");
            archive = new LocalArchive();
        } else {
            archive = new BitmagArchive(conf.getBitmagConf());
            log.debug("Using Bitrepository as archive");
        }
        
        if(conversionType.equalsIgnoreCase(CONVERSION_IMPORT)) {
            
        } else if(conversionType.equalsIgnoreCase(CONVERSION_WARC)) {
        } else {
            System.err.println("Unable to comply with conversion type '" + conversionType + "'. Only accepts '"
                    + CONVERSION_IMPORT + "' or '" + CONVERSION_WARC + "'.");
            System.exit(-1);
        }

        Cumulus.CumulusStart();
        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
        try {
            // TODO!!!
        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
            archive.shutdown();
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
