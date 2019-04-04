package dk.kb.ginnungagap;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.ConfigurationTest;
import dk.kb.ginnungagap.config.MailConfiguration;
import dk.kb.ginnungagap.config.TestConfiguration;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TestSystemUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;

public class MailDispatcherTest extends ExtendedTestCase {

    TestConfiguration conf;

    @BeforeClass
    public void setup() {
        TestFileUtils.setup();
        conf = TestFileUtils.createTempConf();

        MailConfiguration mConf = new MailConfiguration("ginnugagap@kb.dk", Arrays.asList("jolf@kb.dk"));
        conf.setMailConfiguration(mConf);
    }

    @AfterClass
    public void tearDown() {
        TestFileUtils.tearDown();
    }

    @Test(enabled = false)
    public void testSendingMail() throws Exception {
        MailDispatcher mailer = new MailDispatcher();
        mailer.conf = conf;
        mailer.initialize();

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText("content");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        mailer.sendMail("Subject", multipart);
    }
}
