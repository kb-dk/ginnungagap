package dk.kb.ginnungagap.exception;

/**
 * The exception when an issue with the XML transformation or validation occurs.
 */
public class TransformationException extends Exception {

    /**
     * Constructs new TransformationException with the specified detail message.
     * @param message The detail message
     */
    public TransformationException(String message) {
        super(message);
    }

    /**
     * Constructs new TransformationException with the specified
     * detail message and cause.
     * @param message The detail message
     * @param cause The cause
     */
    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }

}
