package dk.kb.ginnungagap.transformation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import dk.kb.ginnungagap.exception.TransformationException;
import dk.kb.ginnungagap.transformation.xml.XmlEntityResolver;
import dk.kb.ginnungagap.transformation.xml.XmlErrorHandler;
import dk.kb.ginnungagap.transformation.xml.XmlValidationResult;
import dk.kb.ginnungagap.transformation.xml.XmlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.utils.StringUtils;

/**
 * Handler for the metadata transformers.
 */
@Component
public class MetadataTransformationHandler {
    
    /** The name for the transformation script for catalog structmaps.*/
    public static final String TRANSFORMATION_SCRIPT_FOR_CATALOG_STRUCTMAP = "transformCatalogStructmap.xsl";
    /** The name for the transformation script for default METS transformation.*/
    public static final String TRANSFORMATION_SCRIPT_FOR_METS = "transformToMets.xsl";
    /** The name for the transformation script for representation METS.*/
    public static final String TRANSFORMATION_SCRIPT_FOR_REPRESENTATION = "transformToMetsRepresentation.xsl";
    /** The name for the transformation script for KB-IDs intellectuel entity metadata.*/
    public static final String TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY = "transformToKbId.xsl";
    
    /** Mapping between the name of the transformations and their transformers.*/
    protected Map<String, MetadataTransformer> transformers;
    /** The directory with the XSLT files.*/
    protected File xsltDir;
    
    /** The xml validator.*/
    protected XmlValidator xmlValidator;
    
    /** The configuration. */
    @Autowired
    protected Configuration conf;
    
    /**
     * Initializes the metadata transformation handler.
     */
    @PostConstruct
    protected void initialize() {
        this.xsltDir = conf.getTransformationConf().getXsltDir();
        this.transformers = new HashMap<String, MetadataTransformer>();
        this.xmlValidator = new XmlValidator();
    }
    
    /**
     * Retrieves the metadata transformer with the given name.
     * @param name The name of the transformation.
     * @return The transformer.
     */
    public MetadataTransformer getTransformer(String name) {
        if(!transformers.containsKey(name)) {
            File xsltFile = new File(xsltDir, name);
            if(!xsltFile.exists()) {
                throw new IllegalArgumentException("The XSLT file '" + xsltFile.getAbsolutePath() + 
                        "' does not exist.");
            }
            transformers.put(name, new MetadataTransformer(xsltFile));
        } 
        return transformers.get(name);
    }
    
    
    /**
     * Validates the transformed metadata.
     * @param metadata The metadata input stream.
     * @throws IOException If an IO exception occurs when trying to validate the metadata.
     * If the validation itself fails, then an IllegalStateException will be thrown instead.
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
        } catch (TransformationException e) {
            throw new IOException("Could not validate the metadata.", e);
        }
    }
    
    /**
     * Retrieves the schema versions of a given XML metadata stream.
     * Will only retrieve each schema location once.
     * @param metadata The XML metadata input stream.
     * @return The collection of unique schemalocations.
     */
    public Collection<String> getMetadataStandards(InputStream metadata) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(metadata);

            return getNamespaceLocation(document);
        } catch(Exception e) {
            throw new IllegalStateException("Could not extract the metadata standards.", e);
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
