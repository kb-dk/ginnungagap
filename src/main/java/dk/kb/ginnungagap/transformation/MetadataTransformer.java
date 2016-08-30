package dk.kb.ginnungagap.transformation;

import java.io.File;

public interface MetadataTransformer {

    /**
     * Transforms a XML file 
     * @param metadataFile
     * @return A file with the output metadata.
     */
    public File transformXmlMetadata(File metadataFile);
}
