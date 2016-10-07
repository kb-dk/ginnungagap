package dk.kb.metadata;

import dk.kb.metadata.representation.MetaGuidResolver;
import dk.kb.metadata.utils.ExceptionUtils;
import dk.kb.metadata.utils.FileIdHandler;
import dk.kb.metadata.utils.IdentifierManager;
import dk.kb.metadata.utils.MdIdHandler;

public class Cleaner {

    public static void cleanStuff() {
        MetaGuidResolver.clear();
        ExceptionUtils.clean();
        FileIdHandler.clean();
        IdentifierManager.clean();
        MdIdHandler.clean();
    }
}
