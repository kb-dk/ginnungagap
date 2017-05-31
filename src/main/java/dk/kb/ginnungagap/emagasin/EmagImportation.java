package dk.kb.ginnungagap.emagasin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.lf5.util.StreamUtils;
import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.bitrepository.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.emagasin.importation.InputFormat;
import dk.kb.ginnungagap.emagasin.importation.OutputFormatter;
import dk.kb.ginnungagap.emagasin.importation.RecordUUIDs;
import dk.kb.ginnungagap.exception.RunScriptException;
import dk.kb.ginnungagap.utils.CalendarUtils;
import dk.kb.metadata.utils.GuidExtrationUtils;

/**
 * Class for import the digital objects of ARC files from the E-magasin back into Cumulus.
 * 
 * An Emag arc-file contains two kinds of records; metadata and digital-object.
 * The metadata is in a deprecated Cumulus format, or the old KB-DOMS environment metadata format.
 * The digital-object is the asset / content-file of a Cumulus record.
 * 
 * The ARC-record with the digital object has a URI containing the UUID of the cumulus-record.
 * The UUID can be used to find the current Cumulus record.
 * 
 * The sub-classes implement the way the ARC files are converted. Either by importing them back into Cumulus
 * or creating new WARC-files from the 'digital-object' ARC-records and their corresponding Cumulus record.
 */
public class EmagImportation {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(EmagImportation.class);

    /** Prefix for the digital object record uri (the content file). */
    protected static final String DIGITAL_OBJECT_PREFIX = "digitalobject";
    /** Character hash for splitting the suffix of the uri from the UUID.*/
    protected static final String SPLIT_HASH = "#";
    /** Character slash for splitting the record uri from the UUID.*/
    protected static final String SPLIT_SLASH = "/";
    
    /** The configuration. */
    protected final Configuration conf;
    /** The Cumulus server. */
    protected final CumulusServer cumulus;
    /** The Retriever of ARC files from the Emagasin.*/
    protected final EmagasinRetriever emagRetriever;
    
    protected final InputFormat inputFormat;
    protected final OutputFormatter outputFormat;
    protected final TiffValidator validator;
    
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param cumulusServer The cumulus server.
     * @param emagasinRetriever
     * @param inputFormat
     * @param outputFormat
     * @param validator
     */
    public EmagImportation(Configuration conf, CumulusServer cumulusServer, EmagasinRetriever emagasinRetriever, 
            InputFormat inputFormat, OutputFormatter outputFormat, TiffValidator validator) {
        this.conf = conf;
        this.cumulus = cumulusServer;
        this.emagRetriever = emagasinRetriever;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.validator = validator;
    }
    
    /**
     * Run the workflow and import everything!.
     */
    public void run() {
        for(String arcFilename : inputFormat.getArcFilenames()) {
            File arcFile = emagRetriever.extractArcFile(arcFilename);
            try {
                handleArcFile(arcFile);
            } catch (IOException e) {
                // TODO: ??
            }
        }
    }
    
    /**
     * Handle a given ARC file.
     * @param arcFile
     * @throws IOException
     */
    protected void handleArcFile(File arcFile) throws IOException {
        ARCReader arcReader = ARCReaderFactory.get(arcFile);
        
        for(ArchiveRecord arcRecord : arcReader) {
            if(!isDigitalObject(arcRecord.getHeader().getUrl())) {
                continue;
            }
            String uid = GuidExtrationUtils.extractGuid(arcRecord.getHeader().getUrl());
            RecordUUIDs uuids = inputFormat.getUUIDsForArcRecordUUID(arcFile.getName(), uid);
            if(uuids == null) {
                // TODO log this error
                continue;
            }
            CumulusRecord record = cumulus.findCumulusRecord(uuids.getCatalogID(), uuids.getCumulusRecordUUID());
            try {
                handleRecord(record, arcRecord, uuids.getCumulusRecordUUID());
                outputFormat.writeSucces(uuids);
            } catch(Exception e) {
                log.warn("Failed to handle record '" + uuids + "'.", e);
                outputFormat.writeFailure(uuids, e.getMessage());
            }
        }
    }
    
    /**
     * Imports the ARC record back into Cumulus as the Asset of the Cumulus Record.
     * @param record The Cumulus record for the digital object.
     * @param arcRecord The ARC record.
     * @param guid The GUID of the ARC record.
     */
    protected void handleRecord(CumulusRecord record, ArchiveRecord arcRecord, String guid) {
        try {
            File contentFile = extractArcRecordAsFile(arcRecord, guid);
            String validationText = "Importation date: " + CalendarUtils.nowToText() + "\n";
            if(validator.isTiffMimetype(arcRecord.getHeader().getMimetype())) {
                validationText += performValidationOfTiffFile(contentFile);
            }
            Files.move(contentFile, record.getFile());
            record.setStringValueInField(Constants.FieldNames.QA_ERROR, validationText);
        } catch (IOException e) {
            String failureText = "Failed to import '" + guid + "': " + e.getMessage();
            record.setStringValueInField(Constants.FieldNames.QA_ERROR, failureText);
            throw new IllegalStateException("Cannot import the file into Cumulus.", e);
        }
    }
    
    /**
     * Performs the validation of the tiff file.
     * @param contentFile
     * @return The results of the validation. Thus the error-message 
     */
    protected String performValidationOfTiffFile(File contentFile) {
        try {
            validator.validateTiffFile(contentFile);
        } catch(RunScriptException e) {
            return e.getMessage();
        }
        
        return "TIFF file is valid";
    }
    
    /**
     * Uploads the file to the asset reference location.
     * Some locations are no longer available, and thus part of their path must be substituted for a new location.
     * In that case the new file location must be made into a new asset-reference for the cumulus record.
     * The record will have its asset reference updated afterwards, so Cumulus is aware of the changes. 
     * @param record The cumulus record to update.
     * @param contentFile The new file for the cumulus record asset reference.
     */
    protected void importFileToCumulusRecord(CumulusRecord record, File contentFile) {
        String oldPath = record.getFieldValueForNonStringField(Constants.FieldNames.ASSET_REFERENCE);
        String newPath = conf.getImportationConfiguration().getSubstitute().substitute(oldPath);
        File newFile = new File(newPath);
        FileUtils.moveFile(contentFile, newFile);
        if(!oldPath.equals(newPath)) {
            record.setNewAssetReference(newFile);
        }
        record.updateAssetReference();
    }
    
    /**
     * Extracts the ARC-record as a file.
     * @param record The ARC-record.
     * @param uuid The UUID of the record.
     * @return The file with the record.
     * @throws IOException If it fails.
     */
    protected File extractArcRecordAsFile(ArchiveRecord record, String uuid) throws IOException {
        File outputFile = new File(conf.getImportationConfiguration().getTempDir(), uuid);
        try (OutputStream os = new FileOutputStream(outputFile)) {
            StreamUtils.copy(record, os);
        }
        return outputFile;
    }
    
    
    /**
     * Determines whether the ARC-record URL corresponds to a digital object.
     * The URL of a digital object is in the form:
     * digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70
     * Whereas metadata ARC-records have the form:
     * metadata://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#1
     * @param recordUrl The URL of the ARC-record.
     * @return Whether or not the ARC-record URL has the prefix of a digital object.
     */
    protected boolean isDigitalObject(String recordUrl) {
        return recordUrl.startsWith(DIGITAL_OBJECT_PREFIX);
    }
}
