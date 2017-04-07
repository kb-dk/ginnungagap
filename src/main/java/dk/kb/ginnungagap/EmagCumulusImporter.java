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
            EmagImportation converter= new EmagImportation(conf, cumulusServer, catalogName);
//            EmagasinRetriever retriever = new EmagasinRetriever(script, outputDir)
            BufferedReader arcFileListReader = new BufferedReader(new InputStreamReader(new FileInputStream(arcListFile)));
            
            String filename;
            while((filename = getNextArcFilename(arcFileListReader)) != null) {
                // TODO!!!
                
//                converter.convertArcFile(arcFile);
            }
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
     * Extracts the next valid line from the list.
     * TODO: perhaps make more tests, that the line does not contain invalid characters, etc.
     * @param reader The reader.
     * @return The next valid line, or null when no more line can be read.
     * @throws IOException If it fails to read.
     */
    protected static String getNextArcFilename(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            if(!line.isEmpty() && !line.contains(" ")) {
                return line;
            } else {
                log.warn("Could not interpret line '" + line + "'");
            }
        }
        return null;
    }
}
