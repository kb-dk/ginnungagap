package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.metadata.utils.GuidExtractionUtils;

/**
 * Class for reinstantiating the Cumulus Record Assets.
 * 
 * It goes through the chosen Cumulus records, finds the file and reinstantiates the AssetReference.
 * 
 * Receives the following arguments:
 * 1. Configuration file.
 * 2. The catalog, which must have its record assets reinstantiated.
 * 3. The input file, with a list of the GUID for the records to have the assets reinstantiated.
 *   - If no list is given, then it will reinstantiate all cumulus record assets.
 * 4. [Optional] Whether to run on all files.
 * 
 * e.g.
 * dk.kb.ginningagap.ReinstantiateCumulusAssets conf/ginnungagap.yml CATALOG recordList.txt "No"
 */
public class ReinstantiateCumulusAssets extends AbstractMain {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(ReinstantiateCumulusAssets.class);
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??
        String confPath = null;
        String catalogName = null;
        String filePath = null;
        if(args.length < 3) {
            failPrintErrorAndExit();
        } else {
            confPath = args[0];
            catalogName = args[1];
            filePath = args[2];
        }
        boolean runOnAll = false;

        try {
            if(args.length > 3) {
                isYes(args[3]);
            }

            Configuration conf = instantiateConfiguration(confPath);

            File inputFile = new File(filePath);
            if(!runOnAll && !inputFile.exists()) {
                System.err.println("Cannot find the input file '" + inputFile.getAbsolutePath() + "'.");
                failPrintErrorAndExit();
            }

            Cumulus.CumulusStart();
            try {
                CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());

                System.out.println("Starting workflow");
                if(runOnAll) {
                    log.info("Running on all records for catalog '" + catalogName + "'.");
                    reinstantiateAllCumulusAssets(cumulusServer, catalogName);
                } else {
                    log.info("Running on the records from file '" + inputFile.getAbsolutePath() + "'");
                    reinstantiateListOfCumulusAssets(cumulusServer, catalogName, inputFile);
                }
            } finally {
                System.out.println("Finished!");
                Cumulus.CumulusStop();
            }
        } catch (IllegalArgumentException e) {
            log.warn("Argument failure.", e);
            failPrintErrorAndExit();
        }
    }
    
    /**
     * Failure. Print argument requirements and exit.
     */
    protected static void failPrintErrorAndExit() {
        System.err.println("Missing arguments. At least two arguments:");
        System.err.println(" * 1. Configuration file.");
        System.err.println(" * 2. The catalog, which must have its record assets reinstantiated.");
        System.err.println(" * 3. The input file, with a list of the GUID for the records to "
                + "have the assets reinstantiated.");
        System.err.println(" * 4. [Optional] Whether to run on all files (YES/NO)");
        System.err.println(" *   - default no");
        System.exit(-1);        
    }
    
    /**
     * Reinstantiates the assets of the records in the list of Cumulus records in the given catalog.
     * @param server The cumulus server.
     * @param catalogName The name of the catalog with the assets to reinstantiate.
     * @param inputFile The file with the list of records to have the assets reinstantiated.
     */
    protected static void reinstantiateListOfCumulusAssets(CumulusServer server, String catalogName, File inputFile) {
        log.info("Reinstantiating the asset of the records from the file : " + inputFile.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), 
                StandardCharsets.UTF_8))) {
            String line;
            while((line = reader.readLine()) != null) {
                String uuid = GuidExtractionUtils.extractGuid(line);
                CumulusRecord record = server.findCumulusRecord(catalogName, uuid);
                reinstantiateRecord(record);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Issue occured while reading doomsdevices", e);
        }
    }
    
    /**
     * Reinstantiates the cumulus assets of all the record in the given catalog.
     * @param server The cumulus server.
     * @param catalogName The name of the catalog with the assets to reinstantiate.
     */
    protected static void reinstantiateAllCumulusAssets(CumulusServer server, String catalogName) {
        log.info("Reinstantiating the asset of all records in catalog: " + catalogName);
        CumulusQuery query = CumulusQuery.getQueryForAllInCatalog(catalogName);
        CumulusRecordCollection items = server.getItems(catalogName, query);
        for(CumulusRecord record : items) {
            reinstantiateRecord(record);
        }
    }
    
    /**
     * Reinstantiates the asset of the cumulus record. 
     * @param record The cumulus record to reinstantiate.
     */
    protected static void reinstantiateRecord(CumulusRecord record) {
        File file = record.getFile();
        record.setNewAssetReference(file);
    }
}
