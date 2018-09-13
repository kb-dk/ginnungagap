package dk.kb.ginnungagap.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusWrapper;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * Controller for dealing with the preservation of the catalog structmap.
 */
@Controller
public class CatalogController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(CatalogController.class);
    
    /** The configuration.*/
    @Autowired
    protected Configuration conf;
    /** The wrapped cumulus client.*/
    @Autowired
    protected CumulusWrapper cumulusWrapper;
    /** The metadata transformer.*/
    @Autowired
    protected MetadataTransformationHandler metadataTransformer;
    /** The bitrepository preserver.*/
    @Autowired
    protected BitmagPreserver preserver;
    
    /**
     * Catalog view controller.
     * @param model The model.
     * @return The catalog view.
     */
    @RequestMapping("/catalog")
    public String getCatalog(Model model) {
        model.addAttribute("catalogs", conf.getCumulusConf().getCatalogs());
        return "catalog";
    }
    
    /**
     * The method for extracting metadata to view for the user.
     * Not preservation.
     * @param catalog The catalog to have created it's METS for.
     * @param ieID The ID of the record to extract metadata for.
     * @param allowSubSet Whether or not to allow the subset of the Cumulus records, or if it should fail when
     * it comes across a Cumulus record that does not meet the prerequisites.
     * @return The response containing the metadata file.
     */
    @RequestMapping("/catalog/extract")
    public ResponseEntity<Resource> extractMetadata(@RequestParam(value="catalog", required=true) String catalog,
            @RequestParam(value="ieID", required=true, defaultValue="") String ieID,
            @RequestParam(value="allowSubSet", required=true) String allowSubSet) {        
        String intellectualEntityID = getIntellectualID(ieID);
        
        File structmapFile = extractStructmap(intellectualEntityID, catalog, allowSubSet);
        
        try {
            log.info("Sending extracted catalog structmap metadata for catalog '" + catalog + "'.");
            Resource resource = new UrlResource(structmapFile.toURI());
            
            String filename = intellectualEntityID + ".xml";
            
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
     * The method for preserving the metadata.
     * @param ieID The ID of the record to extract metadata for.
     * @param catalog The catalog to have created it's METS for.
     * @param collectionID The ID of the Bitrepository collection, where the data will be preserved.
     * @param allowSubSet Whether or not to allow the subset of the Cumulus records, or if it should fail when
     * it comes across a Cumulus record that does not meet the prerequisites.
     * @return Redirect to the catalog view.
     */
    @RequestMapping("/catalog/preserve")
    public RedirectView preserveMetadata(@RequestParam(value="catalog", required=true) String catalog,
            @RequestParam(value="ieID", required=true, defaultValue="") String ieID,
            @RequestParam(value="collectionID", required=true) String collectionID,
            @RequestParam(value="allowSubSet", required=true) String allowSubSet) {        
        String intellectualEntityID = getIntellectualID(ieID);
        
        File structmapFile = extractStructmap(intellectualEntityID, catalog, allowSubSet);
        
        log.info("Packing and preserving the catalog structmap metadata for catalog '" + catalog + "'.");
        
        preserver.packRepresentationMetadata(structmapFile, collectionID);
        preserver.uploadAll();
        
        return new RedirectView("../catalog",true);
    }
    
    /**
     * Extract the IntellectualEntityID from the argument, or create a new UUID if it is null or empty.
     * @param ieID The given intellectual entity id from user argument.
     * @return The intellectual entity id.
     */
    protected String getIntellectualID(String ieID) {
        if(ieID == null || ieID.isEmpty()) {
            return UUID.randomUUID().toString();
        } else {
            return ieID;
        }
    }
    
    /**
     * Extracts a METS with the structmap for all the files in the catalog.
     * @param intellectualEntityID The intellectual entity id for the catalog.
     * @param catalog The name of the catalog.
     * @param allowSubSet The String argument for whether or not a subset of the records are allowed.
     * @return The file with the structmap.
     */
    protected File extractStructmap(String intellectualEntityID, String catalog, String allowSubSet) {
        File structmapFile = new File(conf.getTransformationConf().getMetadataTempDir(), intellectualEntityID);
        
        boolean allowMissingRecords = Boolean.parseBoolean(allowSubSet);
        boolean allRecords = false;
        try {
            String uuid = UUID.randomUUID().toString();
            File extractFile = new File(conf.getTransformationConf().getMetadataTempDir(), uuid + ".xml");
            allRecords = extractGuidsAndFileIDsForCatalog(catalog, intellectualEntityID, uuid, extractFile );
            MetadataTransformer transformer =  metadataTransformer.getTransformer(
                    MetadataTransformationHandler.TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP);
            try (InputStream in = new FileInputStream(extractFile);
                    OutputStream out = new FileOutputStream(structmapFile)) {
                transformer.transformXmlMetadata(in, out);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not extract Cumulus", e);
        }
        
        if(!(allRecords || allowMissingRecords)) {
            throw new IllegalStateException("Could not extract all the records. "
                    + "Some where missing their intellectual entity ID.");
        }
        return structmapFile;
    }
    
    /**
     * Extract the GUID and record name for all the records in the given catalog.
     * They will be put into a file, formatted in XML in the following way for each record:
     * <br/>
     * &lt;record&gt;<br/>
     * &nbsp;&nbsp;&lt;guid&gt;GUID&lt;/guid&gt;<br/>
     * &nbsp;&nbsp;&lt;name&gt;RECORD NAME&lt;/name&gt;<br/>
     * &lt;/record&gt;<br/>
     * 
     * Any Cumulus record in the catalog without an intellectual entity will not be extracted.
     * 
     * @param catalogName The name of the catalog to extract.
     * @param intellectualEntityID The intellectual entity ID of the catalog.
     * @param uuid The UUID of the output file. 
     * @param outputFile The file where the XML with the GUID and Record name for all records in the Cumulus catalog
     * will be written.
     * @return Whether or not all records of the catalog was in the output file.
     * @throws IOException If it fails to write the XML file.
     */
    protected boolean extractGuidsAndFileIDsForCatalog(String catalogName, String intellectualEntityID, String uuid,
            File outputFile) throws IOException {
        CumulusQuery query = CumulusQuery.getQueryForAllInCatalog(catalogName);
        CumulusRecordCollection items = cumulusWrapper.getServer().getItems(catalogName, query);
        
        try (FileOutputStream os = new FileOutputStream(outputFile)) {
            boolean allRecords = true;
            os.write("<catalog>\n".getBytes(StandardCharsets.UTF_8));
            os.write("  <uuid>".getBytes(StandardCharsets.UTF_8));
            os.write(uuid.getBytes(StandardCharsets.UTF_8));
            os.write("</uuid>\n".getBytes(StandardCharsets.UTF_8));
            os.write("  <ie_uuid>".getBytes(StandardCharsets.UTF_8));
            os.write(intellectualEntityID.getBytes(StandardCharsets.UTF_8));
            os.write("</ie_uuid>\n".getBytes(StandardCharsets.UTF_8));
            os.write("  <catalogName>".getBytes(StandardCharsets.UTF_8));
            os.write(catalogName.getBytes(StandardCharsets.UTF_8));
            os.write("</catalogName>\n".getBytes(StandardCharsets.UTF_8));
            for(CumulusRecord record : items) {
                String recordName = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
                String recordIntellectualEntity = record.getFieldValueOrNull(
                        Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
                if(recordIntellectualEntity == null || recordIntellectualEntity.isEmpty()) {
                    allRecords = false;
                    log.warn("Failed extracing intellectual entity for record: " + recordName);
                    continue;
                }
                os.write("  <record>\n".getBytes(StandardCharsets.UTF_8));
                os.write("    <guid>".getBytes(StandardCharsets.UTF_8));
                os.write(recordIntellectualEntity.getBytes(StandardCharsets.UTF_8));
                os.write("</guid>\n".getBytes(StandardCharsets.UTF_8));
                os.write("    <name>".getBytes(StandardCharsets.UTF_8));
                os.write(recordName.getBytes(StandardCharsets.UTF_8));
                os.write("</name>\n".getBytes(StandardCharsets.UTF_8));
                os.write("  </record>\n".getBytes(StandardCharsets.UTF_8));
            }
            os.write("</catalog>\n".getBytes(StandardCharsets.UTF_8));
            
            return allRecords;
        }
    }
}
