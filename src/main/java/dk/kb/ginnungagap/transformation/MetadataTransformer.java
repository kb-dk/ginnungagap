package dk.kb.ginnungagap.transformation;

import java.io.InputStream;
import java.io.OutputStream;

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
}
