package dk.kb.metadata;

import dk.kb.metadata.utils.ExceptionUtils;
import dk.kb.metadata.utils.FileIdHandler;
import dk.kb.metadata.utils.IdentifierManager;
import dk.kb.metadata.utils.MdIdHandler;

/**
 * Method for cleaning the metadata.
 */
public class Cleaner {

    /**
     * Cleans all variables used for the java helper methods for the metadata transformation.
     */
    public static void cleanStuff() {
        ExceptionUtils.clean();
        FileIdHandler.clean();
        IdentifierManager.clean();
        MdIdHandler.clean();
    }
}
