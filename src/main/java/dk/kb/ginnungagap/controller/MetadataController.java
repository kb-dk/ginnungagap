package dk.kb.ginnungagap.controller;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusPreservationUtils;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.WarcUtils;
import dk.kb.metadata.utils.GuidExtractionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dk.kb.ginnungagap.utils.FileUtils.*;
import static dk.kb.ginnungagap.utils.StringUtils.isNullOrEmpty;


/**
 * Controller for dealing with the metadata creation/extraction.
 */
@Controller
public class MetadataController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(MetadataController.class);
    static final String ZIP = ".zip";

    String uploadFile = "Dummy";

    String inputFilePath = "/usr/local/ginnungagap/tempDir/";

    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The wrapped cumulus client.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The metadata transformer.*/
    @Autowired
    protected MetadataTransformationHandler metadataTransformer;
    /** The wrapped archive.*/
    @Autowired
    protected ArchiveWrapper archiveWrapper;
    
    /**
     * The metadata controller view.
     * @param model The model for the view.
     * @return The metadata view.
     */
    @RequestMapping("/metadata")
    public String getMetadata(Model model) {
        model.addAttribute("catalogs", conf.getCumulusConf().getCatalogs());
        return "metadata";
    }
    
    /**
     * The method for extracting metadata.
     * @param id The ID(s) of the record to extract metadata for.
     * @param idType The type of ID for the Cumulus record, either UUID or Record Name.
     * @param catalog The catalog for the Cumulus record.
     * @param metadataType The type of metadata to extract, either METS or KBIDS.
     * @param source The source for the metadata, either new created from Cumulus, or extracted from thhe archive.
     * @return The response containing the metadata file.
     */
    @SuppressWarnings("JvmTaintAnalysis")
    @RequestMapping("/metadata/extract")
    public DeferredResult <ResponseEntity<Resource>> extractMetadata(
            @RequestParam(value="ID",required=false) String id,
            @RequestParam(value="idType", required=false, defaultValue="UUID") String idType,
            @RequestParam(value="catalog", required=true) String catalog,
            @RequestParam(value="metadataType", required=false, defaultValue="METS") String metadataType,
            @RequestParam(value="source", required=false, defaultValue="cumulus") String source) {
        try {
            DeferredResult<ResponseEntity<Resource>> output = new DeferredResult<>(180000L);
            String filename;
            File metadataFile;
            CumulusRecord record;
            String targetDir = UUID.randomUUID().toString();
            String targetPath = conf.getTransformationConf().getMetadataTempDir() + "/" + targetDir;
            new File(targetPath).mkdir();
            File zippedXmls;
            String[] fileList;
            Path path = Paths.get(inputFilePath + uploadFile);

            if(isNullOrEmpty(id)) {
                fileList = getFileListFromFile(path);
            } else {
                fileList = id.split("\\s*,\\s*");
            }
            List<File> srcFiles = new ArrayList<>();
            for (String fid : fileList) {
                filename = fid + ".xml";
                log.info("Extracting '" + metadataType + "' metadata for '" + fid + "' from catalog '" + catalog + "'");
                try {
                    record = getCumulusRecord(fid, idType, catalog);
                    validateRecord(record);
                    if(source.equalsIgnoreCase("archive")) {
                        metadataFile = getArchivedMetadata(filename, metadataType, record, targetDir);
                        String data = FileUtils.readFileToString(metadataFile, "UTF-8");
                        log.trace("Contents from archive: \n" + data);
                    } else {
                        metadataFile = getCumulusTransformedMetadata(filename, metadataType, record, targetPath);
                        String data = FileUtils.readFileToString(metadataFile, "UTF-8");
                        log.trace("Contents from Cumulus: \n" + data);
                    }
                } catch (Exception e) {
                    String errorRecordName = targetPath + "/" + "Error_" + fid + ".txt";
                    metadataFile = createFileWithText(errorRecordName, e.toString());
                }
                srcFiles.add(metadataFile);
            }
            zippedXmls = addToZip(srcFiles, targetPath);

            output.onTimeout(() -> output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout")));
            output.onCompletion(() -> log.trace("Process getting metadata complete"));

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(zippedXmls.toPath()));
            deleteDir(new File(targetPath));

            output.setResult(ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/zip"))     //.contentType(MediaType.TEXT_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zippedXmls.getName() + "\"")
                    .body(resource));
            return output;
        } catch (Exception e) {
            log.warn("Failed to retrieve metadata", e);
            throw new IllegalStateException("Failed to extract metadata", e);
        }
    }

    /**
     * Upload a file containing a list of file IDs to extract metadata for
     * @param file The uploaded file
     * @param model Needed to add the 'catalogs' list to Metadata view
     * @return return the Metadata view to enable setting 'ID type', 'catalog' etc
     */
    @RequestMapping(value = "/metadata/upload", headers = "content-type=multipart/form-data", method = RequestMethod.POST)
    public String submit(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            try {
                uploadFile = file.getOriginalFilename();
                file.transferTo(new File(inputFilePath + uploadFile));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        model.addAttribute("catalogs", conf.getCumulusConf().getCatalogs());
        return "metadata";
    }

    /**
     * Help method to read a file line by line and return the result in a String array
     * @param path Path to the file to read
     * @return String array with a list of files
     */
    private String[] getFileListFromFile(Path path) {
        String[] fList;
        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(path)) {
            list = stream
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Something went wrong reading file to list", e);
        }
        if(list.size() > 250){
            throw new IllegalStateException("Max file size is 250 lines");
        }
        fList = list.toArray(new String[0]);

        return fList;
    }

    /**
     * @param srcFiles The list of files
     * @param targetPath Where to put the file
     * @return The updated zip-file
     */
    private File addToZip(List<File> srcFiles, String targetPath) {
        try (FileOutputStream fos = new FileOutputStream(targetPath + "/metadata" + ZIP);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (File srcFile : srcFiles) {
                try( BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(srcFile.toPath()))) {
                    ZipEntry zipEntry = new ZipEntry(srcFile.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[8192];
                    int length;
                    while ((length = bis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    zipOut.closeEntry();
                } catch (IOException e) {
                    log.debug("Error processing file: " + srcFile.getName());
                    throw new IllegalStateException("Failed adding to zip", e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed adding to zip", e);
        }

        return new File(Paths.get(targetPath, "metadata" + ZIP).toString());
    }

    /**
     * Validates that the record is at least ready for preservation.
     * @param record The record.
     */
    protected void validateRecord(CumulusRecord record) {
        CumulusPreservationUtils.initIntellectualEntityUUID(record);
        
        String uuid = record.getFieldValueOrNull(Constants.FieldNames.METADATA_GUID);
        if(uuid == null || uuid.isEmpty()) {
            record.setStringValueInField(Constants.FieldNames.METADATA_GUID, 
                    UUID.randomUUID().toString());
        }
        
        record.validateFieldsExists(conf.getTransformationConf().getRequiredFields().getWritableFields());
        record.validateFieldsHasValue(conf.getTransformationConf().getRequiredFields().getBaseFields());
    }
    
    /**
     * Retrieves the metadata file from the archive. 
     * @param filename The name for the file containing the metadata.
     * @param metadataType The type of metadata, METS or KBIDS.
     * @param record The Cumulus record with information about which WARC file the metadata is in, and the name 
     * of the WARC record containing the type of metadata.
     * @return The extracted metadata file.
     * @throws Exception If it fails to extract the metadata from the archive.
     */
    protected File getArchivedMetadata(String filename, String metadataType, CumulusRecord record, String targetDir)
            throws Exception {
        String warcId = record.getFieldValue(Constants.FieldNames.METADATA_PACKAGE_ID);
        String collectionId = record.getFieldValue(Constants.FieldNames.COLLECTION_ID);
        File warcFile = archiveWrapper.getFile(warcId, collectionId);
        
        File outputFile = new File(conf.getBitmagConf().getTempDir() + "/" + targetDir, filename);
        String recordId;
        if(metadataType.equalsIgnoreCase("KBIDS")) {
            recordId = GuidExtractionUtils.extractGuid(record.getFieldValue(
                    Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        } else {
            if(!metadataType.equalsIgnoreCase("METS")) {
                log.warn("Undecypherable metadata type '" + metadataType + "'. Deliver METS.");
            }
            recordId = CumulusPreservationUtils.getMetadataUUID(record);
        }
        
        WarcUtils.extractRecord(warcFile, recordId, outputFile);
        log.trace("Metadata file extracted.");
        return outputFile;
    }
    
    /**
     * Extract new metadata based on the Cumulus record.
     * @param filename The name of the file to output the metadata.
     * @param metadataType The type of metadata to create.
     * @param record The Cumulus record containing the metadata to extract.
     * @return The file with the metadata.
     * @throws Exception If it fails to extract or transform the metadata.
     */
    protected File getCumulusTransformedMetadata(String filename, String metadataType, CumulusRecord record, String targetPath)
            throws Exception {
        File metadataFile = new File(targetPath, filename);
        if(metadataType.equalsIgnoreCase("KBIDS")) {
            createKbidsMetadata(record, metadataFile, targetPath);
        } else {
            if(!metadataType.equalsIgnoreCase("METS")) {
                log.warn("Undecypherable metadata type '" + metadataType + "'. Deliver METS.");
            }
            createMetsMetadata(record, metadataFile, targetPath);
        }

        return metadataFile;
    }
    
    /**
     * Create the KBIDS metadata for the Cumulus record.
     * @param record The Cumulus record.
     * @param metadataFile The file where the metadata should be placed.
     * @throws Exception If it fails to create or transform the metadata, or writing it to the output file. 
     */
    protected void createKbidsMetadata(CumulusRecord record, File metadataFile, String targetPath) throws Exception {
        MetadataTransformer transformer = metadataTransformer.getTransformer(
                MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);

        String ieUUID = GuidExtractionUtils.extractGuid(record.getFieldValue(
                Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        String metadataUUID = CumulusPreservationUtils.getMetadataUUID(record);
        String fileUUID = record.getUUID();
        try (OutputStream os = Files.newOutputStream(metadataFile.toPath())) {
            File rawMetadataFile = new File(targetPath, metadataUUID + ".raw.xml");
            CumulusPreservationUtils.createIErawFile(ieUUID, metadataUUID, fileUUID, rawMetadataFile);
            try (InputStream cumulusIn = Files.newInputStream(rawMetadataFile.toPath())) {
                transformer.transformXmlMetadata(cumulusIn, os);
            }
            os.flush();
        }
    }
    
    /**
     * Create the METS metadata for the Cumulus record.
     * @param record The Cumulus record.
     * @param metadataFile The file where the metadata should be placed.
     * @throws Exception If it fails to create or transform the metadata, or writing it to the output file. 
     */
    protected void createMetsMetadata(CumulusRecord record, File metadataFile, String targetPath)
            throws Exception {
        MetadataTransformer transformer = metadataTransformer.getTransformer(
                MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        String metadataUUID = CumulusPreservationUtils.getMetadataUUID(record);
        if(record.isMasterAsset()) {
            metadataUUID = record.getFieldValue(Constants.FieldNames.REPRESENTATION_METADATA_GUID);
            transformer = metadataTransformer.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION);
        }
        try (OutputStream os = Files.newOutputStream(metadataFile.toPath())) {
            File cumulusMetadataFile = new File(targetPath, metadataUUID + ".raw.xml");
            Path cumulusMetadataFilePath = cumulusMetadataFile.toPath();
            try (OutputStream cumulusOut = Files.newOutputStream(cumulusMetadataFilePath)) {
                record.writeFieldMetadata(cumulusOut);
            }
            try (InputStream cumulusIn = Files.newInputStream(cumulusMetadataFilePath)) {
                transformer.transformXmlMetadata(cumulusIn, os);
            } catch (Exception e){
                log.error("Error in XML transformation: ", e);
            }
            os.flush();
        }
    }

    /**
     * Extract a Cumulus record
     * @param id The id of the record
     * @param idType The type of id (UUID or file name)
     * @param catalog The Cumulus Catalog
     * @return The Cumulus record
     */
    private CumulusRecord getCumulusRecord(String id, String idType, String catalog) {
        CumulusRecord record;
        if(idType.equalsIgnoreCase("uuid")) {
            record = cumulusWrapper.getServer().findCumulusRecord(catalog, id);
        } else {
            record = cumulusWrapper.getServer().findCumulusRecordByName(catalog, id);
        }
        if(record == null) {
            throw new IllegalStateException("No Cumulus record for '" + id + "' at catalog '" + catalog
                    + "' can be found!");
        }
        return record;
    }
}
