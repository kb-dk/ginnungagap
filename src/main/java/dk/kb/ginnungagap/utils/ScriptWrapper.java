package dk.kb.ginnungagap.utils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.exception.RunScriptException;

/**
 * Class for executing an external bash script.
 */
public class ScriptWrapper {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(ScriptWrapper.class);

    /** The file with the script to script.*/
    protected final File scriptFile;
    
    /**
     * Constructor.
     * @param scriptFile The script for to be called.
     */
    public ScriptWrapper(File scriptFile) {
        ArgumentCheck.checkExistsNormalFile(scriptFile, "File scriptFile");
        this.scriptFile = scriptFile;
    }
    
    /**
     * Calls the script with the given argument.
     * @param args The argument(s) for the script.
     * @throws RunScriptException If the scripts fails to run, or gives a non-success termination code (0).
     * It will contain all the output from the script (both std and err output).
     */
    protected void callVoidScript(String ... args) throws RunScriptException {
        StringBuffer command = new StringBuffer();
        command.append("bash ");
        command.append(scriptFile.getAbsolutePath());
        for(String arg : args) {
            command.append(" " + arg);
        }
        try {
            log.info("Executing commandline: " + command.toString());
            Process p = Runtime.getRuntime().exec(command.toString());
            int success = p.waitFor();
            if(success != 0) {
                String errMsg = "Failed to run the script.\nErrors:\n" 
                        + StreamUtils.extractInputStreamAsString(p.getErrorStream()) + "Output:\n"
                        + StreamUtils.extractInputStreamAsString(p.getInputStream());
                throw new RunScriptException(errMsg);
            } else {
                log.debug("Successful execution of script. Received the following output:\nErrors:\n" 
                        + StreamUtils.extractInputStreamAsString(p.getErrorStream()) + "Output:\n"
                        + StreamUtils.extractInputStreamAsString(p.getInputStream()));
            }
        } catch (IOException | InterruptedException e) {
            throw new RunScriptException("Failure during execution of command: '" + command + "'", e);
        }
    }
}
