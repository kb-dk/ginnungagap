package dk.kb.ginnungagap.transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.metadata.Cleaner;
import dk.kb.metadata.representation.MetaGuidResolver;
import dk.kb.metadata.utils.CalendarUtils;
import dk.kb.metadata.utils.ExceptionUtils;
import dk.kb.metadata.utils.FileIdHandler;
import dk.kb.metadata.utils.IdentifierManager;
import dk.kb.metadata.utils.MdIdHandler;
import dk.kb.yggdrasil.exceptions.YggdrasilException;
import dk.kb.yggdrasil.xslt.XmlEntityResolver;
import dk.kb.yggdrasil.xslt.XmlErrorHandler;
import dk.kb.yggdrasil.xslt.XmlValidationResult;
import dk.kb.yggdrasil.xslt.XmlValidator;
import dk.kb.yggdrasil.xslt.XslErrorListener;
import dk.kb.yggdrasil.xslt.XslTransformer;
import dk.kb.yggdrasil.xslt.XslUriResolver;

/**
 * Transforms XML metadata through XSLT scripts.
 */
public class XsltMetadataTransformer implements MetadataTransformer {
    /** The XSLT file with the XML transformation.*/
    protected final File xsltFile;
    /** The XSL transformer.*/
    protected final XslTransformer xslTransformer;
    /** */
    protected final XmlValidator xmlValidator;

    /**
     * Constructor.
     * @param xsltFile The XSLT file for the transformation.
     */
    public XsltMetadataTransformer(File xsltFile) {
        ArgumentCheck.checkExistsNormalFile(xsltFile, "File xsltFile");
        try {
            this.xsltFile = xsltFile;
            this.xslTransformer = XslTransformer.getTransformer(xsltFile);
            this.xmlValidator = new XmlValidator();
        } catch (TransformerConfigurationException e) {
            throw new ArgumentCheck("Cannot instantiate a XSL transformer from the file '" + xsltFile + "'.", e);
        }
    }

    @Override
    public void transformXmlMetadata(InputStream xmlFile, OutputStream out) {
        try {
            Cleaner.cleanStuff();
            
            XslUriResolver uriResolver = new XslUriResolver();
            XslErrorListener errorListener = new XslErrorListener();

            Source source = new StreamSource(xmlFile);
            byte[] bytes = xslTransformer.transform(source, uriResolver, errorListener);
            
            if(errorListener.hasErrors()) {
                throw new IllegalStateException("Failed transformation: fatal errors: " + errorListener.fatalErrors 
                        + ", and other errors: " + errorListener.errors + ", and warnings: " + errorListener.warnings);
            }

            out.write(bytes);
            out.flush();
        } catch (TransformerException e) {
            throw new IllegalStateException("Could not perform the transformation of the metadata", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not deliver the transformed metadata to the output stream.", e);
        }
    }
    
    /**
     * Validates a metadata file.
     * @param metadataFile The file to validate.
     * @throws YggdrasilException If it fails to validate.
     */
    public void validate(InputStream metadata) throws YggdrasilException {
            XmlEntityResolver entityResolver = null;
            XmlErrorHandler errorHandler = new XmlErrorHandler();
            XmlValidationResult validationResult = new XmlValidationResult();

            boolean res = xmlValidator.testDefinedValidity(metadata, entityResolver, errorHandler, validationResult);
            
            if(!res) {
                throw new IllegalStateException("Failed validation: \nfatal errors: " + listToString(errorHandler.fatalErrors) 
                        + "\n other errors: " + listToString(errorHandler.errors) + " \nwarnings: " + listToString(errorHandler.warnings));
            }
    }
    
    private String listToString(List<String> list) {
        if(list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder res = new StringBuilder();
        for(String s : list) {
            res.append(s + "\n");
        }
        return res.toString();
    }
}
