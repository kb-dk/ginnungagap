package dk.kb.ginnungagap.workflow;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;

import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusQuery;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;

public class PreservationWorkflowTest extends ExtendedTestCase {

    Configuration conf;
    
    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();
    }
    
    @AfterClass
    public void tearDown() {
//        TestFileUtils.tearDown();
    }
    
    @Test
    public void testNoItems() {
        addDescription("Test stuff!!!");
        
        CumulusServer server = mock(CumulusServer.class);
        XsltMetadataTransformer transformer = mock(XsltMetadataTransformer.class);
        BitmagPreserver preserver = mock(BitmagPreserver.class);
        
        RecordItemCollection recordItemCollection = mock(RecordItemCollection.class);
        
        addStep("Mock the methods", "");
        when(server.getItems(anyString(), any(CumulusQuery.class))).thenReturn(recordItemCollection);
        when(recordItemCollection.iterator()).thenReturn(new ArrayList<Item>().iterator());
        when(recordItemCollection.getLayout()).thenReturn(null);
        when(recordItemCollection.getItemCount()).thenReturn(0);
        
        PreservationWorkflow pw = new PreservationWorkflow(conf.getTransformationConf(), server, transformer, preserver);        
        pw.run();
        
        verifyZeroInteractions(transformer);
        verifyZeroInteractions(preserver);
        
        verify(server).getItems(anyString(), any(CumulusQuery.class));
        verifyNoMoreInteractions(server);
        
        verify(recordItemCollection).iterator();
        verify(recordItemCollection).getLayout();
        verify(recordItemCollection).getItemCount();
        verifyNoMoreInteractions(recordItemCollection);
    }
}
