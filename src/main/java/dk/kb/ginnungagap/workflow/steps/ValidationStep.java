package dk.kb.ginnungagap.workflow.steps;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.CumulusPreservationUtils;
import dk.kb.ginnungagap.cumulus.CumulusQueryUtils;
import dk.kb.ginnungagap.workflow.reporting.WorkflowReport;
import dk.kb.ginnungagap.workflow.schedule.WorkflowStep;
import dk.kb.metadata.utils.CalendarUtils;

/**
 * Abstract class for the Cumulus Record Validation steps.
 */
public abstract class ValidationStep extends WorkflowStep {
    /** Cumulus server.*/
    protected final CumulusServer server;
    /** The name of the catalog to check for records to validate.*/
    protected final String catalogName;
    /** The value for the validation field to have valued.*/
    protected final String validationFieldValue;
    
    /**
     * Constructor.
     * @param server The Cumulus server.
     * @param catalogName The name of the catalog to validate.
     * @param validationFieldValue The validation field value to extract.
     */
    protected ValidationStep(CumulusServer server, String catalogName, String validationFieldValue) {
        super(catalogName);
        this.server = server;
        this.catalogName = catalogName;
        this.validationFieldValue = validationFieldValue;
    }
    
    @Override
    public void performStep(WorkflowReport report) throws Exception {
        CumulusQuery query = CumulusQueryUtils.getQueryForPreservationValidation(catalogName, validationFieldValue);
        
        CumulusRecordCollection items = server.getItems(catalogName, query);
        for(CumulusRecord record : items) {
            validateRecord(record, report);
        }
        setResultOfRun("Validated " + items.getCount() + " records.");
    }

    /**
     * The step for performing the specific validation.
     * Must be implemented by the sub-classes.
     * @param record The record to validate.
     * @param report The report for workflow.
     */
    protected abstract void validateRecord(CumulusRecord record, WorkflowReport report);
    
    /**
     * Report back that the validation of the record failed.
     * @param record The record which is invalid.
     * @param message The message regarding why the WARC file is invalid.
     * @param report The report for workflow.
     */
    protected void setInvalid(CumulusRecord record, String message, WorkflowReport report) {
        report.addFailedRecord(CumulusPreservationUtils.getRecordName(record), message, catalogName);
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK, 
                Constants.FieldValues.PRESERVATION_VALIDATION_FAILURE);
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK_STATUS, message);
    }

    /**
     * Report back that the validation was succes-full.
     * @param record The record which is valid.
     * @param report The report for workflow.
     */
    protected void setValid(CumulusRecord record, WorkflowReport report) {
        report.addSuccessRecord(CumulusPreservationUtils.getRecordName(record), catalogName);
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK, 
                Constants.FieldValues.PRESERVATION_VALIDATION_OK);
        String message = "Validated at: " + CalendarUtils.getCurrentDate();
        record.setStringValueInField(Constants.FieldNames.BEVARING_CHECK_STATUS, message);
    }
}
