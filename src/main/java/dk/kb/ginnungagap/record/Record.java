package dk.kb.ginnungagap.record;

import java.io.File;
import java.io.InputStream;

import dk.kb.ginnungagap.config.RequiredFields;

/**
 * Record 
 */
public interface Record {
    /**
     * Retrieves the metadata as an input stream.
     * @return The input stream with the metadata.
     */
    InputStream getMetadata();
    /**
     * Retrieves the content file.
     * @return The content file.
     */
    File getFile();
    
    /**
     * @return The identifier for this record.
     */
    String getID();
    /**
     * Validates the record against the given required fields.
     * @param fields The required fields validate against.
     * @throws IllegalStateException If any of the requirements are not met.
     */
    void validateRequiredFields(RequiredFields fields) throws IllegalStateException;
    /**
     * Sets the preservation status to failure.
     * @param qaError The error message for the failure state.
     */
    void setPreservationFailed(String qaError);
    /**
     * Sets the preservation status to successfully finished.
     */
    void setPreservationFinished();
    /**
     * Sets the value for the preservation package for the resource.
     * @param filename The name of the file containing the resource (content file).
     */
    void setPreservationResourcePackage(String filename);
    /**
     * Sets the value for the preservation package for the metadata.
     * @param filename The name of the file containing the metadata.
     */
    void setPreservationMetadataPackage(String filename);
}
