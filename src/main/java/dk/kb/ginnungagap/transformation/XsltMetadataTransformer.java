package dk.kb.ginnungagap.transformation;

import java.io.File;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.transformation.xsl.XslTransformer;

public class XsltMetadataTransformer implements MetadataTransformer {

    protected final File xsltFile;
    protected final XslTransformer xslTransformer;
    
    public XsltMetadataTransformer(File xsltFile) {
        try {
            this.xsltFile = xsltFile;
            this.xslTransformer = XslTransformer.getTransformer(xsltFile);
        } catch (Exception e) {
            throw new ArgumentCheck("Cannot instantiate a XSL transformer from the file '" + xsltFile + "'.", e);
        }
    }
    
    @Override
    public File transformXmlMetadata(File xmlFile) {
//        xslTransformer
        // TODO Auto-generated method stub
        return null;
    }

}
