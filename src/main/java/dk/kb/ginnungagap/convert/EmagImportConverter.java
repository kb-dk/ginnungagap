package dk.kb.ginnungagap.convert;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;

/**
 * This converter Performs the conversion from Emagasinet by importing the content-file back into Cumulus.
 */
public class EmagImportConverter extends EmagConverter {

    /**
     * Constructor.
     * @param conf The configuration.
     * @param cumulusServer The Cumulus server.
     * @param catalogName The name of the catalog for the record.
     */
    public EmagImportConverter(Configuration conf, CumulusServer cumulusServer, String catalogName) {
        super(conf, cumulusServer, catalogName);
    }

    @Override
    protected void handleRecord(CumulusRecord record, File contentFile) {
        try {
            Files.move(contentFile, record.getFile());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot import the file into Cumulus.", e);
        }
    }
}
