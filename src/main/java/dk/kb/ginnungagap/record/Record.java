package dk.kb.ginnungagap.record;

import java.io.InputStream;

import dk.kb.ginnungagap.config.RequiredFields;

/**
 * 
 */
public interface Record {
    
    InputStream getMetadata();
    InputStream getFile();
    
    void validateRequiredFields(RequiredFields fields);
}
