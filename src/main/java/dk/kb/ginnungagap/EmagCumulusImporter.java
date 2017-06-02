package dk.kb.ginnungagap;

import java.io.File;

import com.canto.cumulus.Cumulus;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.emagasin.EmagImportation;
import dk.kb.ginnungagap.emagasin.EmagasinRetriever;
import dk.kb.ginnungagap.emagasin.TiffValidator;
import dk.kb.ginnungagap.emagasin.importation.InputFormat;
import dk.kb.ginnungagap.emagasin.importation.OutputFormatter;

/**
 * Class for instantiating the conversion from E-magasinet, by reimporting the content-files into Cumulus again.
 * It extracts the digital-objects of the ARC-files in E-magasinet, finds the given Cumulus record and places them a the pre-ingest area.
 * 
 * The configuration must contain the 
 * 
 * This takes the following arguments:
 * 1. Configuration file
 * 2. The CSV file with the ARC-filename, ARC-record-guid, Cumulus-record-guid, and catalog-name.
 * 3. [OPITONAL] output directory
 *   - Default '.'
 * 4. [OPTIONAL] path to TIFF validation script
 *   - Default is 'bin/run_checkit_tiff.sh'
 * 5. [OPTIONAL] path to CheckIT configuration
 *   - Default is 'conf/cit_tiff.cfg'
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagConversion conf/ginnungagap.yml records-list.csv retrieve_arc_files.sh
 */
public class EmagCumulusImporter {
    /** Constant for not removing files after they have been validated.*/
    private static final boolean DO_NOT_REMOVE_FILES_AFTER_VALIDATION = false;
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath = null;
        String recordListPath = null;
        if(args.length < 2) {
            argumentErrorExit();
        } else {
            confPath = args[0];
            recordListPath = args[1];
        }
        
        String outputDirPath = ".";
        if(args.length > 2) {
            outputDirPath = args[2];
        }
        if(args.length > 3) {
            System.out.println("No validation, the scripts");
        }

        Configuration conf = getConfiguration(confPath);
        
        File outputDir = getOutputDir(outputDirPath);
        
        File recordListFile = getRecordListFile(recordListPath);
        
        EmagasinRetriever arcRetriever = getArcRetriever(conf, outputDir);
        
        InputFormat inFormat = new InputFormat(recordListFile);
        OutputFormatter outFormat = new OutputFormatter(outputDir);

        Cumulus.CumulusStart();
        CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf());
        try {
            EmagImportation converter= new EmagImportation(conf, cumulusServer, arcRetriever, inFormat, outFormat);
            converter.run();
        } catch(Exception e) {
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
        System.err.println("2. The CSV file with the ARC-filename, ARC-record-guid, Cumulus-record-guid, and catalog-name.");
        System.err.println("3. [OPITONAL] output directory");
        System.err.println("  - Default '.'");
        System.err.println("4. [OPTIONAL] path to TIFF validation script");
        System.err.println("  - Default is 'bin/run_checkit_tiff.sh'");
        System.err.println("5. [OPTIONAL] path to CheckIT configuration");
        System.err.println("  - Default is 'conf/cit_tiff.cfg'");
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
        Configuration conf = new Configuration(confFile);
        
        if(conf.getImportationConfiguration() == null) {
            System.err.println("The configuration '" + confFile.getAbsolutePath() 
                    + " does not contain the importation section.");
            argumentErrorExit();
        }
        return conf;
    }
    
    /**
     * Creates the Emagasin retriever from the retrieval script and the output directory.
     * @param conf The configuration where the script location is in the importation section.
     * @param outputDir The directory where the ARC files should be retrieved to.
     * @return The retriever.
     */
    protected static EmagasinRetriever getArcRetriever(Configuration conf, File outputDir) {
        File arcRetrievalScriptFile = conf.getImportationConfiguration().getScriptFile();
        if(!arcRetrievalScriptFile.isFile()) {
            System.err.println("Cannot find the script for retrieving the arc files '" 
                    + arcRetrievalScriptFile.getAbsolutePath() + "'.");
            argumentErrorExit();
        }
        return new EmagasinRetriever(arcRetrievalScriptFile, outputDir);
    }
    
    /**
     * Instantiates the TIFF validator.
     * @param scriptPath The path to the script for validating the TIFF files.
     * @param confPath The CheckIT configuration path.
     * @param outputDir The output directory.
     * @return The validator.
     */
    protected static TiffValidator getTiffValidator(String scriptPath, String confPath, File outputDir) {
        File script = new File(scriptPath);
        if(!script.isFile()) {
            System.err.println("Cannot find the script for validating tiff files '" + script.getAbsolutePath() + "'.");
            argumentErrorExit();
        }
        File conf = new File(confPath);
        if(!conf.isFile()) {
            System.err.println("Cannot find the configuration for validating tiff files '" + conf.getAbsolutePath() 
                    + "'.");
            argumentErrorExit();
        }
        
        return new TiffValidator(outputDir, script, conf, DO_NOT_REMOVE_FILES_AFTER_VALIDATION);
    }
}
