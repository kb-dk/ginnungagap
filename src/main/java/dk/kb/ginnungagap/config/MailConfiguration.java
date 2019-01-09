package dk.kb.ginnungagap.config;

import dk.kb.yggdrasil.utils.HostName;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Configuration for sending mails.
 */
public class MailConfiguration {
    /** The sender of the mails.*/
    protected final String sender;
    /** The receivers of the mails.*/
    protected final List<String> receivers;

    /**
     * Constructor.
     * @param sender The sender of the mails.
     * @param receivers The receivers of the mails.
     */
    public MailConfiguration(String sender, List<String> receivers) {
        this.sender = sender;
        this.receivers = Collections.unmodifiableList(receivers);
    }

    /** @return The sender of the mails.*/
    public String getSender() {
        return sender;
    }
    /** @return The receivers of the mails.*/
    public List<String> getReceivers() {
        return receivers;
    }
}
