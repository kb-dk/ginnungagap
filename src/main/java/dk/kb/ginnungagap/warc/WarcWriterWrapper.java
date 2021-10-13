package dk.kb.ginnungagap.warc;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.exception.WarcException;
import org.jwat.common.ContentType;
import org.jwat.common.RandomAccessFileOutputStream;
import org.jwat.common.Uri;
import org.jwat.warc.WarcConcurrentTo;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcDigest;
import org.jwat.warc.WarcHeader;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;

/**
 * Wrapper class to hide away WARC writing internals.
 */
public class WarcWriterWrapper {

    /** Logging mechanism. */
    private static final Logger logger = LoggerFactory.getLogger(WarcWriterWrapper.class.getName());

    /** Buffer size used by the WARC reader. */
    public static final int WARC_READER_BUFFER_SIZE = 8192;

    /** UUID of package/WARC file. */
    protected String uuid;

    /** WARC file. */
    protected File writerFile;

    /** WARC <code>RandomAccessFile</code>.  */
    protected RandomAccessFile writerRaf;

    /** <code>RandomAccessFile</code> as an <code>OutputStream</code> */
    protected RandomAccessFileOutputStream writerRafout;

    /** WARC writer implementation. */
    protected WarcWriter writer;

    /** Is the WARC file new or not. */
    protected boolean bIsNew;

    /**
     * Open new or existing WARC file.
     * @param path parent path where the file must be created/opened
     * @param uuid uuid of WARC file
     * @return WARC writer wrapper
     * @throws WarcException is an exception occurs
     */
    public static WarcWriterWrapper getWriter(File path, String uuid) throws WarcException {
        ArgumentCheck.checkExistsDirectory(path, "path");
        ArgumentCheck.checkNotNullOrEmpty(uuid, "uuid");
        WarcWriterWrapper w3 = null;
        File writerFile = new File(path, uuid);
        try {
            if (writerFile.exists() && !writerFile.isFile()) {
                throw new WarcException("'" + uuid +"' appears to be an existing folder, this is disappointing.");
            }
            w3 = new WarcWriterWrapper();
            w3.uuid = uuid;
            w3.writerFile = writerFile;
            w3.writerRaf = new RandomAccessFile(w3.writerFile, "rw");
            w3.writerRaf.seek(w3.writerRaf.length());
            w3.writerRafout = new RandomAccessFileOutputStream(w3.writerRaf);
            w3.writer = WarcWriterFactory.getWriter(w3.writerRafout, WARC_READER_BUFFER_SIZE, false);
            w3.writer.setExceptionOnContentLengthMismatch(true);
            w3.bIsNew = (w3.writerRaf.length() == 0L);
        } catch (IOException e) {
            throw new WarcException("Exception while opening WARC file", e);
        }
        return w3;
    }

    /** WARC file Warcinfo id. */
    private Uri warcinfoRecordId;

    /**
     * Returns the WARC file Warcinfo id.
     * @return the WARC file Warcinfo id
     */
    public Uri getWarcinfoRecordId() {
        return warcinfoRecordId;
    }

    /**
     * Append a Warcinfo record to WARC file.
     * @param warcFieldsBytes warc fields as byte array
     * @param blockDigest optional block digest
     * @return WarcRecordId of newly created record
     * @throws WarcException if an exception occurs while writing record
     */
    public Uri writeWarcinfoRecord(byte[] warcFieldsBytes, WarcDigest blockDigest) throws WarcException {
        ArgumentCheck.checkNotNull(warcFieldsBytes, "warcFieldsBytes");
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(warcFieldsBytes);
            warcinfoRecordId = new Uri("urn:uuid:" + uuid);
            WarcRecord record = WarcRecord.createRecord(writer);
            WarcHeader header = record.header;
            header.warcTypeIdx = WarcConstants.RT_IDX_WARCINFO;
            header.warcDate = new Date();
            header.warcFilename = uuid;
            header.warcRecordIdUri = warcinfoRecordId;
            header.contentTypeStr = WarcConstants.CT_APP_WARC_FIELDS;
            header.warcBlockDigest = blockDigest;
            header.contentLength = Long.valueOf(warcFieldsBytes.length);
            writer.writeHeader(record);
            writer.streamPayload(bin);
            writer.closeRecord();
        } catch (Exception e) {
            throw new WarcException("Exception while writing WARC warcinfo record!", e);
        }
        logger.debug("Written Info Record '" + uuid + "'.");

