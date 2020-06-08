package dk.kb.ginnungagap.exception;

/**
 * The exception, when an issue with the YAML reading occurs.
 */
public class YamlException extends Exception {

    /**
     * Constructs new YamlException with the specified detail message.
     * @param message The detail message
     */
    public YamlException(String message) {
        super(message);
    }

    /**
     * Constructs new YamlException with the specified
     * detail message and cause.
     * @param message The detail message
     * @param cause The cause
     */
    public YamlException(String message, Throwable cause) {
        super(message, cause);
    }

}
