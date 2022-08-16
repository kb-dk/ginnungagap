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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Controller for dealing with the metadata creation/extraction.
 */
@Controller
public class MetadataController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(MetadataController.class);
    // todo: make configurable
    static final String ZIP = ".zip";

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
     * @param id The ID of the record to extract metadata for.
//     * @param ids The comma separated list of IDs to extract metadata for.
     * @param idType The type of ID for the Cumulus record, either UUID or Record Name.
     * @param catalog The catalog for the Cumulus record.
     * @param metadataType The type of metadata to extract, either METS or KBIDS.
     * @param source The source for the metadata, either new created from Cumulus, or extracted from thhe archive.
     * @return The response containing the metadata file.
     */
    @RequestMapping("/metadata/extract")
    public DeferredResult <ResponseEntity<Resource>> extractMetadata(
            @RequestParam(value="ID",required=false) String id,
            @RequestParam(value="idType", required=false, defaultValue="UUID") String idType,
            @RequestParam(value="catalog", required=true) String catalog,
            @RequestParam(value="metadataType", required=false, defaultValue="METS") String metadataType,
            @RequestParam(value="source", required=false, defaultValue="cumulus") String source) {
        try {
            DeferredResult<ResponseEntity<Resource>> output = new DeferredResult<>(180000L);
            String filename = null;
            File metadataFile;
            CumulusRecord record = null;
            File zippedXmls = null;
//            File f = new File(conf.getTransformationConf().getMetadataTempDir()+"filename");
//            if(f.exists()) {
//            List<List<String>> rcds = new ArrayList<>();
//                try (BufferedReader br = new BufferedReader(new FileReader(conf.getTransformationConf().getMetadataTempDir()+"filename"))) {
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        String[] fileList = line.split("\\s*,\\s*");
//                        rcds.add(Arrays.asList(values));
//                    }
//                }
//            } else {
//            }

            String[] fileList = id.split("\\s*,\\s*");
            List<String> srcFiles = new ArrayList<>();
            for (String fid : fileList) {
                filename = fid + ".xml";
                log.info("Extracting '" + metadataType + "' metadata for '" + fid + "' from catalog '" + catalog + "'.");
                record = getCumulusRecord(fid, idType, catalog);
                validateRecord(record);
                try {
                    metadataFile = getCumulusTransformedMetadata(filename, metadataType, record);
                    zippedXmls = addToZip(metadataFile, srcFiles);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
//                log.info("Extracting '" + metadataType + "' metadata for '" + id + "' from catalog '" + catalog + "'.");
//                filename = id + ".xml";
//                record = getCumulusRecord(id, idType, catalog);
//                validateRecord(record);
            if(source.equalsIgnoreCase("archive")) {
                metadataFile = getArchivedMetadata(filename, metadataType, record);
                String data = FileUtils.readFileToString(metadataFile, "UTF-8");
                log.trace("Contents from archive: \n" + data);
            } else {
                metadataFile = getCumulusTransformedMetadata(filename, metadataType, record);
                String data = FileUtils.readFileToString(metadataFile, "UTF-8");
                log.trace("Contents from Cumulus: \n" + data);
            }
            output.onTimeout(() -> output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Request timeout")));
            output.onCompletion(() -> log.trace("Process getting metadata complete"));

            Resource resource;
            resource = new UrlResource(zippedXmls.toURI());
            output.setResult(ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zippedXmls.getName() + "\"")
                    .body(resource));
//                resource = new UrlResource(metadataFile.toURI());
//                output.setResult(ResponseEntity.ok()
//                        .contentType(MediaType.TEXT_XML)
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                        .body(resource));
            return output;
        } catch (Exception e) {
            log.warn("Failed to retrieve metadata", e);
            throw new IllegalStateException("Failed to extract metadata", e);
        }
    }

    private File addToZip(File metadataFile, List<String> srcFiles) throws IOException {
        srcFiles.add(metadataFile.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(conf.getTransformationConf().getMetadataTempDir() + ZIP);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
        return new File(conf.getTransformationConf().getMetadataTempDir() + ZIP);
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
    protected File getArchivedMetadata(String filename, String metadataType, CumulusRecord record) 
            throws Exception {
        String warcId = record.getFieldValue(Constants.FieldNames.METADATA_PACKAGE_ID);
        String collectionId = record.getFieldValue(Constants.FieldNames.COLLECTION_ID);
        File warcFile = archiveWrapper.getFile(warcId, collectionId);
        
        File outputFile = new File(conf.getBitmagConf().getTempDir(), filename);
        String recordId = null;
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
    protected File getCumulusTransformedMetadata(String filename, String metadataType, CumulusRecord record) 
            throws Exception {
        File metadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), filename);
        if(metadataType.equalsIgnoreCase("KBIDS")) {
            createKbidsMetadata(record, metadataFile);
        } else {
            if(!metadataType.equalsIgnoreCase("METS")) {
                log.warn("Undecypherable metadata type '" + metadataType + "'. Deliver METS.");
            }
            createMetsMetadata(record, metadataFile);
        }
        
        return metadataFile;
    }
    
    /**
     * Create the KBIDS metadata for the Cumulus record.
     * @param record The Cumulus record.
     * @param metadataFile The file where the metadata should be placed.
     * @throws Exception If it fails to create or transform the metadata, or writing it to the output file. 
     */
    protected void createKbidsMetadata(CumulusRecord record, File metadataFile) throws Exception {
        MetadataTransformer transformer = metadataTransformer.getTransformer(
                MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);

        String ieUUID = GuidExtractionUtils.extractGuid(record.getFieldValue(
                Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY));
        String metadataUUID = CumulusPreservationUtils.getMetadataUUID(record);
        String fileUUID = record.getUUID();
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File rawMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), metadataUUID 
                    + ".raw.xml");
            CumulusPreservationUtils.createIErawFile(ieUUID, metadataUUID, fileUUID, rawMetadataFile);
            try (InputStream cumulusIn = new FileInputStream(rawMetadataFile)) {
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
    protected void createMetsMetadata(CumulusRecord record, File metadataFile) 
            throws Exception {
        MetadataTransformer transformer = metadataTransformer.getTransformer(
                MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_METS);

        String metadataUUID = CumulusPreservationUtils.getMetadataUUID(record);
        if(record.isMasterAsset()) {
            metadataUUID = record.getFieldValue(Constants.FieldNames.REPRESENTATION_METADATA_GUID);
            transformer = metadataTransformer.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_REPRESENTATION);
        }
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), metadataUUID 
                    + ".raw.xml");
            try (OutputStream cumulusOut = new FileOutputStream(cumulusMetadataFile)) {
                record.writeFieldMetadata(cumulusOut);
            }
            try (InputStream cumulusIn = new FileInputStream(cumulusMetadataFile)) {
                transformer.transformXmlMetadata(cumulusIn, os);
            }
            os.flush();
        }
    }

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


