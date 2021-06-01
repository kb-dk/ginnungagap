package dk.kb.ginnungagap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import dk.kb.metadata.utils.GuidExtractionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.archive.ArchiveWrapper;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusPreservationUtils;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.utils.WarcUtils;

/**
 * Controller for dealing with the metadata creation/extraction.
 */
@Controller
public class MetadataController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(MetadataController.class);
    
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
     * @param idType The type of ID for the Cumulus record, either UUID or Record Name.
     * @param catalog The catalog for the Cumulus record.
     * @param metadataType The type of metadata to extract, either METS or KBIDS.
     * @param source The source for the metadata, either new created from Cumulus, or extracted from thhe archive.
     * @return The response containing the metadata file.
     */
    @RequestMapping("/metadata/extract")
    public ResponseEntity<Resource> extractMetadata(@RequestParam(value="ID",required=true) String id,
            @RequestParam(value="idType", required=false, defaultValue="UUID") String idType,
            @RequestParam(value="catalog", required=true) String catalog,
            @RequestParam(value="metadataType", required=false, defaultValue="METS") String metadataType,
            @RequestParam(value="source", required=false, defaultValue="cumulus") String source) {        
        try {
            log.info("Extracting '" + metadataType + "' metadata for '" + id + "' from catalog '" + catalog + "'.");
            String filename = id + ".xml";
            File metadataFile;

            CumulusRecord record = null;
            if(idType.equalsIgnoreCase("uuid")) {
                record = cumulusWrapper.getServer().findCumulusRecord(catalog, id);
            } else {
                record = cumulusWrapper.getServer().findCumulusRecordByName(catalog, id);            
            }
            if(record == null) {
                throw new IllegalStateException("No Cumulus record for '" + id + "' at catalog '" + catalog 
                        + "' can be found!");
            }
            
            validateRecord(record);
            
            if(source.equalsIgnoreCase("archive")) {
                metadataFile = getArchivedMetadata(filename, metadataType, record);
            } else {
                metadataFile = getCumulusTransformedMetadata(filename, metadataType, record);
            }
            Resource resource = new UrlResource(metadataFile.toURI());
            Thread.sleep(100000); //FIXME: Implement as asynchronous call, DeferredResult
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.warn("Failed to retrieve metadata", e);
            throw new IllegalStateException("Failed to extract metadata", e);
        }
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
}
