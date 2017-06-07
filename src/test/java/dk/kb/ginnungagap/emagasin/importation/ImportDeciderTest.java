package dk.kb.ginnungagap.emagasin.importation;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;

import org.omg.CORBA.AnySeqHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dk.kb.ginnungagap.cumulus.Constants;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import junit.framework.Assert;

public class ImportDeciderTest extends ExtendedTestCase {

    File testFile;
    
    @BeforeClass
    public void setup() throws IOException {
        TestFileUtils.setup();
        testFile = TestFileUtils.createFileWithContent("GNU");
    }
    
    @Test
    public void testImportDeciderWithTiffFileFormat() {
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(eq(Constants.FieldNames.FILE_FORMAT))).thenReturn("TIFF Image");
        
        boolean res = ImportDecider.shouldImportRecord(record);
        Assert.assertTrue(res);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.FILE_FORMAT));
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testImportDeciderWithMissingNonTiffFile() {
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(eq(Constants.FieldNames.FILE_FORMAT))).thenReturn("RANDOM-FILE-FORMAT");
        when(record.getFile()).thenReturn(new File(TestFileUtils.getTempDir(), UUID.randomUUID().toString()));
        
        boolean res = ImportDecider.shouldImportRecord(record);
        Assert.assertTrue(res);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.FILE_FORMAT));
        verify(record).getFile();
        verifyNoMoreInteractions(record);
    }
    
    @Test
    public void testImportDeciderWithExistingNonTiffFile() {
        CumulusRecord record = mock(CumulusRecord.class);
        when(record.getFieldValue(eq(Constants.FieldNames.FILE_FORMAT))).thenReturn("RANDOM-FILE-FORMAT");
        when(record.getFile()).thenReturn(testFile);
        
        boolean res = ImportDecider.shouldImportRecord(record);
        Assert.assertFalse(res);
        
        verify(record).getFieldValue(eq(Constants.FieldNames.FILE_FORMAT));
        verify(record).getFile();
        verifyNoMoreInteractions(record);
    }

}
