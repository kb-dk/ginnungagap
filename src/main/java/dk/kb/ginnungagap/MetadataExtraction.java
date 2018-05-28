package dk.kb.ginnungagap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.ginnungagap.utils.StreamUtils;

/**
 * Class for retrieving the preserved metadata for a Cumulus record.
 * 
 * Takes the following arguments:
 * 1. Configuration file.
 * 2. ID for the Cumulus record to retrieve the metadata for.
 * 3. The name of the catalog with the Cumulus record.
 * 4. [OPTIONAL] Whether the ID is 'GUID' or 'Record Name'. Default 'GUID'.
 * 5. [OPTIONAL] The output directory path. Default is '.'.
 * 6. [OPTIONAL] Whether to retrieve the Content file/Asset reference. Default 'no'.
 * 7. [OPTIONAL] Type of archive (BITMAG / LOCAL). Default: BITMAG.
 * 
 * e.g.
 * dk.kb.ginningagap.MetadataExtraction conf/ginnungagap.yml id-1234-5678-id myCatalog
 */
public class MetadataExtraction extends AbstractMain {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(MetadataExtraction.class);
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String ... args) {
        if(args.length < 3) {
            printParametersAndExit();
        }
        String confPath = args[0];
        String id = args[1];
        String catalogName = args[2];
        boolean isGUID = true;
        String outputPath = ".";
        boolean alsoFile = false;
        String archiveType = ARCHIVE_BITMAG;
        if(args.length > 3) {
            if(args[3].toLowerCase().startsWith("guid")) {
                isGUID = true;
            } else if(args[3].toLowerCase().startsWith("record name")) {
                isGUID = false;
            } else {
                System.err.println("Bad ID type. Requires: 'GUID' or 'Record Name'");
                printParametersAndExit();
            }
        }
        try {
            if(args.length > 4 ) {
                outputPath = args[4];
            }
            if(args.length > 5) {
                alsoFile = isYes(args[5]);
            }
            if(args.length > 6) {
                archiveType = args[6];
            }
            if(args.length > 7) {
                System.out.println("Minimum 3 arguments, maximum 6 arguments; "
                        + "All the other arguments are ignored!");
            }
            File outputDir = FileUtils.getDirectory(outputPath);

            Configuration conf = instantiateConfiguration(confPath);
            checkCatalogInConfiguration(conf, catalogName);
            
            try (Archive archive = instantiateArchive(archiveType, conf);
                    CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf())) {
                CumulusRecord record = getRecord(cumulusServer, catalogName, id, isGUID);
                
                String packageName = record.getFieldValue(Constants.FieldNames.METADATA_PACKAGE_ID);
                String collectionId = record.getFieldValue(Constants.FieldNames.COLLECTION_ID);
                File warcFile = archive.getFile(packageName, collectionId);
                getCurrentMetadata(warcFile, record, outputDir);
                
                if(alsoFile) {
                    retrieveFileRecord(record, archive, outputDir);
                }
                System.out.println("Finished!");
            }
        } catch (ArgumentCheck e) {
            log.warn("Argument failure.", e);
            System.err.println("Failed: " + e.getMessage());
            printParametersAndExit();
        } catch (Exception e) {
            System.out.println("Failed!");
            throw new RuntimeException("Unexpected failure.", e);
        }
    }

    /**
     * Prints the parameters for the main class, and then exit.
     * Should only be used, when it has failed on a parameter.
     */
    protected static void printParametersAndExit() {
        System.err.println("Have the following arguments: ");
        System.err.println(" 1. Configuration file.");
        System.err.println(" 2. ID for the Cumulus record to retrieve the metadata for.");
        System.err.println(" 3. The name of the catalog with the Cumulus record.");
        System.err.println(" 4. [OPTIONAL] Whether the ID is 'GUID' or 'Record Name'. Default 'GUID'.");
        System.err.println(" 5. [OPTIONAL] The output directory path. Default is '.'.");
        System.err.println(" 6. [OPTIONAL] Whether to retrieve the Content file/Asset reference. Default 'no'.");
        System.exit(-1);
    }

    /**
     * Retrieves the requested record of the given type.
     * @param server The Cumulus Server.
     * @param catalogName The name of the Cumulus catalog with the record.
     * @param id The identifier for the record.
     * @param isGuid Wether the identifier is the GUID or the Record Name.
     * @return The record.
     */
    protected static CumulusRecord getRecord(CumulusServer server, String catalogName, String id, boolean isGuid) {
        CumulusRecord res = null;
        if(isGuid) {
            res = server.findCumulusRecord(catalogName, id);
        } else {
            res = server.findCumulusRecordByName(catalogName, id);
        }
        
        if(res == null) {
            throw new IllegalStateException("Could not extract record '" + id + "' from catalog '" + catalogName 
                    + "'.");
        }
        return res;
    }
    
    /**
     * Retrieves the metadata for the latest preservation.
     * Will both retrieve the main metadata (METS) and the identifier metatada (KBIDs), also for the
     * representation - the record is a representation.
     * @param warcFile The WARC file with the metadata records.
     * @param record The Cumulus record.
     * @param destination The directory where the metadata records must be placed.
     * @throws IOException If it fails to extract the metadata and create the output files.
     */
    protected static void getCurrentMetadata(File warcFile, CumulusRecord record, File destination) throws IOException {
        String metadataGuid = record.getFieldValue(Constants.FieldNames.METADATA_GUID);
        String ieMetadataGuid = record.getFieldValue(
                Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        
        File metadataFile = new File(destination, metadataGuid);
        FileUtils.deprecateFileIfExists(metadataFile);
        File ieFile = new File(destination, ieMetadataGuid);
        FileUtils.deprecateFileIfExists(ieFile);
        
        String repMetadataGuid = record.getFieldValueOrNull(Constants.FieldNames.REPRESENTATION_METADATA_GUID);
        String repIeMetadataGuid = record.getFieldValueOrNull(
                Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID);
        
        File repFile = null;
        if(repMetadataGuid != null) {
            repFile = new File(destination, repMetadataGuid);
            FileUtils.deprecateFileIfExists(repFile);
        }
        File repIeFile = null;
        if(repIeMetadataGuid != null) {
            repIeFile = new File(destination, repIeMetadataGuid);
            FileUtils.deprecateFileIfExists(repIeFile);
        }
        
        try (InputStream in = new FileInputStream(warcFile)) {
            WarcReader warcReader = WarcReaderFactory.getReader(in);
            for(WarcRecord warcRecord : warcReader) {
                if(warcRecord.header.warcRecordIdStr.contains(metadataGuid)) {
                    copyWarcRecordToFile(warcRecord, metadataFile);
                    System.out.println("Retrieved metadata '" + metadataFile.getAbsolutePath() + "'");
                } else if(warcRecord.header.warcRecordIdStr.contains(ieMetadataGuid)) {
                    copyWarcRecordToFile(warcRecord, ieFile);                    
                    System.out.println("Retrieved IE metadata '" + ieFile.getAbsolutePath() + "'");
                } else if(repMetadataGuid != null && 
                        warcRecord.header.warcRecordIdStr.contains(repMetadataGuid)) {
                    copyWarcRecordToFile(warcRecord, repFile);
                    System.out.println("Retrieved representation metadata '" + repFile.getAbsolutePath() + "'");
                } else if(repIeMetadataGuid != null && 
                        warcRecord.header.warcRecordIdStr.contains(repIeMetadataGuid)) {
                    copyWarcRecordToFile(warcRecord, repIeFile);
                    System.out.println("Retrieved representation ie metadata '" + repIeFile.getAbsolutePath() + "'");
                }
            }
        }
        
        if(!ieFile.exists()) {
            log.warn("Could not retrieve ie metadata record (KBIDs) '" + ieMetadataGuid + "'. The record might have "
                    + "been preserved prior to the implementation of the KBIDs.");
        }
        if(!metadataFile.exists()) {
            throw new IllegalStateException("Did not retrieve the metadata record '" + metadataGuid 
                    + "' from the warc file '" + warcFile.getName());
        }
    }
    
    /**
     * Retrieves the content file for a Cumulus record. 
     * @param record The Cumulus record.
     * @param archive The archive.
     * @param destinationDir The destination directory.
     * @throws IOException If it fails to retrieve the file.
     */
    protected static void retrieveFileRecord(CumulusRecord record, Archive archive, File destinationDir)
            throws IOException {
        String packageName = record.getFieldValue(Constants.FieldNames.RESOURCE_PACKAGE_ID);
        String collectionId = record.getFieldValue(Constants.FieldNames.COLLECTION_ID);
        File warcFile = archive.getFile(packageName, collectionId);
        
        String guid = record.getUUID();
        File outputFile = new File(destinationDir, guid);
        FileUtils.deprecateFileIfExists(outputFile);
        
        try (InputStream in = new FileInputStream(warcFile)) {
            WarcReader warcReader = WarcReaderFactory.getReader(in);
            for(WarcRecord warcRecord : warcReader) {
                if(warcRecord.header.warcRecordIdStr.contains(guid)) {
                    copyWarcRecordToFile(warcRecord, outputFile);
                    System.out.println("Retrieved content file '" + outputFile.getAbsolutePath() + "'.");
                }
            }
        }
    }
    
    /**
     * Copies a WARC record content to an output file.
     * @param record The warc record.
     * @param outputFile The output file.
     * @throws IOException If it fails to copy the warc record to the output file.
     */
    protected static void copyWarcRecordToFile(WarcRecord record, File outputFile) throws IOException {
        try(OutputStream out = new FileOutputStream(outputFile)) {
            StreamUtils.copyInputStreamToOutputStream(record.getPayloadContent(), out);
        }
    }
}
