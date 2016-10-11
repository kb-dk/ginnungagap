package dk.kb.ginnungagap.archive;

import java.io.File;

public interface Archive {
    public void uploadFile(File file, String collectionId);
}
