package dk.kb.ginnungagap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import dk.kb.ginnungagap.convert.prevalidation.ImportPrevalidator;
import dk.kb.ginnungagap.utils.FileUtils;

/**
 * Class for instantiating the prevalidation of the content from E-magasinet.
 * It compares a list of data in the archive with a list from Cumulus about which data is expected in the archive.
 * It starts by sorting both lists.
 * 
 * It also takes the argument of the output directory.
 * 
 * Run as commmand, e.g. :
 * dk.kb.ginningagap.EmagPreValidator outputDir cumulusExtract.txt emagasinExtract.txt
 */
public class EmagPreValidator {

    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * Requires 3 arguments, as described in the class definition.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String outputDirPath = null;
        String cumulusExtractPath = null;
        String emagasinExstractPath = null;
        if(args.length < 3) {
            System.err.println("Missing arguments. Requires the following arguments:");
            System.err.println("  1. Output directory.");
            System.err.println("  2. Path to Cumulus extract file.");
            System.err.println("  3. Path to Emagasin extract file.");
            System.exit(-1);
        } else {
            outputDirPath = args[0];
            cumulusExtractPath = args[1];
            emagasinExstractPath = args[2];
        }

        File outputDir = FileUtils.getDirectory(outputDirPath);
        
        File cumulusExtract = new File(cumulusExtractPath);
        if(!cumulusExtract.isFile()) {
            System.err.println("Cannot find the Cumulus extract file '" + cumulusExtract.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        File emagasinExtract = new File(emagasinExstractPath);
        if(!emagasinExtract.isFile()) {
            System.err.println("Cannot find the Emagasin extract file '" + emagasinExtract.getAbsolutePath() + ".");
            System.exit(-1);
        }

        try {
            ImportPrevalidator prevalidator = new ImportPrevalidator(outputDir);
            BufferedReader cumulusReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(cumulusExtract)));
            BufferedReader emagasinReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(emagasinExtract)));
            prevalidator.compare(emagasinReader, cumulusReader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to prevalidate!", e);
        }
    }    
}
