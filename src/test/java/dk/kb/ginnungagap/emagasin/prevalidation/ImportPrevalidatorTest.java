package dk.kb.ginnungagap.emagasin.prevalidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.emagasin.prevalidation.CumulusExtractRecord;
import dk.kb.ginnungagap.emagasin.prevalidation.EmagasinExtractRecord;
import dk.kb.ginnungagap.emagasin.prevalidation.ImportPrevalidator;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.utils.FileUtils;

public class ImportPrevalidatorTest extends ExtendedTestCase {

    File baseDir;
    File outputDir;
    
    File cumulusExtractionFile;
    File emagasinExtractionFile;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        baseDir = TestFileUtils.getTempDir();
        cumulusExtractionFile = new File("src/test/resources/prevalidation/test_cumulus_extract.txt");
        emagasinExtractionFile = new File("src/test/resources/prevalidation/test_emagasin_extract.txt");
        outputDir = new File(baseDir, "output");
    }
    
    @BeforeMethod
    public void setupMethod() {
        if(outputDir != null && outputDir.exists()) {
            TestFileUtils.delete(outputDir);
        }
        outputDir = FileUtils.getDirectory(outputDir.getAbsolutePath());
    }
    
    @AfterClass
    public void tearDownClass() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testInstantiation() {
        ImportPrevalidator prevalidator = new ImportPrevalidator(outputDir);
        Assert.assertNotNull(prevalidator);
        Assert.assertEquals(outputDir.list().length, 4);
    }
    
    @Test
    public void testCumulusRecords() throws Exception {
        ImportPrevalidator prevalidator = new ImportPrevalidator(outputDir);
        
        BufferedReader cumulusRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(cumulusExtractionFile)));
        CumulusExtractRecord record;
        while((record = prevalidator.getNextCumulusExtractRecord(cumulusRecordReader)) != null) {
            System.err.println(record.getLine());
        }
        
    }

    @Test
    public void testEmagasinRecords() throws Exception {
        ImportPrevalidator prevalidator = new ImportPrevalidator(outputDir);
        
        BufferedReader emagasinRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(emagasinExtractionFile)));
        EmagasinExtractRecord record;
        while((record = prevalidator.getNextEmagasinExtractRecord(emagasinRecordReader)) != null) {
            System.err.println(record.getLine());
        }
    }

    @Test
    public void testCompare() throws FileNotFoundException {
        ImportPrevalidator prevalidator = new ImportPrevalidator(outputDir);
        
        BufferedReader cumulusRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(cumulusExtractionFile)));
        BufferedReader emagasinRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(emagasinExtractionFile)));
        prevalidator.compare(emagasinRecordReader, cumulusRecordReader);
    }
    
    @Test(enabled = false)
    public void testCompleteCompare() throws FileNotFoundException {        
        File origOutputDir = new File(baseDir, "origOutputs");
        File origCumulusExtractFile = new File("/home/jolf/data/cumulus_extract.txt");
        File origEmagasinExtractFile = new File("/home/jolf/data/emagasin_extract.txt");
        
        ImportPrevalidator prevalidator = new ImportPrevalidator(origOutputDir);
        
        BufferedReader cumulusRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(origCumulusExtractFile)));
        BufferedReader emagasinRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(origEmagasinExtractFile)));
        prevalidator.compare(emagasinRecordReader, cumulusRecordReader);
    }

}
