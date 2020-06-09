package dk.kb.ginnungagap.archive.bitmag;

import dk.kb.ginnungagap.exception.BitmagException;
import org.bitrepository.access.getchecksums.GetChecksumsClient;
import org.bitrepository.access.getchecksums.conversation.ChecksumsCompletePillarEvent;
import org.bitrepository.access.getfile.GetFileClient;
import org.bitrepository.access.getfileids.GetFileIDsClient;
import org.bitrepository.bitrepositoryelements.ChecksumDataForChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ChecksumSpecTYPE;
import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.bitrepositoryelements.ResultingChecksums;
import org.bitrepository.client.eventhandler.*;
import org.bitrepository.common.utils.CalendarUtils;
import org.bitrepository.common.utils.FileUtils;
import org.bitrepository.common.utils.SettingsUtils;
import org.bitrepository.modify.putfile.PutFileClient;
import org.bitrepository.protocol.FileExchange;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for {@link dk.kb.ginnungagap.archive.bitmag.Bitrepository }
 * Named BitrepositoryTester and not BitrepositoryTest to avoid inclusion in
 * the set of unittests run by Maven.
 *
 * Ignoring every test here, since we do not have the configurations for an active bitrepository instance.
 */
public class BitrepositoryTest extends ExtendedTestCase {

    public static String SETTINGS_FOLDER = "src/test/resources/config/bitmag/)";
    public static String PEM_FILE = "src/test/resources/config/bitmag/client.pem";

    @Test(enabled = false)
    public void testOkYamlFile() throws Exception {
        new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
    }
    
