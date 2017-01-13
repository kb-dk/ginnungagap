package dk.kb.ginnungagap.workflow;

import java.io.File;

import dk.kb.ginnungagap.convert.EmagConverter;

public class ConversionWorkflow implements Workflow {

    protected final File arcFileList;
    protected final File scriptFile;
    protected final EmagConverter converter;
    
    public ConversionWorkflow(EmagConverter converter, File arcFileList, File scriptFile) {
        this.converter = converter;
        this.arcFileList = arcFileList;
        this.scriptFile = scriptFile;
    }
    
}
