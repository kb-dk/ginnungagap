package dk.kb.ginnungagap.exception;

/**
 * The exception, when an issue with the Bitrepository client implementation occurs.
 */
public class BitmagException extends Exception {

    /**
     * Constructs new BitmagException with the specified detail message.
     * @param message The detail message
     */
    public BitmagException(String message) {
        super(message);
    }

    /**
     * Constructs new BitmagException with the specified
     * detail message and cause.
     * @param message The detail message
     * @param cause The cause
     */
    public BitmagException(String message, Throwable cause) {
        super(message, cause);
    }

}