        return warcinfoRecordId;
    }

    /**
     * Append a resource record to WARC file.
     * @param in payload input stream
     * @param len payload length
     * @param contentType payload content-type
     * @param blockDigest optional block digest
     * @param uuid The UUID for the record.
     * @return WarcRecordId of newly created record
     * @throws WarcException if an exception occurs while writing record
     */
    public Uri writeResourceRecord(InputStream in, long len, ContentType contentType, WarcDigest blockDigest,
                                   String uuid) throws WarcException {
        ArgumentCheck.checkNotNull(in, "in");
        ArgumentCheck.checkNotNull(len, "len");
        ArgumentCheck.checkNotNull(contentType, "contentType");
        ArgumentCheck.checkNotNull(uuid, "uuid");
        Uri warcRecordIdUri = null;
        try {
            warcRecordIdUri = new Uri("urn:uuid:" + uuid);
            WarcRecord record = WarcRecord.createRecord(writer);
            WarcHeader header = record.header;
            header.warcTypeIdx = WarcConstants.RT_IDX_RESOURCE;
            header.warcDate = new Date();
            header.warcWarcinfoIdUri = warcinfoRecordId;
            header.warcRecordIdUri = warcRecordIdUri;
            header.warcTargetUriUri = warcRecordIdUri;
            header.warcBlockDigest = blockDigest;
            header.contentType = contentType;
            header.contentLength = len;
            writer.writeHeader(record);
            writer.streamPayload(in);
            writer.closeRecord();
        } catch (Exception e) {
            throw new WarcException("Exception while writing WARC resource record!", e);
        }
        logger.debug("Written Resource Record '" + uuid + "'.");

        return warcRecordIdUri;
    }

    /**
     * Append a metadata record to WARC file.
     * @param in payload input stream
     * @param len payload length
     * @param refersTo The refers to header element.
     * @param contentType payload content-type
     * @param blockDigest optional block digest
     * @param warcUuid The UUID for the record.
     * @param targetUuid The UUID for the target uri of the record. 
     * @return WarcRecordId of newly created record
     * @throws WarcException if an exception occurs while writing record
     */
    public Uri writeMetadataRecord(InputStream in, long len, ContentType contentType, Uri refersTo,
                                   WarcDigest blockDigest, String warcUuid, String targetUuid) throws WarcException {
        ArgumentCheck.checkNotNull(in, "in");
        ArgumentCheck.checkNotNull(len, "len");
        ArgumentCheck.checkNotNull(contentType, "contentType");
        ArgumentCheck.checkNotNull(warcUuid, "uuid");
        Uri warcRecordIdUri = null;
        Uri warcTargetUri = null;
        try {
            warcRecordIdUri = new Uri("urn:uuid:" + warcUuid);
            warcTargetUri = new Uri("urn:uuid:" + targetUuid);
            WarcRecord record = WarcRecord.createRecord(writer);
            WarcHeader header = record.header;
            header.warcTypeIdx = WarcConstants.RT_IDX_METADATA;
            header.warcDate = new Date();
            header.warcWarcinfoIdUri = warcinfoRecordId;
            header.warcRecordIdUri = warcRecordIdUri;
            header.warcTargetUriUri = warcTargetUri;
            header.warcRefersToUri = refersTo;
            header.warcBlockDigest = blockDigest;
            header.contentType = contentType;
            header.contentLength = len;
            writer.writeHeader(record);
            writer.streamPayload(in);
            writer.closeRecord();
        } catch (Exception e) {
            throw new WarcException("Exception while writing WARC metadata record!", e);
        }
        logger.debug("Written Metadata Record '" + warcUuid + "'.");

        return warcRecordIdUri;
    }

    /**
     * Append a update record to WARC file.
     * @param in payload input stream
     * @param len payload length
     * @param contentType payload content-type
     * @param refersTo The refers to header element.
     * @param concurrentTo List of concurrentTo header elements.
     * @param blockDigest optional block digest
     * @param uuid The UUID for the record.
     * @return WarcRecordId of newly created record
     * @throws WarcException if an exception occurs while writing record
     */
    public Uri writeUpdateRecord(InputStream in, long len, ContentType contentType, Uri refersTo,
                                 List<WarcConcurrentTo> concurrentTo, WarcDigest blockDigest, String uuid)
            throws WarcException {
        ArgumentCheck.checkNotNull(in, "in");
        ArgumentCheck.checkNotNull(len, "len");
        ArgumentCheck.checkNotNull(contentType, "contentType");
        ArgumentCheck.checkNotNull(uuid, "uuid");
        ArgumentCheck.checkNotNull(concurrentTo, "concurrentTo");
        Uri warcRecordIdUri = null;
        try {
            warcRecordIdUri = new Uri("urn:uuid:" + uuid);
            WarcRecord record = WarcRecord.createRecord(writer);
            WarcHeader header = record.header;
            header.warcTypeStr = "update";
            header.warcDate = new Date();
            header.warcWarcinfoIdUri = warcinfoRecordId;
            header.warcRecordIdUri = warcRecordIdUri;
            header.warcTargetUriUri = warcRecordIdUri;
            header.warcConcurrentToList.addAll(concurrentTo);
            header.warcRefersToUri = refersTo;
            header.warcBlockDigest = blockDigest;
            header.contentType = contentType;
            header.contentLength = len;
            writer.writeHeader(record);
            writer.streamPayload(in);
            writer.closeRecord();
        } catch (Exception e) {
            throw new WarcException("Exception while writing WARC metadata record!", e);
        }
        logger.debug("Written Update Record '" + uuid + "'.");

        return warcRecordIdUri;
    }
    
    /**
     * @return The current size of the warc file.
     */
    public long getWarcFileSize() {
        return writerFile.length();
    }
    
    /**
     * @return The Warc file.
     */
    public File getWarcFile() {
        return writerFile;
    }
    
    /**
     * @return The ID for the Warc file.
     */
    public String getWarcFileId() {
        return writerFile.getName();
    }

    /**
     * Close writer, output stream and random access file.
     * @throws WarcException if an exception occurs while closing associated resources
     */
    public void close() throws WarcException {
        try {
            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (writerRafout != null) {
                writerRafout.close();
                writerRafout = null;
            }
            if (writerRaf != null) {
                writerRaf.close();
                writerRaf = null;
            }
        } catch (IOException e) {
            throw new WarcException("Exception closing WarcWriterWrapper!", e);
        }
    }
}
