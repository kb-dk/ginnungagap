package dk.kb.ginnungagap.workflow;

import java.io.File;

import dk.kb.ginnungagap.emagasin.EmagImportation;

public class ConversionWorkflow implements Workflow {

    protected final File arcFileList;
    protected final File scriptFile;
    protected final EmagImportation converter;
    
    public ConversionWorkflow(EmagImportation converter, File arcFileList, File scriptFile) {
        this.converter = converter;
        this.arcFileList = arcFileList;
        this.scriptFile = scriptFile;
    }
    
}
