package dk.kb.ginnungagap.transformation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.utils.StringUtils;
import dk.kb.metadata.Cleaner;
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
    /** The xml validator.*/
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
        transform(xmlFile, out, xslTransformer);
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

    /**
     * Validates a metadata file.
     * @param metadata The inputstream with the metadata to validate.
     * @throws IOException If it fails to validate.
     */
    public void validate(InputStream metadata) throws IOException {
        XmlEntityResolver entityResolver = null;
        XmlErrorHandler errorHandler = new XmlErrorHandler();
        XmlValidationResult validationResult = new XmlValidationResult();

        try {
            boolean res = xmlValidator.testDefinedValidity(metadata, entityResolver, errorHandler, validationResult);

            if(!res) {
                String fatalErrors = StringUtils.listToString(errorHandler.fatalErrors, "\n");
                String errors = StringUtils.listToString(errorHandler.errors, "\n");
                String warnings = StringUtils.listToString(errorHandler.warnings, "\n");
                throw new IllegalStateException("Failed validation: \nfatal errors: " + fatalErrors
                        + "\n other errors: " + errors + " \nwarnings: " + warnings);
            }
        } catch (YggdrasilException e) {
            throw new IOException("Could not validate the metadata.", e);
        }
    }
    
    /**
     * Retrieves the schema versions of a given XML metadata stream.
     * Will only retrieve each schema location once.
     * @param metadata The XML metadata input stream.
     * @return The collection of unique schemalocations.
     * @throws IOException If the input stream cannot be loaded as XML objects.
     */
    public Collection<String> getMetadataStandards(InputStream metadata) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(metadata);

            return getNamespaceLocation(document);
        } catch(ParserConfigurationException | SAXException e) {
            throw new IOException("Could not extract the metadata standards.", e);
        }
    }
    
    /**
     * Retrieves the namespace locations of a given node, and all the subnodes.
     * @param node The node.
     * @return The namespaces of the current node and all its subnodes.
     */
    protected Set<String> getNamespaceLocation(Node node) {
        Set<String> res = new HashSet<String>();
        for(int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node n = node.getChildNodes().item(i);
            if(n.hasChildNodes()) {
                res.addAll(getNamespaceLocation(n));
            }
        }
        if(node.hasAttributes()) {
            Node schemaLocation = node.getAttributes().getNamedItemNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, 
                    "schemaLocation");
            if(schemaLocation != null) {
                res.add(schemaLocation.getNodeValue());
            }
        }
        
        return res;
    }
}
