package dk.kb.ginnungagap.config;

import java.io.File;
import java.util.ArrayList;

public class TestConfiguration extends Configuration {

    TransformationConfiguration transConf;
    BitmagConfiguration bmConf;
    CumulusConfiguration cConf;
    ImportationConfiguration importConf;
    
    public TestConfiguration(File confFile) {
        super(confFile);
    }
    
    public void setTransformationConfiguration(TransformationConfiguration transConf) {
        this.transConf = transConf;
    }

    @Override
    public TransformationConfiguration getTransformationConf() {
        if(this.transConf != null) {
            return transConf;
        }
        return super.getTransformationConf();
    }
    
    public void setBitmagConfiguration(BitmagConfiguration bmConf) {
        this.bmConf = bmConf;
    }

    @Override
    public BitmagConfiguration getBitmagConf() {
        if(this.bmConf != null) {
            return bmConf;
        }
        return super.getBitmagConf();
    }

    public void removeRequiredFields() {
        TransformationConfiguration tc = getTransformationConf();
        transConf = new TransformationConfiguration(tc.getXsltDir(), tc.getXsdDir(), tc.getMetadataTempDir(), 
                new RequiredFields(new ArrayList<String>(), new ArrayList<String>()));
    }
    
    @Override
    public CumulusConfiguration getCumulusConf() {
        if(this.cConf != null) {
            return cConf;
        }
        return super.getCumulusConf();
    }
    public void setCumulusConf(CumulusConfiguration cConf) {
        this.cConf = cConf;
    }
    
    
    @Override
    public ImportationConfiguration getImportationConfiguration() {
        if(this.importConf != null) {
            return importConf;
        }
        return super.getImportationConfiguration();
    }
    public void setImportationConf(ImportationConfiguration iConf) {
        this.importConf = iConf;
    }
}
