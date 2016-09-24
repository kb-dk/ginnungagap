package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Configuration for the transformation and of the required fields.
 */
public class TransformationConfiguration {
    /** The directory with the XSLT files.*/
    protected final File xsltDir;
    /** The directory with XSD files.*/
    protected final File xsdDir;
    /** The required fields from Cumulus for making the */
    protected final List<String> requiredFields;
    
    /**
     * Constructor.
     * @param xsltDir The directory with XSLT files.
     * @param xsdDir The directory with XSD files.
     * @param requiredFields The required fields.
     */
    public TransformationConfiguration(File xsltDir, File xsdDir, Collection<String> requiredFields) {
        this.xsdDir = xsdDir;
        this.xsltDir = xsltDir;
        this.requiredFields = new ArrayList<String>(requiredFields);
    }
    
    /** @return The required fields. */
    public List<String> getRequiredFields() {
        return requiredFields;
    }
    /** @return The directory with the XSLT files.*/
    public File getXsltDir() {
        return xsltDir;
    }
    /** @return The directory with XSD files.*/
    public File getXsdDir() {
        return xsdDir;
    }
}
