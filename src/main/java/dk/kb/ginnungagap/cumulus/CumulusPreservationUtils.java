package dk.kb.ginnungagap.cumulus;

import java.io.File;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jwat.warc.WarcDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.StringUtils;

/**
 * Class containing all the preservation methods regarding Cumulus records.
 */
public class CumulusPreservationUtils {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CumulusPreservationUtils.class);

    /**
     * Initializes the Cumulus record for preservation.
     * @param record The Cumulus record to be initialized.
     */
    public static void initialiseRecordForPreservation(CumulusRecord record) {
        initIntellectualEntityUUID(record);
        initRecordChecksum(record);
        
        resetMetadataGuid(record);
    }
    
    /**
     * Initializes the representation part of the Cumulus record for preservation.
     * @param record The Cumulus record to have its representaiton initialized.
     */
    public static void initializeRecordRepresentaitonForPreservation(CumulusRecord record) {
        initRepresentationIntellectualEntityUUID(record);
        resetRepresentationMetadataGuid(record);
    }
    
    /**
     * Sets a new Metadata GUID for the record.
     */
    protected static void resetMetadataGuid(CumulusRecord record) {
        String metadataGuid = UUID.randomUUID().toString();
        record.setStringValueInField(Constants.FieldNames.METADATA_GUID, metadataGuid);
    }

    /**
     * Sets a new metadata GUID for the representation of this record.
     */
    protected static void resetRepresentationMetadataGuid(CumulusRecord record) {
        String representationMetadataGuid = UUID.randomUUID().toString();
        record.setStringValueInField(Constants.FieldNames.REPRESENTATION_METADATA_GUID, 
                representationMetadataGuid);
    }
    
    /**
     * Initializes the intellectual entity UUID for the representation of this record.
     * If it already exists, then it is 
     * @param record The record to have its intellectual entity UUID initialised.
     */
    protected static void initRepresentationIntellectualEntityUUID(CumulusRecord record) {
        String uuid = record.getFieldValueOrNull(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID);
        if(uuid == null || uuid.isEmpty()) {
            record.setStringValueInField(Constants.FieldNames.REPRESENTATION_INTELLECTUAL_ENTITY_UUID, 
                    UUID.randomUUID().toString());
        } else {
            log.trace("Already has a value for the related intellectual object of the representation");
        }
    }
    
    /**
     * Initializes the value in the related object identifier value for the intellectual entity.
     * @param record The Cumulus record.
     */
    public static void initIntellectualEntityUUID(CumulusRecord record) {
        String uuid = record.getFieldValueOrNull(
                Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
        if(uuid == null || uuid.isEmpty()) {
            record.setStringValueInField(Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY, 
                    UUID.randomUUID().toString());
        } else {
            log.trace("Already has a value for the related intellectual object of the representation");
        }
    }

    /**
     * Initializes the file-checksum for the cumulus record.
     * @param record The Cumulus record to have its checksum initialized.
     */
    public static void initRecordChecksum(CumulusRecord record) {
        if(record.getFieldValueOrNull(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER) != null) {
            return;
        }
        
        WarcDigest md5Digest = ChecksumUtils.calculateChecksum(record.getFile(), ChecksumUtils.MD5_ALGORITHM);
        record.setStringValueInField(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER, md5Digest.digestString);
    }
    
    /**
     * Sets the preservation status to successfully finished.
     * Also removes any existing error messages.
     * @param record The record to set to finished.
     */
    public static void setPreservationFinished(CumulusRecord record) {
        record.setStringEnumValueForField(Constants.FieldNames.PRESERVATION_STATUS, 
                Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_COMPLETED);
        record.setStringValueInField(Constants.FieldNames.QA_ERROR, "");
    }
    
    /**
     * Sets the preservation status to failure.
     * @param record The cumulus record to set the status to failed.
     * @param status The error message for the failure state.
     */
    public static void setPreservationFailed(CumulusRecord record, String status) {
        record.setStringEnumValueForField(Constants.FieldNames.PRESERVATION_STATUS, 
                Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_FAILED);
        record.setStringValueInField(Constants.FieldNames.QA_ERROR, status);
    }
    
    /**
     * Creates the raw XML file for the intellectual entity, for the transformation.
     * It should have the following format:
     * <record>
     *   <ie_uuid>IE_UUID</ie_uuid>
     *   <object_uuid>OBJECT_UUID</object_uuid>
     *   <file_uuid>FILE_UUID</file_uuid>
     * </record>
     * (where the file uuid field is optional).
     * @param ieUUID The UUID for the intellectual entity.
     * @param metadataUUID The UUID for the metadata object.
     * @param fileUUID The UUID for the file. This may be null.
     * @param ieRawFile The file for the raw IE metadata output.
     */
    public static void createIErawFile(String ieUUID, String metadataUUID, String fileUUID, File ieRawFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("record");
            doc.appendChild(rootElement);

            Element ieField = doc.createElement("ie_uuid");
            rootElement.appendChild(ieField);
            ieField.appendChild(doc.createTextNode(ieUUID));

            Element metadataField = doc.createElement("object_uuid");
            rootElement.appendChild(metadataField);
            metadataField.appendChild(doc.createTextNode(metadataUUID));
            
            if(!StringUtils.isNullOrEmpty(fileUUID)) {
                Element fileField = doc.createElement("file_uuid");
                rootElement.appendChild(fileField);
                fileField.appendChild(doc.createTextNode(fileUUID));
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(ieRawFile);

            transformer.transform(source, result);
        } catch(Exception e) {
            throw new IllegalStateException("Cannot create the raw IntellectualEntity metadata file.", e);
        }
    }

    /**
     * Method for extracting the record name of a Cumulus record.
     * @param record The record.
     * @return The record name.
     */
    public static String getRecordName(CumulusRecord record) {
        return record.getFieldValue(Constants.FieldNames.RECORD_NAME);
    }
}
