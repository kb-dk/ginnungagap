package dk.kb.ginnungagap.workflow.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.ginnungagap.utils.StreamUtils;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.metadata.utils.CalendarUtils;
import dk.kb.yggdrasil.bitmag.Bitrepository;

/**
 * The workflow step for importing Cumulus record asset files from the archive.
 */
public class ImportationStep implements WorkflowStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(ImportationStep.class);

    /** Cumulus server.*/
    protected final CumulusServer server;
    /** The Bitmag client*/
    protected final Bitrepository bitmag;
    /** The name of the catalog to check for records for importation.*/
    protected final String catalogName;
    
    /**
     * Constructor.
     * @param server The Cumulus server.
     * @param bitmag The bitrepository.
     * @param catalogName The name of the catalog to validate.
     */
    public ImportationStep(CumulusServer server, Bitrepository bitmag, String catalogName) {
        this.server = server;
        this.bitmag = bitmag;
        this.catalogName = catalogName;
    }
    
    @Override
    public void performStep() throws Exception {
        CumulusQuery query = CumulusQuery.getQueryForPreservationImportation(catalogName);
        
        RecordItemCollection items = server.getItems(catalogName, query);
        FieldExtractor fe = new FieldExtractor(items.getLayout(), server, catalogName);
        for(Item item : items) {
            CumulusRecord record = new CumulusRecord(fe, item);
            importRecord(record);
        }
    }

    /**
     * The step for performing the specific validation.
     * Must be implemented by the sub-classes.
     * @param record The record to validate.
     */
    protected void importRecord(CumulusRecord record) {
        try {
            String warcId = record.getFieldValue(Constants.PreservationFieldNames.RESOURCEPACKAGEID);
            String collectionId = record.getFieldValue(Constants.PreservationFieldNames.COLLECTIONID);
            String uuid = record.getUUID();
            File f = bitmag.getFile(warcId, collectionId, null);
            
            try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(f))) {
                WarcRecord warcRecord = getWarcRecord(reader, uuid);

                File file = extractRecord(warcRecord, record);
                importFile(record, file);
            }
            setValid(record);
        } catch (IllegalStateException e) {
            String errMsg = "The record '" + record + "' is invalid: " + e.getMessage();
            log.info(errMsg, e);
            setInvalid(record, errMsg);            
        } catch (Exception e) {
            String errMsg = "Error when trying to validate record '" + record + "'";
            log.warn(errMsg, e);
            setInvalid(record, errMsg + " : " + e.getMessage());
        }
    }
    
    /**
     * Import the file to the destination for the Cumulus record asset reference.
     * @param record The record to have the file imported.
     * @param file The file to import.
     */
    protected void importFile(CumulusRecord record, File file) {
        String filePath = record.getFieldValueForNonStringField(Constants.FieldNames.ASSET_REFERENCE);
        File f = new File(filePath);
        FileUtils.moveOrOverrideFile(file, f);
        record.setNewAssetReference(f);
    }
    
    /**
     * Extracts the content of a record into a file. 
     * @param warcRecord The WARC record to be extracted.
     * @param cumulusRecord The Cumulus record for the warc record to be extracted.
     * @return The file with the extracted WARC record.
     * @throws IOException If an error occurs while extracting the WARC record.
     */
    protected File extractRecord(WarcRecord warcRecord, CumulusRecord cumulusRecord) throws IOException {
        File outputFile = new File(cumulusRecord.getUUID());
        
        try (OutputStream os = new FileOutputStream(outputFile)) {
            StreamUtils.copyInputStreamToOutputStream(warcRecord.getPayloadContent(), os);
            os.flush();
            os.close();
        }
        return outputFile;
    }
    
    /**
     * Retrieves the WARC record from the WARC file.
     * Will throw an exception, if the record is not found.
     * @param file The WARC file to extract the WARC record from.
     * @param recordId The id of the WARC record.
     * @return The WARC record.
     * @throws IOException If an error occurs when reading the WARC file.
     */
    protected WarcRecord getWarcRecord(WarcReader reader, String recordId) throws IOException {
        for(WarcRecord record : reader) {
            if(record.header.warcRecordIdStr.contains(recordId)) {
                return record;
            }
        }
        throw new IllegalStateException("Could not find the record '" + recordId + "' in the WARC file.");
    }

    /**
     * Report back that the validation of the record failed.
     * @param record The record which is invalid.
     * @param message The message regarding why the WARC file is invalid.
     */
    protected void setInvalid(CumulusRecord record, String message) {
        record.setStringValueInField(Constants.FieldNames.BEVARING_IMPORTATION, 
                Constants.FieldValues.PRESERVATION_IMPORT_FAILURE);
        record.setStringValueInField(Constants.FieldNames.BEVARING_IMPORTATION_STATUS, message);
    }

    /**
     * Report back that the validation was succes-full.
     * @param record The record which is valid.
     */
    protected void setValid(CumulusRecord record) {
        record.setStringValueInField(Constants.FieldNames.BEVARING_IMPORTATION, 
                Constants.FieldValues.PRESERVATION_IMPORT_NONE);
        String message = "Imported at: " + CalendarUtils.getCurrentDate();
        record.setStringValueInField(Constants.FieldNames.BEVARING_IMPORTATION_STATUS, message);
    }

    @Override
    public String getName() {
        return "Importation Workflow";
    }
}
