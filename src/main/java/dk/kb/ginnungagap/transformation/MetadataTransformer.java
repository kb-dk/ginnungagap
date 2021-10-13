package dk.kb.ginnungagap.transformation;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.transformation.xml.XslErrorListener;
import dk.kb.ginnungagap.transformation.xml.XslTransformer;
import dk.kb.ginnungagap.transformation.xml.XslUriResolver;
import dk.kb.metadata.Cleaner;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Transforms XML metadata through XSLT scripts.
 */
public class MetadataTransformer {
    /** The XSLT file with the XML transformation.*/
    protected final File xsltFile;
    /** The XSL transformer.*/
    protected final XslTransformer xslTransformer;

    /**
     * Constructor.
     * @param xsltFile The XSLT file for the transformation.
     */
    public MetadataTransformer(File xsltFile) {
        ArgumentCheck.checkExistsNormalFile(xsltFile, "File xsltFile");
        try {
            this.xsltFile = xsltFile;
            this.xslTransformer = XslTransformer.getTransformer(xsltFile);
        } catch (TransformerException e) {
            throw new ArgumentCheck("Cannot instantiate a XSL transformer from the file '" + xsltFile + "'.", e);
        }
    }

    /**
     * Transforms metadata, and deliver the content into a file. 
     * @param metadata The stream with metadata.
     * @param out Where the output must be delivered.
     */
    public void transformXmlMetadata(InputStream metadata, OutputStream out) {
        transform(metadata, out, xslTransformer);
    }
    
    /**
     * Performs the transformation of the metadata based on the given xsl transformation.
     * @param xmlFile The metadata input stream.
     * @param out The output stream where the transformed metadata is delivered.
     * @param transformer The transformer for the metadata.
     */
    protected void transform(InputStream xmlFile, OutputStream out, XslTransformer transformer) {
        try {
            Cleaner.cleanStuff();

            XslUriResolver uriResolver = new XslUriResolver();
            XslErrorListener errorListener = new XslErrorListener();

            Source source = new StreamSource(xmlFile);
            byte[] bytes = transformer.transform(source, uriResolver, errorListener);

            out.write(bytes);
            out.flush();
            
            if(errorListener.hasErrors()) {
                throw new IllegalStateException("Failed transformation: fatal errors: " + errorListener.fatalErrors 
                        + ", and other errors: " + errorListener.errors + ", and warnings: " + errorListener.warnings);
            }
        } catch (TransformerException e) {
            throw new IllegalStateException("Could not perform the transformation of the metadata", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not deliver the transformed metadata to the output stream.", e);
        }
    }
}
