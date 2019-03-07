package dk.kb.ginnungagap;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.yggdrasil.utils.HostName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", conf.getMailConfiguration().getSender());
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(host));
            for(String receiver : conf.getMailConfiguration().getReceivers()) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            }
            message.setSubject(subject);
            message.setSentDate(new Date());
            
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(content);
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            message.setContent(multipart);
            
            Transport.send(message);
        } catch (MessagingException e) {
            log.error("Encountered an error while trying to send a mail with the subject: " + subject, e);
        }
    }
}
