package dk.kb.ginnungagap.cumulus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jwat.warc.WarcDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.canto.cumulus.Asset;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.fieldvalue.AssetReference;
import com.canto.cumulus.fieldvalue.StringEnumFieldValue;

import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.cumulus.field.Field;
import dk.kb.ginnungagap.cumulus.field.StringField;
import dk.kb.ginnungagap.cumulus.field.TableField;
import dk.kb.ginnungagap.cumulus.field.TableField.Row;
import dk.kb.ginnungagap.utils.ChecksumUtils;
import dk.kb.ginnungagap.utils.StringUtils;

/**
 * Record from Cumulus.
 * The Cumulus records are extracted from the Cumulus Server using a Cumulus Query.
 * 
 * The Cumulus server extracts a RecordItemCollection, which both is a collection of RecordItems and
 * contains the field layout (used for creating the FieldExtractor).
 * Each RecordItem is used with the FieldExtractor for the RecordItemCollection to create one 
 * CumulusRecord (this class).
 * 
 * Basically this class contains helper methods and extractor for the Item delivered by Cumulus.
 */
public class CumulusRecord {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CumulusRecord.class);

    /** Constant for not allowing assert to be extracted from proxy.*/
    protected static final boolean ASSET_NOT_ALLOW_PROXY = false;

    /** The field extractor.*/
    protected final FieldExtractor fe;
    /** The Cumulus record item.*/
    protected final Item item;

    /** The guid for the metadata record. It is created and stored the first time it is needed. */
    protected String metadataGuid;
    
    /**
     * Constructor.
     * @param fe The field extractor.
     * @param item The Cumulus record item.
     */
    public CumulusRecord(FieldExtractor fe, Item item) {
        this.fe = fe;
        this.item = item;        
    }
    
    /**
     * Initializes the fields, which must be initialized 
     */
    public void initFields() {
        initChecksumField();
        initRelatedIntellectualEntityObjectIdentifier();
    }

    /**
     * @return The identifier for this record.
     */
    public String getUUID() {
        String res = getFieldValue(Constants.FieldNames.GUID);
        if(res.contains("/")) {
            res = res.substring(res.lastIndexOf("/")+1, res.length());
        }
        return res;
    }

    /**
     * Extracts the value of the field with the given name.
     * If multiple fields have the given field name, then only the value of one of the fields are returned.
     * The result is in String format.
     * @param fieldname The name for the field. 
     * @return The string value of the field. 
     */
    public String getFieldValue(String fieldname) {
        GUID fieldGuid = fe.getFieldGUID(fieldname);
        return item.getStringValue(fieldGuid);
    }
    
    /**
     * Retrieves the string value of a field (also non-string fields, except tables, pictures and audio).
     * @param fieldname The name of the field.
     * @return The string value of the field.
     */
    public String getFieldValueForNonStringField(String fieldname) {
        return fe.getStringValueForField(fieldname, item);
    }

    /**
     * Retrieves the metadata as an input stream.
     * @param cumulusFieldMetadataFile The file which the metadata is written to.
     * @return The input stream with the metadata.
     */
    public InputStream getMetadata(File cumulusFieldMetadataFile) {
        try {
            writeMetadataFile(cumulusFieldMetadataFile);
            return new FileInputStream(cumulusFieldMetadataFile);
        } catch (Exception e) {
            throw new IllegalStateException("Could not extract metadata file.", e);
        }
    }

    /**
     * Retrieves the content file.
     * @return The content file.
     */
    public File getFile() {
        try {
            AssetReference reference = item.getAssetReferenceValue(GUID.UID_REC_ASSET_REFERENCE);

            Asset asset = reference.getAsset(ASSET_NOT_ALLOW_PROXY);
            return asset.getAsFile();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot retrieve the file.", e);
        }        
    }
    
    /**
     * Set the given file as new asset reference for this cumulus record.
     * @param f The new file for the asset reference.
     */
    public void setNewAssetReference(File f) {
        try {
            AssetReference reference = item.getAssetReferenceValue(GUID.UID_REC_ASSET_REFERENCE);

            AssetReference newARef = new AssetReference(reference.getCumulusSession(), f.getAbsolutePath(), null);
            item.setAssetReferenceValue(GUID.UID_REC_ASSET_REFERENCE, newARef);
            item.save();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot update the asset reference with file '" + f.getAbsolutePath() 
                    + "'.", e);
        }
    }
    
    /**
     * Updates the asset reference.
     */
    public void updateAssetReference() {
        item.updateAssetReference();
        item.save();
    }
    
    /**
     * Extracts all the metadata fields for this record and converts them into an XML file. 
     * @param cumulusFieldFile The file where the XML for this record is placed.
     * @throws ParserConfigurationException If the XML parse has an issue with the configuration.
     * @throws TransformerException If the transformer has an issue.
     */
    protected void writeMetadataFile(File cumulusFieldFile) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("record");
        doc.appendChild(rootElement);
        
        Map<String, Field> fields = fe.getFields(item);

        for(Field f : fields.values()) {
            if(!f.isEmpty()) {
                Element field = doc.createElement("field");
                rootElement.appendChild(field);
                field.setAttribute("data-type", f.getType());
                field.setAttribute("name", f.getName());
                
                if(f instanceof StringField) {
                    StringField sf = (StringField) f;
                    for(String v : getValues(sf.getStringValue())) {
                        Element value = doc.createElement("value");
                        field.appendChild(value);
                        value.appendChild(doc.createTextNode(v));
                    }
                } else if(f instanceof TableField) {
                    Element table = doc.createElement("table");
                    field.appendChild(table);
                    
                    TableField tf = (TableField) f;
                    for(Row r : tf.getRows()) {
                        Element row = doc.createElement("row");
                        table.appendChild(row);
                        
                        for(Map.Entry<String, String> element : r.getElements().entrySet()) {
                            Element coloumn = doc.createElement("field");
                            row.appendChild(coloumn);
                            coloumn.setAttribute("name", element.getKey());
                            
                            for(String v : getValues(element.getValue())) {
                                Element value = doc.createElement("value");
                                coloumn.appendChild(value);
                                value.appendChild(doc.createTextNode(v));
                            }
                        }
                    }
                }
            }
        }
        
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(cumulusFieldFile);

        transformer.transform(source, result);
    }
    
    /**
     * The given value is formatted and split into lines.
     * @param value
     * @return
     */
    protected String[] getValues(String value) {
        String encodedValue = StringUtils.xmlEncode(value);
        return encodedValue.split("\n");
    }

    /**
     * Validates the record against the given required fields.
     * @param requiredFields The required fields validate against.
     * @throws IllegalStateException If any of the requirements are not met.
     */
    public void validateRequiredFields(RequiredFields requiredFields) {
        List<String> fieldsNotMeetingRequirementsErrors = new ArrayList<String>();

        Map<String, Field> fields = fe.getAllFields(item);
        for(String field : requiredFields.getBaseFields()) {
            if(!fields.containsKey(field)) {
                fieldsNotMeetingRequirementsErrors.add("The field '" + field 
                        + "' does not exist.");                
            } else if(fields.get(field).isEmpty()) {
                fieldsNotMeetingRequirementsErrors.add("The field '" + field 
                        + "' does not contain any data.");
            }
        }

        for(String field : requiredFields.getWritableFields()) {
            if(!fields.containsKey(field)) {
                fieldsNotMeetingRequirementsErrors.add("The field '" + field 
                        + "' does not exist.");                                
            }
            // Do not check for value, and do not check 'editable' - since it only refers to editablity in the GUI.
        }

        if(!fieldsNotMeetingRequirementsErrors.isEmpty()) {
            String errMsg = "The following field(s) did not live up to the requirements: \n" 
                    + StringUtils.listToString(fieldsNotMeetingRequirementsErrors, "\n");
            log.warn(errMsg);
            throw new IllegalStateException("Required fields failure, " + fieldsNotMeetingRequirementsErrors.size() 
                    + " field(s) did not live up to their requirements.\n" + errMsg);
        }
    }

    /**
     * Sets the value for the md5 checksum of the file for this record.
     * This must be the checksum of the resource record in the packaged WARC file.
     */
    public void initChecksumField() {
        WarcDigest md5Digest = ChecksumUtils.calculateChecksum(getFile(), ChecksumUtils.MD5_ALGORITHM);
        try {
            GUID metadataPackageIdGuid = fe.getFieldGUID(Constants.FieldNames.CHECKSUM_ORIGINAL_MASTER);
            item.setStringValue(metadataPackageIdGuid, md5Digest.digestString);
            item.save();
        } catch (Exception e) {
            String errMsg = "Could not set the checksum for the file.";
            log.error(errMsg, e);
            throw new IllegalStateException(errMsg, e);
        }
    }
    
    /**
     * Initializes the value in the related object identifier value for the intellectual entity.
     */
    public void initRelatedIntellectualEntityObjectIdentifier() {
        try {
            GUID relatedIntellectualEntityGuid = fe.getFieldGUID(
                    Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY);
            if(item.hasValue(relatedIntellectualEntityGuid)) {
                log.debug("Already has a value for the related intellectual object");
            } else {
                item.setStringValue(relatedIntellectualEntityGuid, UUID.randomUUID().toString());
                item.save();
            }
        } catch (Exception e) {
            String errMsg = "Could not set or retrieve the Cumulus field: " 
                    + Constants.FieldNames.RELATED_OBJECT_IDENTIFIER_VALUE_INTELLECTUEL_ENTITY;
            log.error(errMsg, e);
            throw new IllegalStateException(errMsg, e);
        }
    }
    
    /**
     * Sets the preservation status to failure.
     * @param status The error message for the failure state.
     */
    public void setPreservationFailed(String status) {
        try {
            GUID preservationStatusGuid = fe.getFieldGUID(Constants.FieldNames.PRESERVATION_STATUS);
            StringEnumFieldValue enumValue = item.getStringEnumValue(preservationStatusGuid);
            enumValue.setFromDisplayString(Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_FAILED);
            item.setStringEnumValue(preservationStatusGuid, enumValue);
            
            GUID qaErrorGuid = fe.getFieldGUID(Constants.FieldNames.QA_ERROR);
            item.setStringValue(qaErrorGuid, status);
            item.save();
        } catch (Exception e) {
            log.error("Could not set preservation failure state for status '" + status + "'", e);
        }
    }

    /**
     * Sets the preservation status to successfully finished.
     * Also removes any existing error messages.
     */
    public void setPreservationFinished() {
        try {
            GUID preservationStatusGuid = fe.getFieldGUID(Constants.FieldNames.PRESERVATION_STATUS);
            StringEnumFieldValue enumValue = item.getStringEnumValue(preservationStatusGuid);
            enumValue.setFromDisplayString(Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_COMPLETED);
            item.setStringEnumValue(preservationStatusGuid, enumValue);

            GUID qaErrorGuid = fe.getFieldGUID(Constants.FieldNames.QA_ERROR);
            item.setStringValue(qaErrorGuid, "");
            item.save();
        } catch (Exception e) {
            log.error("Could not set preservation complete", e);
        }
    }
    
    /**
     * Set the string value of a given Cumulus field.
     * @param fieldName The name of the field.
     * @param value The new value of the field.
     */
    public void setStringValueInField(String fieldName, String value) {
        try {
            GUID fieldGuid = fe.getFieldGUID(fieldName);
            item.setStringValue(fieldGuid, value);
            item.save();
        } catch (Exception e) {
            String errMsg = "Could not set the value '" + value + "' for the field '" + fieldName + "'";
            log.error(errMsg, e);
            throw new IllegalStateException(errMsg, e);
        }
    }

    /**
     * Retrieves the value for the metadata guid.
     * The first time this method is called, then the guid is created and written back to Cumulus.
     * All following calls of the method returns the guid created in the initial call.
     * @return The metadata guid for this cumulus record.
     */
    public String getMetadataGUID() {
        if(metadataGuid == null) {
            metadataGuid = UUID.randomUUID().toString();
            try {
                GUID metadataPackageIdGuid = fe.getFieldGUID(Constants.PreservationFieldNames.METADATA_GUID);
                item.setStringValue(metadataPackageIdGuid, metadataGuid);
                item.save();
            } catch (Exception e) {
                String errMsg = "Could not set the package id for the metadata.";
                log.error(errMsg, e);
                throw new IllegalStateException(errMsg, e);
            }            
        }

        return metadataGuid;
    }

    @Override
    public String toString() {
        return "[CumulusRecord : " + getClass().getCanonicalName() + " -> " + getUUID() + "]";
    }
}