//    @RequestMapping(value = "/metadata/upload", headers = "content-type=multipart/*", method = RequestMethod.POST)
//    public ResponseEntity uploadToLocalFileSystem(@RequestParam("file") MultipartFile file) {
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        Path path = Paths.get(inputFilePath + fileName);
//        try {
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/files/download/")
//                .path(fileName)
//                .toUriString();
//        return ResponseEntity.ok(fileDownloadUri);

//    public void handleUpload(@RequestParam("file") MultipartFile file)  {
//
//        if (!file.isEmpty()) {
//            try {
//                file.transferTo(new File(inputFilePath));
//            } catch (IOException e) {
//                throw new IllegalStateException(e);
//            }
//        }

//        File metadataFile;
//        String filename;
//        String metadataType = "METS";
//        String idType = "NAME";
//        String catalog = "Samlingsbilleder";
//        Path inputFileP = Paths.get(inputFilePath);
//        List<String> fileList = Arrays.asList(str.split("\\s*,\\s*"));
//
//
//        try (Stream<String> lines = Files.lines(inputFileP)) {
//            fileList = lines.collect(Collectors.toList());
//        }
//        catch (IOException e){
//            log.info("File was not read to list", e);
//        }
//        if(!(fileList == null)){
//            for (String id:fileList){
//                filename = id + ".xml";
//                log.info("Extracting '" + metadataType + "' metadata for '" + id + "' from catalog '" + catalog + "'.");
//                CumulusRecord record = getCumulusRecord(id, idType, catalog);
//                validateRecord(record);
//                try {
//                    metadataFile = getCumulusTransformedMetadata(filename, metadataType, record);
//                    String data = FileUtils.readFileToString(metadataFile, "UTF-8");
//                    log.trace("Contents from Cumulus: \n" + data);
//                } catch (Exception e) {
//                    throw new IllegalStateException(e);
//                }
//                deleteFile(inputFileP.toFile());
//            }
//        }
//    }
}
