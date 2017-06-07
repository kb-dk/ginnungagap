package dk.kb.ginnungagap.emagasin.importation;

import java.io.File;

public class TestOutputFormatter extends OutputFormatter {

    public TestOutputFormatter(File outputDir) {
        super(outputDir);
    }

    public File getSuccessFile() {
        return succesFile;
    }
    
    public File getFailureFile() {
        return failureFile;
    }
}
