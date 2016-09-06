package dk.kb.ginnungagap.transformation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.yggdrasil.xslt.XslErrorListener;
import dk.kb.yggdrasil.xslt.XslTransformer;
import dk.kb.yggdrasil.xslt.XslUriResolver;

/**
 * Transforms XML metadata through XSLT scripts.
 * 
 * @author jolf
 *
 */
public class XsltMetadataTransformer implements MetadataTransformer {
    /** The XSLT file with the XML transformation.*/
    protected final File xsltFile;
    /** The XSL transformer.*/
    protected final XslTransformer xslTransformer;

    /**
     * Constructor.
     * @param xsltFile The XSLT file for the transformation.
     */
    public XsltMetadataTransformer(File xsltFile) {
        try {
            this.xsltFile = xsltFile;
            this.xslTransformer = XslTransformer.getTransformer(xsltFile);
        } catch (TransformerConfigurationException e) {
            throw new ArgumentCheck("Cannot instantiate a XSL transformer from the file '" + xsltFile + "'.", e);
        }
    }

    @Override
    public void transformXmlMetadata(InputStream xmlFile, OutputStream out) {
        try {
            XslUriResolver uriResolver = new XslUriResolver();
            XslErrorListener errorListener = new XslErrorListener();

            Source source = new StreamSource(xmlFile);
            byte[] bytes = xslTransformer.transform(source, uriResolver, errorListener);

            out.write(bytes);
            out.flush();
        } catch (TransformerException e) {
            throw new IllegalStateException("Could not perform the transformation of the metadata", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not deliver the transformed metadata to the output stream.", e);
        }
    }
}
