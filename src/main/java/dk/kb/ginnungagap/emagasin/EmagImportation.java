package dk.kb.ginnungagap.emagasin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.log4j.lf5.util.StreamUtils;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;
import com.google.common.io.Files;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;

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
    /** The name of the catalog. */
    protected final String catalogName;
    
    /**
     * Constructor.
     * @param conf The configuration.
     * @param cumulusServer The cumulus server.
     * @param catalogName The name of the catalog for the record.
     */
    public EmagImportation(Configuration conf, CumulusServer cumulusServer, String catalogName) {
        this.conf = conf;
        this.cumulus = cumulusServer;
        this.catalogName = catalogName;
    }
    
    /**
     * The method for initiating the conversion.
     * 
     * @param arcFile The ARC file to convert.
     */
    public void convertArcFile(File arcFile) {
        try (ArchiveReader reader = ArchiveReaderFactory.get(arcFile);) {
            log.debug("Starting to convert the arc-file: " + arcFile.getName());
            for(ArchiveRecord arcRecord : reader) {
                if(isDigitalObject(arcRecord.getHeader().getUrl())) {
                    String uuid = extractUUID(arcRecord.getHeader().getUrl());
                    File contentFile = extractArcRecordAsFile(arcRecord, uuid);
                    CumulusRecord cumulusRecord = findCumulusRecord(uuid);

                    handleRecord(cumulusRecord, contentFile);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert Arc-file: " + arcFile, e);
        }
    }
    
    /**
     * Imports the record back into Cumulus.
     * @param record The Cumulus record for the digital object.
     * @param contentFile The digital object / Cumulus record asset / content file of new packaging.
     */
    protected void handleRecord(CumulusRecord record, File contentFile) {
        try {
            Files.move(contentFile, record.getFile());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot import the file into Cumulus.", e);
        }
    }
    
    /**
     * Extracts the ARC-record as a file.
     * @param record The ARC-record.
     * @param uuid The UUID of the record.
     * @return The file with the record.
     * @throws IOException If it fails.
     */
    protected File extractArcRecordAsFile(ArchiveRecord record, String uuid) throws IOException {
        File outputFile = new File(conf.getConversionConfiguration().getTempDir(), uuid);
        try (OutputStream os = new FileOutputStream(outputFile)) {
            StreamUtils.copy(record, os);
        }
        return outputFile;
    }
    
    /**
     * Finds the Cumulus record corresponding to the ARC record / digital object.
     * @param uuid The UUID of the Cumulus record to find.
     * @return The Cumulus record.
     */
    protected CumulusRecord findCumulusRecord(String uuid) {
        CumulusQuery query = CumulusQuery.getQueryForSpecificUUID(catalogName, uuid);
        RecordItemCollection items = cumulus.getItems(catalogName, query);
        if(items == null || !items.iterator().hasNext()) {
            throw new IllegalStateException("Could not find any records for UUID: '" + uuid + "'");            
        }
        
        log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " records to be preserved.");
        
        FieldExtractor fe = new FieldExtractor(items.getLayout());

        Iterator<Item> iterator = items.iterator();
        CumulusRecord res = new CumulusRecord(fe, iterator.next());
        if(iterator.hasNext()) {
            log.warn("More than one record found for '" + uuid + "'. Only using the first found.");
        }
        
        return res;
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
    
    /**
     * Extracts the UUID from the ARC-record URL.
     * E.g. the URL:
     * digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0
     * will give the UUID:
     * 7dfe7540-6ab1-11e2-83ab-005056887b70
     * 
     * @param recordUrl The URL of the ARC-record.
     * @return The UUID part of the URL.
     */
    protected String extractUUID(String recordUrl) {
        int lowerIndex = recordUrl.lastIndexOf(SPLIT_SLASH) + 1;
        int upperIndex;
        if(recordUrl.contains(SPLIT_HASH)) {
            upperIndex = recordUrl.lastIndexOf(SPLIT_HASH);
        } else {
            upperIndex = recordUrl.length();
        }
        return recordUrl.substring(lowerIndex, upperIndex);
    }
}
