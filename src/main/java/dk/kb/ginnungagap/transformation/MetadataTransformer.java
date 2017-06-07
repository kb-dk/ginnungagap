package dk.kb.ginnungagap.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Interface for the metadata transformer.
 */
public interface MetadataTransformer {

    /**
     * Transforms metadata, and deliver the content into a file. 
     * @param metadata The stream with metadata.
     * @param out Where the output must be delivered.
     */
    void transformXmlMetadata(InputStream metadata, OutputStream out);
    
    /**
     * Validates the transformed metadata.
     * @param is The metadata input stream.
     * @throws IOException If an IO exception occurs when trying to validate the metadata.
     * If the validation itself fails, then an IllegalStateException will be thrown instead.
     */
    void validate(InputStream is) throws IOException;
    
    /**
     * Retrieves the metadata standards of a transformed metadata input-stream. 
     * @param is The input stream to the transformed metadata.
     * @return The list of standards used within the transformed metadata.
     * @throws IOException If it fails to read the input-stream or extract the name of the metadata standards.
     */
    Collection<String> getMetadataStandards(InputStream is) throws IOException;
}
