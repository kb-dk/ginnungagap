package dk.kb.ginnungagap.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.Test;

import junit.framework.Assert;

public class StreamUtilsTest extends ExtendedTestCase {
    String TEST_MSG = "THIS IS THE STREAM CONTENT: " + UUID.randomUUID().toString();

    @Test
    public void testConstructor() {
        StreamUtils c = new StreamUtils();
        Assert.assertNotNull(c);
    }
    
    @Test
    public void testCopyInputStreamToOutputStreamSuccess() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(TEST_MSG.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtils.copyInputStreamToOutputStream(in, out);
        
        String res = new String(out.toByteArray());
        Assert.assertEquals(TEST_MSG, res);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCopyInputStreamToOutputStreamFailureInputStreamNull() throws IOException {
        ByteArrayInputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtils.copyInputStreamToOutputStream(in, out);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCopyInputStreamToOutputStreamFailureOutputStreamNull() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(TEST_MSG.getBytes());;
        ByteArrayOutputStream out = null;
        StreamUtils.copyInputStreamToOutputStream(in, out);
    }

}
