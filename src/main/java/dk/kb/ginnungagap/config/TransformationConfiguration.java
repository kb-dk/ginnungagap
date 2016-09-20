package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransformationConfiguration {
    protected final File xsltDir;
    protected final File xsdDir;
    protected final List<String> requiredFields;
    
    public TransformationConfiguration(File xsltDir, File xsdDir, Collection<String> requiredFields) {
        this.xsdDir = xsdDir;
        this.xsltDir = xsltDir;
        this.requiredFields = new ArrayList<String>(requiredFields);
    }
}
