package dk.kb.ginnungagap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.bitrepository.common.utils.FileUtils;
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

/**
 * Class for instantiating the Cumulus File Validation.
 * 
 * It just validates which cumulus record has a file, and which does not (or if it is impossible to access the file).
 * 
 * It only requres the configuration file, where it will iterate through all the catalogs. 
 * But it can also take the output file location as argument.
 * 
 * Receives the following arguments:
 * 1. Configuration file.
 * 2. [OPTIONAL] output file (default is: 'file_validation.txt' in current folder)
 * 
 * e.g.
 * dk.kb.ginningagap.CumulusFileValidation conf/ginnungagap.yml output.txt
 */
public class CumulusFileValidation {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CumulusFileValidation.class);
    /** The name of the default output file. */
    private static final String DEFAULT_OUTPUT_FILE_PATH = "file_validation.txt";
    /** The format of the output file.*/
    private static final String OUTPUT_FORMAT = "catalog;uuid;exists";
    
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
                failPrintErrorAndExit();
            }
        } else {
            confPath = args[0];
        }
        String outputFilePath;
        if(args.length > 1) {
            outputFilePath = args[1];
        } else {
            outputFilePath = DEFAULT_OUTPUT_FILE_PATH;
        }
        
        File confFile = new File(confPath);
        if(!confFile.isFile()) {
            System.err.println("Cannot find the configuration file '" + confFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        Configuration conf = new Configuration(confFile);

        File outputFile = new File(outputFilePath);
        if(outputFile.exists()) {
            FileUtils.deprecateFile(outputFile);
            outputFile = new File(outputFilePath);
        }
        
        Cumulus.CumulusStart();
        try {
            CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());

            System.out.println("Starting workflow");
            validateCumulusRecordFiles(cumulusServer, conf, outputFile);
        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
        }
    }
    
    /**
     * Failure. Print argument requirements and exit.
     */
    protected static void failPrintErrorAndExit() {
        System.err.println("Missing arguments. At least two arguments:");
        System.err.println("  * 1. Configuration file.");
        System.err.println(" * 2. [OPTIONAL] output file (default is: 'file_validation.txt' in current folder)");
        System.exit(-1);        
    }
    
    /**
     * Validates all the file of all cumulus record for the catalogs in the configuration.
     * @param server The cumulus server.
     * @param conf The configuration.
     * @param outputFile The output file.
     */
    protected static void validateCumulusRecordFiles(CumulusServer server, Configuration conf, File outputFile) {
        try (OutputStream out = new FileOutputStream(outputFile)) {
            out.write(OUTPUT_FORMAT.getBytes());
            out.write("\n".getBytes());
            for(String catalog : conf.getCumulusConf().getCatalogs()) {
                validateForCatalog(server, catalog, out);
            }
        } catch (IOException e) {
            log.warn("Failed to validate cumulus record", e);
        }
    }
    
    /**
     * Validates the files for all cumulus record in the given catalog.
     * @param server The cumulus server.
     * @param catalogName The name of the catalog.
     * @param out The output stream for the validation results.
     */
    protected static void validateForCatalog(CumulusServer server, String catalogName, OutputStream out) {
        CumulusQuery query = CumulusQuery.getQueryForAllInCatalog(catalogName);
        RecordItemCollection collection = server.getItems(catalogName, query);
        FieldExtractor fe = new FieldExtractor(collection.getLayout(), server, catalogName);
        for(Item item : collection) {
            CumulusRecord record = new CumulusRecord(fe, item);
            try {
                checkRecord(record, catalogName, out);
            } catch (IOException e) {
                log.error("Failed to handle record '" + record + "'", e);
            }
        }
    }
    
    /**
     * Validates a given cumulus record, and writes the results to the output stream.
     * @param record The current Cumulus record to validate.
     * @param catalogName The name of the catalog.
     * @param out The output stream for the validation results.
     * @throws IOException If it fails to write the results for this record.
     */
    protected static void checkRecord(CumulusRecord record, String catalogName, OutputStream out) throws IOException {
        try {
            File f = record.getFile();
            if(f.exists()) {
                out.write(new String(catalogName + "; " + record.getUUID() + "; FOUND\n").getBytes());
            } else {
                out.write(new String(catalogName + "; " + record.getUUID() + "; MISSING\n").getBytes());                
            }
        } catch (Throwable e) {
            out.write(new String(catalogName + "; " + record.getUUID() + "; FAILED TO FIND\n").getBytes());
            log.debug("Failed to handle record '" + record + "'", e);
        }
        out.flush();
    }
}
