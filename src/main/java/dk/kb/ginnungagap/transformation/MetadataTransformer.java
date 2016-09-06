package dk.kb.ginnungagap.transformation;

import java.io.InputStream;
import java.io.OutputStream;

public interface MetadataTransformer {

    /**
     * Transforms metadata, and deliver the content into a file. 
     * @param metadata The stream with metadata.
     * @param out Where the output must be delivered.
     * @return A file with the output metadata.
     */
    public void transformXmlMetadata(InputStream metadata, OutputStream out);
}
