package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
import dk.kb.metadata.utils.GuidExtrationUtils;

/**
 * Class for reinstantiating the Cumulus Record Assets.
 * 
 * It goes through the chosen Cumulus records, finds the file and reinstantiates the AssetReference.
 * 
 * Receives the following arguments:
 * 1. Configuration file.
 * 2. The catalog, which must have its record assets reinstantiated.
 * 3. [OPTIONAL] input file, with a list of the GUID for the records to have the assets reinstantiated.
 *   - If no list is given, then it will reinstantiate all cumulus record assets.
 * 
 * e.g.
 * dk.kb.ginningagap.ReinstantiateCumulusAssets conf/ginnungagap.yml CATALOG recordList.txt
 */
public class ReinstantiateCumulusAssets {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(ReinstantiateCumulusAssets.class);
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??
        String confPath;
        String catalogName = null;
        String filePath = null;
        if(args.length < 2) {
            confPath = System.getenv("GINNUNGAGAP_CONF_FILE");
            if(confPath == null || confPath.isEmpty()) {
                failPrintErrorAndExit();
            }
        } else {
            confPath = args[0];
            catalogName = args[1];
        }
        if(args.length > 2) {
            filePath = args[2];
        }
        
        File confFile = new File(confPath);
        if(!confFile.isFile()) {
            System.err.println("Cannot find the configuration file '" + confFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        Configuration conf = new Configuration(confFile);

        File inputFile = null;
        if(new File(filePath).exists()) {
            inputFile = new File(filePath);
        }
        
        Cumulus.CumulusStart();
        try {
            CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());

            System.out.println("Starting workflow");
            if(inputFile != null) {
                reinstantiateListOfCumulusAssets(cumulusServer, catalogName, inputFile);
            } else {
                reinstantiateAllCumulusAssets(cumulusServer, catalogName);
            }
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
        System.err.println(" * 1. Configuration file.");
        System.err.println(" * 2. The catalog, which must have its record assets reinstantiated.");
        System.err.println(" * 3. [OPTIONAL] input file, with a list of the GUID for the records to "
                + "have the assets reinstantiated.");
        System.err.println(" *   - If no list is given, then it will reinstantiate all cumulus record assets.");
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
            String line;
            while((line = reader.readLine()) != null) {
                String uuid = GuidExtrationUtils.extractGuid(line);
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
        RecordItemCollection items = server.getItems(catalogName, query);
        FieldExtractor fe = new FieldExtractor(items.getLayout(), server, catalogName);
        for(Item item : items) {
            CumulusRecord record = new CumulusRecord(fe, item);
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
