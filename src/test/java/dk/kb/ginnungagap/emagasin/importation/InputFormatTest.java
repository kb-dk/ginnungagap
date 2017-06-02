package dk.kb.ginnungagap.emagasin.importation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.utils.CalendarUtils;

public class InputFormatTest extends ExtendedTestCase {

    String ARC_FILENAME_1 = "ArcFileName";
    String ARC_FILENAME_2 = "ArcFileName2";
    String CATALOG_NAME = "catalog";
    
    List<String> ARC_1_ARC_UUIDS = new ArrayList<String>();
    List<String> ARC_1_CUMULUS_UUIDS = new ArrayList<String>();
    List<String> ARC_2_ARC_UUIDS = new ArrayList<String>();
    List<String> ARC_2_CUMULUS_UUIDS = new ArrayList<String>();
    
    int NUMBER_OF_RECORDS_PER_ARC_FILE = 3;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
    }
    
    
    @Test
    public void testLoadExampleFile() {
        File inputFile = new File("src/test/resources/import/test_input.txt");
        InputFormat inf = new InputFormat(inputFile);
        Assert.assertEquals(inf.getArcFilenames().size(), 2);
        List<RecordUUIDs> uuids = new ArrayList<RecordUUIDs>();
        for(Collection<RecordUUIDs> uuidList : inf.uuidsForArcFiles.values()) {
            uuids.addAll(uuidList);
        }
        Assert.assertEquals(uuids.size(), 3);
    }
    
    
    @Test
    public void testFile() throws IOException {
        File file = createTestFile();
        InputFormat inf = new InputFormat(file);
        Assert.assertEquals(inf.getArcFilenames().size(), 2);
        Assert.assertTrue(inf.getArcFilenames().contains(ARC_FILENAME_1));
        Assert.assertTrue(inf.getArcFilenames().contains(ARC_FILENAME_2));
        
        Assert.assertEquals(inf.getNotFoundRecordsForArcFile(ARC_FILENAME_1).size(), NUMBER_OF_RECORDS_PER_ARC_FILE);
        Assert.assertEquals(inf.getNotFoundRecordsForArcFile(ARC_FILENAME_2).size(), NUMBER_OF_RECORDS_PER_ARC_FILE);
        
        for(String s : ARC_1_ARC_UUIDS) {
            RecordUUIDs r = inf.getUUIDsForArcRecordUUID(ARC_FILENAME_1, s);
            Assert.assertNotNull(r);
            r.setFound();
        }
        for(String s : ARC_2_ARC_UUIDS) {
            RecordUUIDs r = inf.getUUIDsForArcRecordUUID(ARC_FILENAME_2, s);
            Assert.assertNotNull(r);
            r.setFound();
        }
        Assert.assertEquals(inf.getNotFoundRecordsForArcFile(ARC_FILENAME_1).size(), 0);
        Assert.assertEquals(inf.getNotFoundRecordsForArcFile(ARC_FILENAME_2).size(), 0);

    }
    
    protected File createTestFile() throws IOException {
        StringBuffer res = new StringBuffer();
        for(int i = 0; i < NUMBER_OF_RECORDS_PER_ARC_FILE; i++) {
            String auuid1 = UUID.randomUUID().toString();
            String cuuid1 = UUID.randomUUID().toString();
            String auuid2 = UUID.randomUUID().toString();
            String cuuid2 = UUID.randomUUID().toString();
            
            res.append(ARC_FILENAME_1 + ";" + auuid1 + ";" + cuuid1 + ";" + CATALOG_NAME + "\n");
            res.append(ARC_FILENAME_2 + ";" + auuid2 + ";" + cuuid2 + ";" + CATALOG_NAME + "\n");
            ARC_1_ARC_UUIDS.add(auuid1);
            ARC_1_CUMULUS_UUIDS.add(cuuid1);
            ARC_2_ARC_UUIDS.add(auuid2);
            ARC_2_CUMULUS_UUIDS.add(cuuid2);
        }

        return TestFileUtils.createFileWithContent(res.toString());

    }
    
}