    // Apparently we cannot mock the PutFileClient.
    @Test(enabled = false)
    public void testUpload() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));

        PutFileClient mockClient = mock(PutFileClient.class);

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[6];

                List<ContributorEvent> events = new ArrayList<ContributorEvent>();
                for(String pillarID : SettingsUtils.getPillarIDsForCollection(collectionID)) {
                    ContributorEvent ce = new ContributorCompleteEvent(pillarID, collectionID);
                    events.add(ce);
                    eh.handleEvent(ce);
                }
                eh.handleEvent(new CompleteEvent(collectionID, events));
                return null;
            }
        }).when(mockClient).putFile(anyString(), any(URL.class), anyString(), any(), any(), any(), any(EventHandler.class), anyString());
        FileExchange fe = mock(FileExchange.class);
        
        br.setPutFileClient(mockClient);
        
        when(fe.putFile(any(File.class))).thenReturn(new URL("http://localhost:80/dav/test.txt"));
        br.setFileExchange(fe);
        
        String generatedName = "helloworld" + System.currentTimeMillis() + ".txt";
        File payloadFile = getFileWithContents(generatedName, "Hello World".getBytes());
        boolean success = br.uploadFile(payloadFile, "books");
        assertTrue(success, "Should have returned true for success, but failed");
        payloadFile.delete();
    }

    @Test(enabled = false)
    public void testUploadOnUnknownCollection() throws Exception {
        Bitrepository br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String generatedName = "helloworld" + System.currentTimeMillis() + ".txt";
        File payloadFile = getFileWithContents(generatedName, "Hello World".getBytes());
        boolean success = br.uploadFile(payloadFile, "cars");
        assertFalse(success, "Shouldn't have returned true for success, but succeeded");
        payloadFile.delete();
    }

    @Test(enabled = false)
    public void testGetFileSuccess() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));

        final String FILE_CONTENT = "Hello World";
        GetFileClient mockClient = mock(GetFileClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[4];
                eh.handleEvent(new CompleteEvent(collectionID, Arrays.asList()));
                return null;
            }
        }).when(mockClient).getFileFromFastestPillar(anyString(), anyString(), any(), any(), any(), any());
        br.setGetFileClient(mockClient);
        
        // mock file-exchange
        FileExchange fe = mock(FileExchange.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                File out = (File) invocation.getArguments()[0];
                FileOutputStream fos = new FileOutputStream(out);
                fos.write(FILE_CONTENT.getBytes());
                fos.flush();
                fos.close();
                return null;
            }
        }).when(fe).getFile(any(File.class), anyString());
        when(fe.getURL(anyString())).thenReturn(new URL("http://localhost:80/dav/test.txt"));
        br.setFileExchange(fe);
        
        File fr = br.getFile("helloworld.txt", "books", null);
        byte[] payloadReturned = getPayload(fr);
        String helloWorldReturned = new String(payloadReturned, "UTF-8");
        assertEquals(FILE_CONTENT, helloWorldReturned);
        
        verify(fe).getURL(anyString());
        verify(fe).getFile(any(File.class), anyString());
        verifyNoMoreInteractions(fe);

        verify(mockClient).getFileFromFastestPillar(anyString(), anyString(), any(), any(), any(), anyString());
        verifyNoMoreInteractions(mockClient);
        FileUtils.delete(fr);
    }

    @Test(enabled = false, expectedExceptions = BitmagException.class)
    public void testGetFileFailureOperation() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));

        GetFileClient mockClient = mock(GetFileClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[4];
                eh.handleEvent(new OperationFailedEvent(collectionID, "Is intended to fail", Arrays.asList()));
                return null;
            }
        }).when(mockClient).getFileFromFastestPillar(anyString(), anyString(), any(), any(), any(), any());
        br.setGetFileClient(mockClient);
        
        // mock file-exchange
        FileExchange fe = mock(FileExchange.class);
        when(fe.getURL(anyString())).thenReturn(new URL("http://localhost:80/dav/test.txt"));
        br.setFileExchange(fe);
        
        br.getFile("helloworld.txt", "books", null);
    }

    @Test(enabled = false, expectedExceptions = BitmagException.class)
    public void testGetFileFailuredDownload() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));

        GetFileClient mockClient = mock(GetFileClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[4];
                eh.handleEvent(new CompleteEvent(collectionID, Arrays.asList()));
                return null;
            }
        }).when(mockClient).getFileFromFastestPillar(anyString(), anyString(), any(), any(), any(), any());
        br.setGetFileClient(mockClient);
        
        // mock file-exchange
        FileExchange fe = mock(FileExchange.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException("Fail downloading file.");
            }
        }).when(fe).getFile(any(File.class), anyString());
        when(fe.getURL(anyString())).thenReturn(new URL("http://localhost:80/dav/test.txt"));
        br.setFileExchange(fe);
        
        br.getFile("helloworld.txt", "books", null);
    }

    @Test(enabled = false, expectedExceptions = BitmagException.class)
    public void getFileFailureBadURL() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));

        GetFileClient mockClient = mock(GetFileClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[4];
                eh.handleEvent(new CompleteEvent(collectionID, Arrays.asList()));
                return null;
            }
        }).when(mockClient).getFileFromFastestPillar(anyString(), anyString(), any(), any(), any(), any());
        br.setGetFileClient(mockClient);
        
        // mock file-exchange
        FileExchange fe = mock(FileExchange.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException("Fail downloading file.");
            }
        }).when(fe).getFile(any(File.class), anyString());
        when(fe.getURL(anyString())).thenReturn(new URL("http://localhost:80/dav/test.txt"));
        br.setFileExchange(fe);
        
        br.getFile("helloworld.txt", "books", null);
    }

    @Test(enabled = false)
    public void testGetChecksumsSuccessEmptyResults() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String fileid = "The ID of the file";

        GetChecksumsClient mockClient = mock(GetChecksumsClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[5];

                eh.handleEvent(new CompleteEvent(collectionID, Arrays.asList()));
                return null;
            }
        }).when(mockClient).getChecksums(anyString(), any(), anyString(), any(ChecksumSpecTYPE.class), any(), any(EventHandler.class), anyString());
        br.setGetChecksumsClient(mockClient);

        Map<String, ChecksumsCompletePillarEvent> res = br.getChecksums(fileid, "books");
        
        verify(mockClient).getChecksums(anyString(), any(), anyString(), any(), any(), any(EventHandler.class), anyString());
        verifyNoMoreInteractions(mockClient);

        assertTrue(res.isEmpty(), "Should be empty.");
    }

    @Test(enabled = false)
    public void testGetChecksumsSuccessFullResults() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String fileid = "The ID of the file";
        String collectionID = "books";

        GetChecksumsClient mockClient = mock(GetChecksumsClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                ChecksumSpecTYPE checksumType = (ChecksumSpecTYPE) invocation.getArguments()[4];
                EventHandler eh = (EventHandler) invocation.getArguments()[5];
                ResultingChecksums resCs = new ResultingChecksums();
                ChecksumDataForChecksumSpecTYPE csData = new ChecksumDataForChecksumSpecTYPE();
                csData.setCalculationTimestamp(CalendarUtils.getNow());
                csData.setChecksumValue("checksum".getBytes());
                csData.setFileID(collectionID);
                resCs.getChecksumDataItems().add(csData);

                List<ContributorEvent> events = new ArrayList<ContributorEvent>();
                for(String pillarID : SettingsUtils.getPillarIDsForCollection(collectionID)) {
                    ContributorEvent ce = new ChecksumsCompletePillarEvent(pillarID, collectionID, resCs, checksumType, false);
                    events.add(ce);
                    eh.handleEvent(ce);
                }
                eh.handleEvent(new CompleteEvent(collectionID, events));

                return null;
            }
        }).when(mockClient).getChecksums(anyString(), any(), anyString(), any(ChecksumSpecTYPE.class), any(), any(EventHandler.class), anyString());
        br.setGetChecksumsClient(mockClient);

        Map<String, ChecksumsCompletePillarEvent> res = br.getChecksums(fileid, collectionID);
        
        verify(mockClient).getChecksums(anyString(), any(), anyString(), any(), any(), any(EventHandler.class), anyString());
        verifyNoMoreInteractions(mockClient);

        assertFalse(res.isEmpty(), "Should not be empty.");
        for(String pillarID : SettingsUtils.getPillarIDsForCollection(collectionID)) {
            assertTrue(res.containsKey(pillarID));
            assertNotNull(res.get(pillarID));
        }
    }

    @Test(enabled = false)
    public void testGetChecksumsFailure() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String fileid = "The ID of the file";
        String collectionID = "books";

        GetChecksumsClient mockClient = mock(GetChecksumsClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[5];

                List<ContributorEvent> events = new ArrayList<ContributorEvent>();
                for(String pillarID : SettingsUtils.getPillarIDsForCollection(collectionID)) {
                    ContributorEvent ce = new ContributorFailedEvent(pillarID, collectionID, ResponseCode.FAILURE);
                    events.add(ce);
                    eh.handleEvent(ce);
                }

                eh.handleEvent(new OperationFailedEvent(collectionID, "Failure intended", events));

                return null;
            }
        }).when(mockClient).getChecksums(anyString(), any(), anyString(), any(ChecksumSpecTYPE.class), any(), any(EventHandler.class), anyString());
        br.setGetChecksumsClient(mockClient);

        Map<String, ChecksumsCompletePillarEvent> res = br.getChecksums(fileid, collectionID);
        
        verify(mockClient).getChecksums(anyString(), any(), anyString(), any(), any(), any(EventHandler.class), anyString());
        verifyNoMoreInteractions(mockClient);

        assertTrue(res.isEmpty(), "Should be empty.");
    }

    @Test(enabled = false)
    public void testGetFileIDsSuccess() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String fileid = "The ID of the file";
        String collectionID = "books";

        GetFileIDsClient mockClient = mock(GetFileIDsClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[4];

                eh.handleEvent(new CompleteEvent(collectionID, Arrays.asList()));
                return null;
            }
        }).when(mockClient).getFileIDs(anyString(), any(), anyString(), any(URL.class), any(EventHandler.class));
        br.setGetFileIDsClient(mockClient);

        boolean success = br.existsInCollection(fileid, collectionID);
        assertTrue(success);
    }

    @Test(enabled = false)
    public void testGetFileIDsResponseFailure() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String fileid = "The ID of the file";
        String collectionID = "books";

        GetFileIDsClient mockClient = mock(GetFileIDsClient.class);
        
        // Set the Complete action, when the event is called.
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String collectionID = (String) invocation.getArguments()[0];
                EventHandler eh = (EventHandler) invocation.getArguments()[4];

                List<ContributorEvent> events = new ArrayList<ContributorEvent>();
                for(String pillarID : SettingsUtils.getPillarIDsForCollection(collectionID)) {
                    ContributorEvent ce = new ContributorFailedEvent(pillarID, collectionID, ResponseCode.FAILURE);
                    events.add(ce);
                    eh.handleEvent(ce);
                }

                eh.handleEvent(new OperationFailedEvent(collectionID, "Failure intended", events));
                return null;
            }
        }).when(mockClient).getFileIDs(anyString(), any(), anyString(), any(URL.class), any(EventHandler.class));
        br.setGetFileIDsClient(mockClient);

        boolean success = br.existsInCollection(fileid, collectionID);
        assertFalse(success);
    }

    @Test(enabled = false)
    public void testGetFileIDsCollectionFailure() throws Exception {
//        if (TravisUtils.runningOnTravis()) {
//            return;
//        }
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        String fileid = "The ID of the file";
        String collectionID = "NonExistingCollection";

        boolean success = br.existsInCollection(fileid, collectionID);
        assertFalse(success);
    }

    @Test(enabled = false)
    public void testGetCollections() throws Exception {
        BitrepositoryTestingAPI br = new BitrepositoryTestingAPI(new File(SETTINGS_FOLDER), new File(PEM_FILE));
        List<String> knownCols = br.getKnownCollections();
        assertEquals(knownCols.size(), 5);
    }
    
    private File getFileWithContents(String packageId, byte[] payload) throws IOException {
        File tempDir = new File("temporarydir");
        if (tempDir.isFile()) {
            fail("please remove file '" + tempDir.getAbsolutePath() + "'.");
        }
        tempDir.mkdirs();
        File fr = new File(tempDir, packageId);
        // Remove file if it exists
        if (fr.exists()) {
            fr.delete();
        }
        if (fr.exists()) {
            fail("please remove file '" + fr.getAbsolutePath() + "'.");
        }
        OutputStream ous = new FileOutputStream(fr);
        ous.write(payload);
        ous.close();

        return fr;
    }
    
    private byte[] getPayload(File fr) throws IOException {
        InputStream is = new FileInputStream(fr);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            baos.write(b);
        }
        is.close();
        baos.close();
        return baos.toByteArray();
    }
}
