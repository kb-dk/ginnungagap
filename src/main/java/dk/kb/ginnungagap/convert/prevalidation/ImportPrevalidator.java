package dk.kb.ginnungagap.convert.prevalidation;

import java.io.File;

import dk.kb.ginnungagap.config.ConversionConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusServer;

/**
 * 
 * 
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/742a6c00-908a-11e2-a385-0016357f605f#0##47667682##e113fb8d9b20515056b53eff07c98a45
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/8b0feee0-908a-11e2-a385-0016357f605f#0##52006696##5484079db1b5974c1e2324dcdae82e72
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/68fdace0-908d-11e2-a385-0016357f605f#0##47143591##68dd4da18072d0024622c10d5a563043
 */
public class ImportPrevalidator {
    
    protected static final String SUFFIX_FOUND_FILE = ".found";
    protected static final String SUFFIX_NOT_FOUND_FILE = ".not_found";
    
    protected final CumulusServer server;
    
    protected final File foundArcFiles;
    protected final File notFoundArcFiles;
    
    protected final ConversionConfiguration conf;
    
    
    public ImportPrevalidator(CumulusServer server, ConversionConfiguration conf, String filename) {
        this.server = server;
        this.conf = conf;
        this.foundArcFiles = new File(conf.getPreValidationOutputDir(), filename + SUFFIX_FOUND_FILE);
        this.notFoundArcFiles = new File(conf.getPreValidationOutputDir(), filename + SUFFIX_NOT_FOUND_FILE);
    }
    

//    public void handleArchiveIndexFile(File archiveFile) {
//        
//    }
    
//    public void validateRecord(String catalogName, String guid, String checksum, String length) {
//        CumulusQuery query = CumulusQuery.getQueryForSpecificUUID(catalogName, guid);
//        RecordItemCollection items = server.getItems(catalogName, query);
//        
//    }
}
