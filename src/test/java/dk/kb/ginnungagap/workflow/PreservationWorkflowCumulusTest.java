package dk.kb.ginnungagap.workflow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.canto.cumulus.Asset;
import com.canto.cumulus.Cumulus;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.FieldTypes;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.Layout;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.constants.CombineMode;
import com.canto.cumulus.constants.FindFlag;
import com.canto.cumulus.fieldvalue.AssetReference;
import com.canto.cumulus.fieldvalue.StringEnumFieldValue;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.cumulus.FieldExtractor;
import dk.kb.ginnungagap.testutils.SetupCumulusTests;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TravisUtils;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;
import junit.framework.Assert;

public class PreservationWorkflowCumulusTest extends ExtendedTestCase {

    TestConfiguration conf;
    File contentFile;
    CumulusServer server;
    
    String passwordFilePath = System.getenv("HOME") + "/cumulus-password.yml";
    String catalogName = "Letters OM";

    @BeforeClass
    public void setup() throws Exception {
        if(TravisUtils.runningOnTravis()) {
            throw new SkipException("Skipping this test");
        }
        File passwordFile = new File(passwordFilePath);
        if(!passwordFile.isFile()) {
            throw new SkipException("Cannot connect to Cumulus without the password-file: " + passwordFilePath);
        }
        Cumulus.CumulusStart();
        TestFileUtils.setup();

        conf = SetupCumulusTests.getConfiguration(catalogName);
        server = new CumulusServer(conf.getCumulusConf());
//        conf = TestFileUtils.createTempConf();
    }
    
    @BeforeMethod
    public void setupMethod() throws IOException {

        contentFile = TestFileUtils.createFileWithContent("This is the content");        
    }
    
//    @AfterMethod
    public void tearDownMethod() {
        TestFileUtils.tearDown();
    }
    
    @AfterClass
    public void tearDownClass() {
        Cumulus.CumulusStop();
    }
    
    @Test
    public void testStuff() throws Exception {
        
        XsltMetadataTransformer transformer = new XsltMetadataTransformer(new File("src/main/resources/scripts/xslt/transformToMets.xsl"));
        XsltMetadataTransformer representationTransformer = new XsltMetadataTransformer(new File("src/main/resources/scripts/xslt/transformToMetsRepresentation.xsl"));
        BitmagPreserver preserver = mock(BitmagPreserver.class);

        
        PreservationWorkflow workflow = new PreservationWorkflow(conf.getTransformationConf(), server, transformer, representationTransformer, preserver);
        String query = "GUID\tcontains\ta05d8e80-6d63-11df-8c95-0016357f605f"
                + "\nand\t" + Constants.FieldNames.CATALOG_NAME + "\tis\t" + catalogName;
        EnumSet<FindFlag> flags = EnumSet.of(FindFlag.FIND_MISSING_FIELDS_ARE_ERROR, FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);

        RecordItemCollection items = server.getItems(catalogName, new CumulusQuery(query, flags, CombineMode.FIND_NEW));

        FieldExtractor fe = new FieldExtractor(items.getLayout(), server, catalogName);
        
        
        for(Item item : items) {
            CumulusRecord record = new TestCumulusRecord(fe, item);
            workflow.sendRecordToPreservation(record);
        }

    }
    
    class TestCumulusRecord extends CumulusRecord {

        public TestCumulusRecord(FieldExtractor fe, Item item) {
            super(fe, item);
        }
        
        @Override
        public File getFile() {
            return new File(passwordFilePath);
        }
    }
}
