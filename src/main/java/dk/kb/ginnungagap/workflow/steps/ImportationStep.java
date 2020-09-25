package dk.kb.ginnungagap.workflow.steps;

import java.io.File;

import dk.kb.ginnungagap.cumulus.CumulusPreservationUtils;
import dk.kb.ginnungagap.workflow.reporting.WorkflowReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.cumulus.CumulusQueryUtils;
import dk.kb.ginnungagap.utils.FileUtils;
import dk.kb.ginnungagap.utils.WarcUtils;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.metadata.utils.CalendarUtils;

/**
 * The workflow step for importing Cumulus record asset files from the archive.
 */
public class ImportationStep extends WorkflowStep {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(ImportationStep.class);

    /** Cumulus server.*/
    protected final CumulusServer server;
    /** The Bitrepository archive.*/
    protected final Archive archive;
    /** The name of the catalog to check for records for importation.*/
    protected final String catalogName;
    /** The retain directory, where existing files will be placed, so they are not overridden.*/
    protected final File retainDir;
    
    /**
     * Constructor.
     * @param server The Cumulus server.
     * @param archive The bitrepository archive.
     * @param catalogName The name of the catalog to validate.
     * @param retainDir The directory to place existing files, so they will not be overridden.
     */
    public ImportationStep(CumulusServer server, Archive archive, String catalogName, File retainDir) {
        super(catalogName);
        this.server = server;
        this.archive = archive;
        this.catalogName = catalogName;
        this.retainDir = retainDir;
    }
    
    @Override
    public void performStep(WorkflowReport report) throws Exception {
        CumulusQuery query = CumulusQueryUtils.getQueryForPreservationImportation(catalogName);
        
        CumulusRecordCollection items = server.getItems(catalogName, query);
        int i = 0;
        for(CumulusRecord record : items) {
            setResultOfRun("Running... Importing " + record.getUUID());
            importRecord(record, report);
            i++;
        }
        setResultOfRun("Imported " + i + " records");
    }

    /**
     * The step for performing the specific validation.
     * Must be implemented by the sub-classes.
     * @param record The record to validate.
     * @param report The report for workflow.
     */
    protected void importRecord(CumulusRecord record, WorkflowReport report) {
        try {
            String warcId = record.getFieldValue(Constants.FieldNames.RESOURCE_PACKAGE_ID);
            String collectionId = record.getFieldValue(Constants.FieldNames.COLLECTION_ID);
            String uuid = record.getUUID();
            File f = archive.getFile(warcId, collectionId);
            
            File file = new File(retainDir, uuid);
            WarcUtils.extractRecord(f, uuid, file);
            importFile(record, file);
            
            setValid(record, report);
        } catch (IllegalStateException e) {
            String errMsg = "The record '" + record + "' is invalid: " + e.getMessage();
            log.info(errMsg, e);
            setInvalid(record, errMsg, report);
        } catch (Exception e) {
            String errMsg = "Error when trying to validate record '" + record + "'";
            log.warn(errMsg, e);
            setInvalid(record, errMsg + " : " + e.getMessage(), report);
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
        deprecateFile(f);
        FileUtils.forceMove(file, f);
        record.setNewAssetReference(f);
    }
    
    /**
     * Deprecates a file, if it already exists.
     * @param f The file move to the retain directory, if it already exists.
     */
    protected void deprecateFile(File f) {
        if(!f.exists()) {
            return;
        }
        File newFile = new File(retainDir, f.getName());
        FileUtils.deprecateMove(f, newFile);
    }
    
    /**
     * Report back that the validation of the record failed.
     * @param record The record which is invalid.
     * @param message The message regarding why the WARC file is invalid.
     * @param report The report for workflow.
     */
    protected void setInvalid(CumulusRecord record, String message, WorkflowReport report) {
        report.addFailedRecord(CumulusPreservationUtils.getRecordName(record), message, catalogName);
        record.setStringEnumValueForField(Constants.FieldNames.BEVARING_IMPORTATION,
                Constants.FieldValues.PRESERVATION_IMPORT_FAILURE);
        record.setStringValueInField(Constants.FieldNames.BEVARING_IMPORTATION_STATUS, message);
    }

    /**
     * Report back that the validation was succes-full.
     * @param record The record which is valid.
     * @param report The report for workflow.
     */
    protected void setValid(CumulusRecord record, WorkflowReport report) {
        report.addSuccessRecord(CumulusPreservationUtils.getRecordName(record), catalogName);
        record.setStringEnumValueForField(Constants.FieldNames.BEVARING_IMPORTATION,
                Constants.FieldValues.PRESERVATION_IMPORT_NONE);
        String message = "Imported at: " + CalendarUtils.getCurrentDate();
        record.setStringValueInField(Constants.FieldNames.BEVARING_IMPORTATION_STATUS, message);
    }

    @Override
    public String getName() {
        return "Importation Workflow for catalog '" + catalogName + "'";
    }
}
