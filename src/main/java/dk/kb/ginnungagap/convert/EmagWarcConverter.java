package dk.kb.ginnungagap.convert;

import java.io.File;

import dk.kb.ginnungagap.archive.WarcPacker;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusRecord;
import dk.kb.ginnungagap.cumulus.CumulusServer;
import dk.kb.ginnungagap.transformation.MetadataTransformer;

/**
 * The conversion of the Emag arc-files are done, by using the metadata from the current Cumulus-record,
 * and the content-file of the arc-file.
 * They are packaged in WARC files and preserved in the Bitrepository.
 */
public class EmagWarcConverter extends EmagConverter {

    /** The metadata transformer.*/
    protected final MetadataTransformer transformer;
    /** The warc packer, which also handles the upload. */
    protected final WarcPacker packer;

    
    public EmagWarcConverter(Configuration conf, CumulusServer cumulusServer, String catalogName, WarcPacker packer, 
            MetadataTransformer transformer) {
        super(conf, cumulusServer, catalogName);
        this.transformer = transformer;
        this.packer = packer;
    }

    @Override
    protected void handleRecord(CumulusRecord record, File contentFile) {
        // TODO Auto-generated method stub

    }

}
