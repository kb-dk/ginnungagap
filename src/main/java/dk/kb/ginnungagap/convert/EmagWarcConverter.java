package dk.kb.ginnungagap.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        try {
            record.initFields();
            record.validateRequiredFields(conf.getTransformationConf().getRequiredFields());

            File metadataFile = transformAndValidateMetadata(record);
            packer.packRecord(record, metadataFile);
        } catch (IOException e) {
            throw new IllegalStateException("", e);
        }
    }

    
    /**
     * Transforms and validates the metadata from the Cumulus record.
     * 
     * @param record The record with the metadata to transform and validate.
     * @return The file containing the transformed metadata.
     * @throws IOException If an error occurs when reading or writing the metadata.
     */
    protected File transformAndValidateMetadata(CumulusRecord record) throws IOException {
        String metadataUUID = record.getMetadataGUID();
        File metadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), metadataUUID);
        try (OutputStream os = new FileOutputStream(metadataFile)) {
            File cumulusMetadataFile = new File(conf.getTransformationConf().getMetadataTempDir(), metadataUUID + "_raw.xml");
            transformer.transformXmlMetadata(record.getMetadata(cumulusMetadataFile), os);
            os.flush();
        }
        
        transformer.validate(new FileInputStream(metadataFile));
        
        return metadataFile;
    }
}
