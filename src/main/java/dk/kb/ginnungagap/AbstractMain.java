package dk.kb.ginnungagap;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagArchive;
import dk.kb.ginnungagap.archive.LocalArchive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * The common methods for the main methods.
 */
public abstract class AbstractMain {
//    /** The logger.*/
//    private static final Logger log = LoggerFactory.getLogger(AbstractMain.class);
//
//    /** Archive parameter for the local archive.*/
//    public static final String ARCHIVE_LOCAL = "local";
//    /** Archive parameter for the bitrepository archive.*/
//    public static final String ARCHIVE_BITMAG = "bitmag";
//    
//    /** The name for the transformation script for catalog structmaps.*/
//    public static final String TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP = 
//            MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP;
//    /** The name for the transformation script for default METS transformation.*/
//    public static final String TRANSFORMATION_SCRIPT_FOR_METS = 
//            MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS;
//    /** The name for the transformation script for representation METS.*/
//    public static final String TRANSFORMATION_SCRIPT_FOR_REPRESENTATION = 
//            MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION;
//    /** The name for the transformation script for KB-IDs intellectuel entity metadata.*/
//    public static final String TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY = 
//            MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY;
//    
//    /**
//     * Instantiates the Archive.
//     * If the archiveType is null or empty, then the bitmag archive is instantiated.
//     * @param archiveType The archive-type text-argument, or null/empty for bitmag archive.
//     * @param conf The configuration.
//     * @return The archive.
//     */
//    protected static Archive instantiateArchive(String archiveType, Configuration conf) {
//        if(archiveType == null || archiveType.isEmpty()) {
//            return new BitmagArchive(conf.getBitmagConf());
//        }
//        
//        if(!archiveType.equalsIgnoreCase(ARCHIVE_LOCAL) && !archiveType.equalsIgnoreCase(ARCHIVE_BITMAG)) {
//            throw new ArgumentCheck("Unable to comply with archive type '" + archiveType 
//                    + "'. Only accepts '" + ARCHIVE_LOCAL + "' or '" + ARCHIVE_BITMAG + "'.");
//        }
//
//        if(archiveType.equalsIgnoreCase(ARCHIVE_LOCAL)) {
//            log.debug("Instantiating local archive.");
//            return new LocalArchive(LocalArchive.DEFAULT_PATH);
//        } else {
//            log.debug("Using Bitrepository as archive");
//            return new BitmagArchive(conf.getBitmagConf());
//        }
//    }
//    
//    /**
//     * Instantiates the configuration based on the file at the given path.
//     * @param path The path to the configuration file.
//     * @return The configuration.
//     */
//    protected static Configuration instantiateConfiguration(String path) {
//        ArgumentCheck.checkNotNullOrEmpty(path, "String path");
//        
//        return new Configuration(path);
//    }
//    
//    /**
//     * Retrieves the metadata transformer.
//     * @param conf The configuration.
//     * @param name The name of the transformation script.
//     * @return The metadata transformer.
//     */
//    protected static MetadataTransformer instantiateTransformer(Configuration conf, String name) {
//        File xsltFile = new File(conf.getTransformationConf().getXsltDir(), name);
//        return new MetadataTransformer(xsltFile);
//    }
//    
//    /**
//     * Instantiates the transformation handler, and ensures that the given transformations are present.
//     * @param conf The configuration.
//     * @param transformationNames The names of the different required transformations.
//     * @return The transformation handler with the transformations.
//     */
//    protected static MetadataTransformationHandler instantiateTransformationHandler(Configuration conf, 
//            String ... transformationNames) {
//        MetadataTransformationHandler res = new MetadataTransformationHandler(
//                conf.getTransformationConf().getXsltDir());
//        
//        for(String name : transformationNames) {
//            res.getTransformer(name);
//        }
//        
//        return res;
//    }
//    
//    /**
//     * Checks whether or not the given value is a 'YES'.
//     * If null or empty, then it is not yes.
//     * It is only a yes, if it starts with 'y' or 'Y'.
//     * It will throw an exception, if the argument starts with something else than y or n.
//     * @param arg The argument.
//     * @return Either true or false.
//     */
//    protected static boolean isYes(String arg) {
//        if(arg == null || arg.isEmpty()) {
//            return false;
//        }
//        if(arg.startsWith("y") || arg.startsWith("Y")) {
//            return true;
//        }
//        if(arg.startsWith("n") || arg.startsWith("N")) {
//            return false;
//        }
//        throw new ArgumentCheck("Could not determine whether yes or no for argument: " + arg);
//    }
//    
//    /**
//     * Check that the configuration has the given catalog.
//     * @param conf The configuration.
//     * @param catalogName The name of the catalog, which must exist in the configuration.
//     */
//    protected static void checkCatalogInConfiguration(Configuration conf, String catalogName) {
//        if(!conf.getCumulusConf().getCatalogs().contains(catalogName)) {
//            throw new ArgumentCheck("The catalog name '" + catalogName + "' must be contained in the list of "
//                    + "catalogs in the configuration.");
//        }
//    }
}
