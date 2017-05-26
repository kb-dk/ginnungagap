package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
import dk.kb.ginnungagap.emagasin.EmagasinRetriever;
import dk.kb.ginnungagap.emagasin.importation.InputFormat;
import dk.kb.ginnungagap.emagasin.importation.OutputFormatter;

/**
 * Class for instantiating the conversion from E-magasinet, by reimporting the content-files into Cumulus again.
 * It extracts the digital-objects of the ARC-files in E-magasinet, finds the given Cumulus record and places them a the pre-ingest area.
 * 
 * This takes the following arguments:
 * 1. Configuration file
 * 2. The CSV file with the ARC-filename, ARC-record-guid and Cumulus-record-guid.
 * 3. Name of the catalog with the Cumulus record corresponding to the digital-objects in the ARC files.
 * 4. path to ARC-file retrieval script
 * 5. [OPITONAL] output directory
 *   - Default '.'
 * 6. [OPTIONAL] prefix to be replace
 *   - Default is no prefix (use '-')
 * 7. [OPTIONAL] prefix to replace with
 *   - Default is no prefix (use '-')
 * 8. [OPTIONAL] path to TIFF validation script
 *   - Default is 'bin/run_checkit_tiff.sh'
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagConversion conf/ginnungagap.yml records-list.csv Catalog . "dia-dom-01" "dia-omf-01"
 */
public class EmagCumulusImporter {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagCumulusImporter.class);
    /** */
    private static final String DEFAULT_TIFF_VALIDATION_PATH = "bin/run_checkit_tiff.sh";
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath = null;
        String recordListPath = null;
        String catalogName = null;
        String retrieveArcScriptPath = null;
        if(args.length < 4) {
            argumentErrorExit();
        } else {
            confPath = args[0];
            recordListPath = args[1];
            catalogName = args[2];
            retrieveArcScriptPath = args[3];
        }
        
        String outputDirPath = ".";
        if(args.length > 4) {
            outputDirPath = args[4];
        }
        if(args.length == 6) {
            System.err.println("Cannot handle only 6th argument ('" + args[5] + "')");
            System.err.println("Requires either neither 6th nor 7th, or both 6th and 7th argument");
            argumentErrorExit();
        }
        String prefixReplaceFrom = null;
        String prefixReplaceTo = null;
        if(args.length > 6) {
            prefixReplaceFrom = args[5];
            prefixReplaceTo = args[6];
        }
        String tiffValidationScriptPath = DEFAULT_TIFF_VALIDATION_PATH;
        if(args.length > 7) {
            tiffValidationScriptPath = args[7];
        }

        Configuration conf = getConfiguration(confPath);
        
        File outputDir = getOutputDir(outputDirPath);
        
        File recordListFile = getRecordListFile(recordListPath);
        
        EmagasinRetriever arcRetriever = getArcRetriever(retrieveArcScriptPath, outputDir);
        
        InputFormat inFormat = new InputFormat(catalogName, recordListFile);
        OutputFormatter outFormat = new OutputFormatter(outputDir);

        Cumulus.CumulusStart();
        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
        try {
            EmagImportation converter= new EmagImportation(conf, cumulusServer, catalogName);
//            EmagasinRetriever retriever = new EmagasinRetriever(script, outputDir)
            BufferedReader arcFileListReader = new BufferedReader(new InputStreamReader(new FileInputStream(recordListFile)));
            
//            converter.
        } catch (IOException e) {
            e.printStackTrace();
            // Terminate after this!
        } finally {
            System.out.println("Finished!");
            Cumulus.CumulusStop();
        }
    }
    
    /**
     * Write the argument requirements, and then exit.
     */
    protected static void argumentErrorExit() {
        System.err.println("Missing arguments. Requires the following arguments:");
        System.err.println("1. Configuration file");
        System.err.println("2. The CSV file with the ARC-filename, ARC-record-guid and Cumulus-record-guid.");
        System.err.println("3. Name of the catalog with the Cumulus record corresponding to the digital-objects in the ARC files.");
        System.err.println("4. path to ARC-file retrieval script");
        System.err.println("5. [OPITONAL] output directory");
        System.err.println("  - Default '.'");
        System.err.println("6. [OPTIONAL] prefix to be replace");
        System.err.println("  - Default is no prefix (use '-')");
        System.err.println("7. [OPTIONAL] prefix to replace with");
        System.err.println("  - Default is no prefix (use '-')");
        System.err.println("8. [OPTIONAL] path to TIFF validation script");
        System.err.println("  - Default is 'bin/run_checkit_tiff.sh'");
        System.exit(-1);
    }
    
    /**
     * Retrieve the file with the record list.
     * @param recordListPath The path to the file.
     * @return The record list file.
     */
    protected static File getRecordListFile(String recordListPath) {
        File recordListFile = new File(recordListPath);
        if(!recordListFile.isFile()) {
            System.err.println("Cannot find the record list file '" + recordListFile.getAbsolutePath() + ".");
            argumentErrorExit();
        }
        return recordListFile;
    }
    
    /**
     * Retrieves the output directory.
     * @param outputDirPath The path to the output directory.
     * @return The output directory.
     */
    protected static File getOutputDir(String outputDirPath) {
        File outputDir = new File(outputDirPath);
        if(!outputDir.isDirectory()) {
            System.err.println("Invalid output directory '" + outputDir.getAbsolutePath() + "'");
            argumentErrorExit();
        }
        return outputDir;
    }
    
    /**
     * Creates the configuration.
     * @param configurationFilePath The path to the configuration.
     * @return The configuration.
     */
    protected static Configuration getConfiguration(String configurationFilePath) {
        File confFile = new File(configurationFilePath);
        if(!confFile.isFile()) {
            System.err.println("Cannot find the configuration file '" + confFile.getAbsolutePath() + "'.");
            argumentErrorExit();
        }
        return new Configuration(confFile);
    }
    
    /**
     * Creates the Emagasin retriever from the retrieval script and the output directory.
     * @param arcRetrievalScriptPath The path to the retrieval script.
     * @param outputDir The directory where the ARC files should be retrieved to.
     * @return The retriever.
     */
    protected static EmagasinRetriever getArcRetriever(String arcRetrievalScriptPath, File outputDir) {
        File arcRetrievalScriptFile = new File(arcRetrievalScriptPath);
        if(!arcRetrievalScriptFile.isFile()) {
            System.err.println("Cannot find the script for retrieving the arc files '" 
                    + arcRetrievalScriptFile.getAbsolutePath() + "'.");
            argumentErrorExit();
        }
        return new EmagasinRetriever(arcRetrievalScriptFile, outputDir);
    }
}
