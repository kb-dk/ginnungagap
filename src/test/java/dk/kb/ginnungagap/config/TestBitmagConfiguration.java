package dk.kb.ginnungagap.config;

import java.io.File;

public class TestBitmagConfiguration extends BitmagConfiguration {

    public Integer testWarcFileSizeLimit;
    
    public TestBitmagConfiguration(File settingsDir, File privateKeyFile, int maxFailingPillars, int warcFileSizeLimit,
            File tempDir, String algorithm) {
        super(settingsDir, privateKeyFile, maxFailingPillars, warcFileSizeLimit, tempDir, algorithm);
    }

    public void setWarcFileSizeLimit(int limit) {
        this.testWarcFileSizeLimit = limit;
    }
    @Override
    public int getWarcFileSizeLimit() {
        if(testWarcFileSizeLimit == null) {
            return super.getWarcFileSizeLimit();
        }
        return testWarcFileSizeLimit;
    }
    
}
