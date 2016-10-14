package dk.kb.ginnungagap.record;

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
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.Field;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.cumulus.StringField;
import dk.kb.ginnungagap.cumulus.TableField;
import dk.kb.ginnungagap.cumulus.TableField.Row;

/**
 * Record from Cumulus.
 */
public class CumulusRecord implements Record {
    /** The logger.*/
    private final static Logger log = LoggerFactory.getLogger(CumulusRecord.class);

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
    
    @Override
    public String getID() {
        // TODO: use a different identifier?
        return Integer.toString(item.getID());
    }
    
    @Override
    public String getFieldValue(String fieldname) {
        GUID fieldGuid = fe.getFieldGUID(fieldname);
        return item.getStringValue(fieldGuid);
    }

    @Override
    public ByteArrayInputStream getMetadata() {
        StringBuffer sb = extractMetadataAsXML();
        return new ByteArrayInputStream(sb.toString().getBytes(Charset.defaultCharset()));
    }

    @Override
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

    @Override
    public void validateRequiredFields(RequiredFields requiredFields) {
        List<String> fieldsNotMeetingRequirements = new ArrayList<String>();
        
        Map<String, Field> fields = fe.getFields(item);
        for(String field : requiredFields.getBaseFields()) {
            if(!fields.containsKey(field) || fields.get(field).isEmpty()) {
                fieldsNotMeetingRequirements.add(field);
            }
        }

        for(String field : requiredFields.getWritableFields()) {
            if(!fields.containsKey(field) || !fields.get(field).isFieldEditable()) {
                fieldsNotMeetingRequirements.add(field);
            }
        }
        
        if(!fieldsNotMeetingRequirements.isEmpty()) {
            String errMsg = "The following fields does not live up to the requirements: " 
                    + fieldsNotMeetingRequirements;
            log.warn(errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    @Override
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

    @Override
    public void setPreservationFinished() {
        try {
            GUID preservationStatusGuid = fe.getFieldGUID(Constants.FieldNames.PRESERVATION_STATUS);
            item.setStringValue(preservationStatusGuid, Constants.FieldValues.PRESERVATIONSTATE_ARCHIVAL_COMPLETED);
            item.save();
        } catch (Exception e) {
            log.error("Could not set preservation complete", e);
        }
    }

    @Override
    public void setPreservationResourcePackage(String filename) {
        try {
            GUID representationPackageIdGuid = fe.getFieldGUID(Constants.PreservationFieldNames.REPRESENTATIONPACKAGEID);
            item.setStringValue(representationPackageIdGuid, filename);
            item.save();
        } catch (Exception e) {
            log.error("Could not set the representation package id.", e);
        }
    }

    @Override
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
