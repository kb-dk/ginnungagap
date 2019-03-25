package dk.kb.ginnungagap;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.workflow.reporting.WorkflowReport;
import dk.kb.yggdrasil.utils.HostName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

/**
 * The component for sending mails.
 */
@Component
public class MailDispatcher {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(MailDispatcher.class);

    /** The host of the machine, where the mail is being sent.*/
    protected String host;

    /** The configuration. Auto-wired.*/
    @Autowired
    protected Configuration conf;

    /**
     * Initializes this component.
     */
    @PostConstruct
    protected void initialize() {
        HostName hostname = new HostName();
        this.host = hostname.getHostName();
        if(this.host.isEmpty()) {
            this.host = "localhost";
        }
    }

    /**
     * Method for sending a mail.
     * It will be sent to all the receivers in the configuration, and it will be from the sender in the configuration.
     * @param subject The subject of the mail.
     * @param content The content of the mail.
     */
    public void sendReport(String subject, String content) {
        try {
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(content);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            sendMail(subject, multipart);
        } catch (MessagingException e) {
            log.error("Encountered an error while trying to send a mail with the subject: " + subject, e);
        }
    }

    /**
     * Sends a workflow report.
     * @param report The report to send.
     */
    public void sendReport(WorkflowReport report) {
        if(report.hasContent()) {
            log.info("Sending workflow report for workflow: " + report.getWorkflowName());
            try  {
                Multipart multipart = new MimeMultipart();

                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText(report.getMainContentForMail());
                multipart.addBodyPart(bodyPart);

                if(report.getNumberOfSuccesses() > 1) {
                    MimeBodyPart successAttachmentPart = new MimeBodyPart();
                    successAttachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(
                            report.getSuccessContentForMail().getBytes(StandardCharsets.UTF_8), "text/text")));
                    successAttachmentPart.setFileName("success.txt");
                    multipart.addBodyPart(successAttachmentPart);
                }

                if(report.getNumberOfFailures() > 1) {
                    MimeBodyPart failureAttachmentPart = new MimeBodyPart();
                    failureAttachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(
                            report.getFailedContentForMail().getBytes(StandardCharsets.UTF_8), "text/text")));
                    failureAttachmentPart.setFileName("success.txt");
                    multipart.addBodyPart(failureAttachmentPart);

                }

                sendMail(report.getMailSubject(), multipart);
            } catch (MessagingException e) {
                log.error("Encountered an error while trying to send a mail for the workflow report: " +
                        report.getWorkflowName() + " : " + report.getMailSubject(), e);
            }
        } else {
            log.info("No report to send for workflow: " + report.getWorkflowName());
        }
    }

    /**
     * Sends the actual mail.
     * @param subject The subject of the mail.
     * @param content The content, in form of a multipart content object.
     * @throws MessagingException If it fails to create or send the mail.
     */
    protected void sendMail(String subject, Multipart content) throws MessagingException {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(conf.getMailConfiguration().getSender()));
        for(String receiver : conf.getMailConfiguration().getReceivers()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        }
        message.setSubject(subject);
        message.setSentDate(new Date());
        message.setContent(content);

        Transport.send(message);
    }
}
