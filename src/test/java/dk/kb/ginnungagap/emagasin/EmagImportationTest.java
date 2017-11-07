package dk.kb.ginnungagap.emagasin;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.emagasin.importation.InputFormat;
import dk.kb.ginnungagap.emagasin.importation.OutputFormatter;
import dk.kb.ginnungagap.emagasin.importation.TestOutputFormatter;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import junit.framework.Assert;

public class EmagImportationTest extends ExtendedTestCase {

    String digitalObjectUrl = "digitalobject://Uid:dk:kb:doms:2007-01/7dfe7540-6ab1-11e2-83ab-005056887b70#0";
    String nonDigitalObjectUrl = "metadata://Uid:dk:kb:doms:2007-01/07f40370-a0c1-11e1-81c1-0016357f605f#2";
    
    String testUid = "44f4c8e0-abfc-11e6-8df8-005056882ec3";
    File origArcFile = new File("src/test/resources/conversion/KBDOMS-test.arc");
    File arcFile;
    File contentFile;

    TestConfiguration conf;
    String catalogName = "asdasdfasdf";
    EmagasinRetriever emagasinRetriever;
    CumulusServer cumulusServer;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
        contentFile = TestFileUtils.createFileWithContent("This is the random content: " + UUID.randomUUID().toString());
    }
    
    @BeforeMethod
    public void setupMethod() throws IOException {
        cumulusServer = mock(CumulusServer.class);
        emagasinRetriever = mock(EmagasinRetriever.class);
        arcFile = TestFileUtils.copyFileToTemp(origArcFile);
    }
    
    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }
    
    @Test
    public void testIsDigitalObject() {
        addDescription("Test which ARC-record URLs are digital objects");
        InputFormat inputFormat = mock(InputFormat.class);
        OutputFormatter outputFormat = mock(OutputFormatter.class);
        EmagImportation importer = new EmagImportation(conf, cumulusServer, emagasinRetriever, inputFormat, outputFormat);
        
        addStep("Test urls", "should determine which is a digital object");
        Assert.assertTrue(importer.isDigitalObject(digitalObjectUrl));
        Assert.assertFalse(importer.isDigitalObject(nonDigitalObjectUrl));
    }
    
    @Test
    public void testSuccessCase() throws Exception {
        addDescription("Test the succes case, where the record is imported");
        addStep("define variables for this test", "");
        String arcFileName = arcFile.getName();
        String arcUUID = testUid;
        String cUUID = testUid;
        String catalog = UUID.randomUUID().toString();

        String path = new File(TestFileUtils.getTempDir(), testUid).getAbsolutePath();
        
        addStep("Mock the cumulus-server, cumulus-record and emagasin-retriever methods", "");
        CumulusRecord record = mock(CumulusRecord.class);
        when(cumulusServer.findCumulusRecord(eq(catalog), eq(testUid))).thenReturn(record);
        
        when(record.getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE))).thenReturn(path);
        when(record.getFieldValue(eq(Constants.FieldNames.FILE_FORMAT))).thenReturn("TIFF Image");
        
        when(emagasinRetriever.extractArcFile(anyString())).thenReturn(arcFile);
        
        addStep("Create input file", "The file to the input");
        File inputFile = TestFileUtils.createFileWithContent(arcFileName + ";" + arcUUID + ";" + cUUID + ";" + catalog + "\n");
        
        InputFormat inputFormat = new InputFormat(inputFile);
        TestOutputFormatter outputFormat = new TestOutputFormatter(TestFileUtils.getTempDir());
        
        EmagImportation importer = new EmagImportation(conf, cumulusServer, emagasinRetriever, inputFormat, outputFormat);
        importer.run();

        addStep("Verify the number of lines in the output files", "");
        Assert.assertEquals(2, TestFileUtils.numberOfLinesInFile(outputFormat.getSuccessFile()));
        Assert.assertEquals(1, TestFileUtils.numberOfLinesInFile(outputFormat.getFailureFile()));

        addStep("Verify the calls to the ", "");
        verify(cumulusServer).findCumulusRecord(eq(catalog), eq(testUid));
        verifyNoMoreInteractions(cumulusServer);
        
        verify(emagasinRetriever).extractArcFile(arcFileName);
        verifyNoMoreInteractions(emagasinRetriever);
        
        Assert.assertEquals(0, inputFormat.getNotFoundRecordsForArcFile(arcFileName).size());
        
        verify(record).getFieldValueForNonStringField(eq(Constants.FieldNames.ASSET_REFERENCE));
        verify(record).updateAssetReference();
        verify(record).setStringValueInField(eq(Constants.FieldNames.QA_ERROR), anyString());
        verify(record).getFieldValue(eq(Constants.FieldNames.FILE_FORMAT));
        verify(record).getUUID();
        verify(record).setNewAssetReference(any(File.class));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testNoCumulusRecordFoundFailure() throws Exception {
        addDescription("Test the case, when the record is not found in Cumulus.");
        addStep("define variables for this test", "");
        String arcFileName = arcFile.getName();
        String arcUUID = testUid;
        String cUUID = testUid;
        String catalog = UUID.randomUUID().toString();

        addStep("Mock the cumulus-server, cumulus-record and emagasin-retriever methods", "");
        
        when(emagasinRetriever.extractArcFile(anyString())).thenReturn(arcFile);
        
        addStep("Create input file", "The file to the input");
        File inputFile = TestFileUtils.createFileWithContent(arcFileName + ";" + arcUUID + ";" + cUUID + ";" + catalog + "\n");
        
        InputFormat inputFormat = new InputFormat(inputFile);
        TestOutputFormatter outputFormat = new TestOutputFormatter(TestFileUtils.getTempDir());
        
        EmagImportation importer = new EmagImportation(conf, cumulusServer, emagasinRetriever, inputFormat, outputFormat);
        importer.run();

        addStep("Verify the number of lines in the output files", 
                "Only header-line for success-file; header-line and failure-record for failure-file");
        Assert.assertEquals(1, TestFileUtils.numberOfLinesInFile(outputFormat.getSuccessFile()));
        Assert.assertEquals(2, TestFileUtils.numberOfLinesInFile(outputFormat.getFailureFile()));

        Assert.assertEquals(0, inputFormat.getNotFoundRecordsForArcFile(arcFileName).size());

        addStep("Verify the calls to the ", "");
        verify(cumulusServer).findCumulusRecord(eq(catalog), eq(testUid));
        verifyNoMoreInteractions(cumulusServer);
        
        verify(emagasinRetriever).extractArcFile(arcFileName);
        verifyNoMoreInteractions(emagasinRetriever);
    }
    
    @Test
    public void testNoArcRecordFoundFailure() throws Exception {
        addDescription("Test the case, when the record is not found in the ARC file.");
        addStep("define variables for this test", "");
        String arcFileName = arcFile.getName();
        String arcUUID = UUID.randomUUID().toString();
        String cUUID = testUid;
        String catalog = UUID.randomUUID().toString();

        addStep("Mock the cumulus-server, cumulus-record and emagasin-retriever methods", "");
        
        when(emagasinRetriever.extractArcFile(anyString())).thenReturn(arcFile);
        
        addStep("Create input file", "The file to the input");
        File inputFile = TestFileUtils.createFileWithContent(arcFileName + ";" + arcUUID + ";" + cUUID + ";" + catalog + "\n");
        
        InputFormat inputFormat = new InputFormat(inputFile);
        TestOutputFormatter outputFormat = new TestOutputFormatter(TestFileUtils.getTempDir());
        
        EmagImportation importer = new EmagImportation(conf, cumulusServer, emagasinRetriever, inputFormat, outputFormat);
        importer.run();

        addStep("Verify the number of lines in the output files", 
                "Only header-line for success-file; header-line and failure-record for failure-file");
        Assert.assertEquals(1, TestFileUtils.numberOfLinesInFile(outputFormat.getSuccessFile()));
        Assert.assertEquals(2, TestFileUtils.numberOfLinesInFile(outputFormat.getFailureFile()));

        Assert.assertEquals(1, inputFormat.getNotFoundRecordsForArcFile(arcFileName).size());

        addStep("Verify the calls to the ", "");
        verifyZeroInteractions(cumulusServer);
        
        verify(emagasinRetriever).extractArcFile(arcFileName);
        verifyNoMoreInteractions(emagasinRetriever);
    }

}
