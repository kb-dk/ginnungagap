package dk.kb.ginnungagap.config;

import java.io.File;

public class TransformationConfiguration {
    protected final File xsltDir;
    protected final File xsdDir;
    
    public TransformationConfiguration(File xsltDir, File xsdDir) {
        this.xsdDir = xsdDir;
        this.xsltDir = xsltDir;
    }
}
