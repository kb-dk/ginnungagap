package dk.kb.ginnungagap.exception;

/**
 * Exception which should be cast, when running a script fails.
 * Should be used by the {@link dk.kb.ginnungagap.utils.ScriptWrapper} and all its sub-implementations.
 */
@SuppressWarnings("serial")
public class RunScriptException extends Exception {
    
    /**
     * Constructor.
     * @param msg The message of the exception.
     */
    public RunScriptException(String msg) {
        super(msg);
    }
    
    /**
     * Constructor.
     * @param msg The message of the exception.
     * @param cause The cause of the exception.
     */
    public RunScriptException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
