package dk.kb.ginnungagap.exception;

/**
 * The exception, when an issue with the WARC packaging occurs.
 */
public class WarcException extends Exception {

    /**
     * Constructs new WarcException with the specified detail message.
     * @param message The detail message
     */
    public WarcException(String message) {
        super(message);
    }

    /**
     * Constructs new WarcException with the specified
     * detail message and cause.
     * @param message The detail message
     * @param cause The cause
     */
    public WarcException(String message, Throwable cause) {
        super(message, cause);
    }

}
