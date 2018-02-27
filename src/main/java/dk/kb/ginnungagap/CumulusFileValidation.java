package dk.kb.ginnungagap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.bitrepository.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Cumulus;

import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.exception.ArgumentCheck;

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
public class CumulusFileValidation extends AbstractMain {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CumulusFileValidation.class);
    /** The name of the default output file. */
    private static final String DEFAULT_OUTPUT_FILE_PATH = "file_validation.txt";
    /** The format of the output file.*/
    private static final String OUTPUT_FORMAT = "catalog;uuid;exists";

    /** The output result message when the file is found.*/
    protected static final String OUTPUT_RES_FOUND = "FOUND";
    /** The output result message when the file is not found.*/
    protected static final String OUTPUT_RES_MISSING = "MISSING";
    /** The output result message when an error occurs.*/
    protected static final String OUTPUT_RES_ERROR = "FAILED TO FIND";
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String ... args) {
        // How do you instantiate any part of the primordial void ??
        
        String confPath = null;
        if(args.length < 1) {
            failPrintErrorAndExit();
        } else {
            confPath = args[0];
        }
        String outputFilePath;
        if(args.length > 1) {
            outputFilePath = args[1];
        } else {
            outputFilePath = DEFAULT_OUTPUT_FILE_PATH;
        }

        File outputFile = getOutputFile(outputFilePath);

        try {
            Configuration conf = instantiateConfiguration(confPath);


            Cumulus.CumulusStart();
            try {
                CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());

                System.out.println("Starting workflow");
                validateCumulusRecordFiles(cumulusServer, conf, outputFile);
            } finally {
                System.out.println("Finished!");
                Cumulus.CumulusStop();
            }
        } catch (ArgumentCheck e) {
            log.warn("Argument failure.", e);
            failPrintErrorAndExit();
        }
    }
    
    /**
     * Retrieves the output file.
     * @param outputFilePath The path to the output file.
     * @return The output file.
     */
    protected static File getOutputFile(String outputFilePath) {
        File outputFile = new File(outputFilePath);
        if(outputFile.exists()) {
            FileUtils.deprecateFile(outputFile);
            outputFile = new File(outputFilePath);
        }
        return outputFile;
    }
    
    /**
     * Failure. Print argument requirements and exit.
     */
    protected static void failPrintErrorAndExit() {
        System.err.println("Missing arguments. At least two arguments:");
        System.err.println(" * 1. Configuration file.");
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
            out.write(OUTPUT_FORMAT.getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));
            for(String catalog : conf.getCumulusConf().getCatalogs()) {
                validateForCatalog(server, catalog, out);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to validate cumulus record", e);
        }
    }
    
    /**
     * Validates the files for all cumulus record in the given catalog.
     * @param server The cumulus server.
     * @param catalogName The name of the catalog.
     * @param out The output stream for the validation results.
     */
    protected static void validateForCatalog(CumulusServer server, String catalogName, OutputStream out) 
            throws IOException {
        CumulusQuery query = CumulusQuery.getQueryForAllInCatalog(catalogName);
        CumulusRecordCollection collection = server.getItems(catalogName, query);
        for(CumulusRecord record : collection) {
            checkRecord(record, catalogName, out);
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
                out.write((catalogName + "; " + record.getUUID() + "; " + OUTPUT_RES_FOUND + "\n").getBytes(
                        StandardCharsets.UTF_8));
            } else {
                out.write((catalogName + "; " + record.getUUID() + "; " + OUTPUT_RES_MISSING + "\n").getBytes(
                        StandardCharsets.UTF_8));
            }
        } catch (Throwable e) {
            out.write((catalogName + "; " + record.getUUID() + "; " + OUTPUT_RES_ERROR + "\n").getBytes(
                    StandardCharsets.UTF_8));
            log.debug("Failed to handle record '" + record + "'", e);
        }
        out.flush();
    }
}
