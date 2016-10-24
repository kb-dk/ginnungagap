package dk.kb.ginnungagap.cumulus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Asset;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.fieldvalue.AssetReference;

import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.cumulus.field.Field;
import dk.kb.ginnungagap.cumulus.field.StringField;
import dk.kb.ginnungagap.cumulus.field.TableField;
import dk.kb.ginnungagap.cumulus.field.TableField.Row;
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
    private static final boolean ASSET_NOT_ALLOW_PROXY = false;

    /** The field extractor.*/
    private final FieldExtractor fe;
    /** The Cumulus record item.*/
    private final Item item;

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
     * @return The identifier for this record.
     */
    public String getID() {
        // TODO: use a different identifier?
        return getFieldValue(Constants.FieldNames.GUID);
//        return Integer.toString(item.getID());
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
     * Retrieves the metadata as an input stream.
     * @return The input stream with the metadata.
     */
    public ByteArrayInputStream getMetadata() {
        StringBuffer sb = extractMetadataAsXML();
        return new ByteArrayInputStream(sb.toString().getBytes(Charset.defaultCharset()));
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
     * Transforms the metadata fields for the record into XML.
     * The fields with no values are ignored.
     * @return A StringBuffer with the XML of the metadata.
     */
    protected StringBuffer extractMetadataAsXML() {
        Map<String, Field> fields = fe.getFields(item);

        StringBuffer res = new StringBuffer();
        res.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        res.append("\n");
        res.append("<record>\n");

        for(Field f : fields.values()) {
            if(!f.isEmpty()) {
                res.append("  <field name=\"" + f.getName() + "\" data-type=\"" + f.getType() + "\">\n");
                if(f instanceof StringField) {
                    StringField sf = (StringField) f;
                    res.append("    <value>" + sf.getStringValue() + "</value>\n");
                } else if(f instanceof TableField) {
                    TableField tf = (TableField) f;
                    res.append("    <table>\n");
                    for(Row r : tf.getRows()) {
                        res.append("      <row>\n");
                        for(Map.Entry<String, String> element : r.getElements().entrySet()) {
                            res.append("        <field name=\"" + element.getKey() + "\" data-type=\"" 
                                    + element.getValue() + "\">\n");
                        }
                        res.append("      </row>\n");
                    }
                    res.append("    <table>\n");
                }
                res.append("  </field>\n");
            }
        }
        res.append("</record>\n");

        return res;
    }

    /**
     * Validates the record against the given required fields.
     * @param requiredFields The required fields validate against.
     * @throws IllegalStateException If any of the requirements are not met.
     */
    public void validateRequiredFields(RequiredFields requiredFields) {
        List<String> fieldsNotMeetingRequirementsErrors = new ArrayList<String>();

        Map<String, Field> fields = fe.getFields(item);
        for(String field : requiredFields.getBaseFields()) {
            if(!fields.containsKey(field) || fields.get(field).isEmpty()) {
                fieldsNotMeetingRequirementsErrors.add("The field '" + field 
                        + "' did not exist or did not contain any data.");
            }
        }

        for(String field : requiredFields.getWritableFields()) {
            if(!fields.containsKey(field) || !fields.get(field).isFieldEditable()) {
                fieldsNotMeetingRequirementsErrors.add("The field '" + field 
                        + "' did not exist or was not writable.");
            }
        }

        if(!fieldsNotMeetingRequirementsErrors.isEmpty()) {
            log.warn("The following field(s) did not live up to the requirements: \n" 
                    + StringUtils.listToString(fieldsNotMeetingRequirementsErrors, "\n"));
            throw new IllegalStateException("Required fields failure, " + fieldsNotMeetingRequirementsErrors.size() 
                    + " field(s) did not live up to their requirements.");
        }
    }

    /**
     * Sets the preservation status to failure.
     * @param status The error message for the failure state.
     */
    public void setPreservationFailed(String status) {
        try {
            GUID preservationStatusGuid = fe.getFieldGUID(Constants.FieldNames.PRESERVATION_STATUS);
            item.setStringValue(preservationStatusGuid, Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_FAILED);
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
            item.setStringValue(preservationStatusGuid, Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_COMPLETED);
            GUID qaErrorGuid = fe.getFieldGUID(Constants.FieldNames.QA_ERROR);
            item.setStringValue(qaErrorGuid, "");
            item.save();
        } catch (Exception e) {
            log.error("Could not set preservation complete", e);
        }
    }

    /**
     * Sets the value for the preservation package for the resource.
     * @param filename The name of the file containing the resource (content file).
     */
    public void setPreservationResourcePackage(String filename) {
        try {
            GUID representationPackageIdGuid = fe.getFieldGUID(
                    Constants.PreservationFieldNames.REPRESENTATIONPACKAGEID);
            item.setStringValue(representationPackageIdGuid, filename);
            item.save();
        } catch (Exception e) {
            log.error("Could not set the representation package id.", e);
        }
    }

    /**
     * Sets the value for the preservation package for the metadata.
     * @param filename The name of the file containing the metadata.
     */
    public void setPreservationMetadataPackage(String filename) {
        try {
            GUID metadataPackageIdGuid = fe.getFieldGUID(Constants.PreservationFieldNames.METADATAPACKAGEID);
            item.setStringValue(metadataPackageIdGuid, filename);
            item.save();
        } catch (Exception e) {
            log.error("Could not set the package id for the metadata.", e);
        }
    }

    @Override
    public String toString() {
        return "[Record : " + getClass().getCanonicalName() + " -> " + getID() + "]";
    }
}
