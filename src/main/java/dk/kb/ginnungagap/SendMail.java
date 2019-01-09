package dk.kb.ginnungagap;

import java.util.Date;
import java.util.List;
import java.util.Properties;

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

import dk.kb.ginnungagap.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The component for sending mails.
 */
@Component
public class SendMail {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(SendMail.class);

    /** The receivers of the mails.*/
    private List<String> receivers;
    /** The sender of the mails.*/
    private String sender;
    /** The host of the machine, where the mail is being sent.*/
    private String host;

    /** The configuration. Auto-wired.*/
    @Autowired
    protected Configuration conf;
    
    /**
     * Initializes this component.
     */
    @PostConstruct
    protected void initialize() {
        this.receivers = conf.getMailConfiguration().getReceivers();
        this.sender = conf.getMailConfiguration().getSender();
        this.host = "localhost";
    }

    /**
     * Method for sending a mail.
     * It will be sent to all the receivers in the configuration, and it will be from the sender in the configuration.
     * @param subject The subject of the mail.
     * @param content The content of the mail.
     */
    public void sendReport(String subject, String content) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            for(String receiver : receivers) {
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
