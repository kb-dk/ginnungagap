package dk.kb.ginnungagap.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

/**
 * Class for utility methods regarding WARC files.
 */
public class WarcUtils {
    
    /**
     * Extracts a record from a WARC file and delivers it to the output file.
     * @param warcFile The WARC file with the record.
     * @param recordId The ID of the record to extract.
     * @param outputFile The file where the record will be delivered.
     * @throws IOException If the WARC files does not contain a record with the given id, or
     * if an error occurs while reading the WARC file or writing to the output file.
     */
    public static void extractRecord(File warcFile, String recordId, File outputFile) throws IOException {
        try (WarcReader reader = WarcReaderFactory.getReader(new FileInputStream(warcFile))) {
            WarcRecord warcRecord = getWarcRecord(reader, recordId);

            try (OutputStream os = new FileOutputStream(outputFile)) {
                StreamUtils.copyInputStreamToOutputStream(warcRecord.getPayloadContent(), os);
                os.flush();
                os.close();
            }
        }

    }
    
    /**
     * Retrieves the WARC record from the WARC file.
     * Will throw an exception, if the record is not found.
     * @param file The WARC file to extract the WARC record from.
     * @param recordId The id of the WARC record.
     * @return The WARC record.
     * @throws IOException If an error occurs when reading the WARC file.
     */
    protected static WarcRecord getWarcRecord(WarcReader reader, String recordId) throws IOException {
        for(WarcRecord record : reader) {
            if(record.header.warcRecordIdStr.contains(recordId)) {
                return record;
            }
        }
        throw new IOException("Could not find the record '" + recordId + "' in the WARC file.");
    }

}
