package dk.kb.ginnungagap.retriever;

import java.io.InputStream;

/**
 * 
 */
public class Record {
    
    protected final InputStream metadata;
    protected final InputStream file;
    
    
    public Record(InputStream metadata, InputStream file) {
        this.metadata = metadata;
        this.file = file;
    }

    public InputStream getMetadata() {
        return metadata;
    }
    
    public InputStream getFile() {
        return file;
    }
}
