package dk.kb.ginnungagap.convert.prevalidation;

import java.io.File;

/**
 * Prevalidator.
 * 
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/742a6c00-908a-11e2-a385-0016357f605f#0##47667682##e113fb8d9b20515056b53eff07c98a45
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/8b0feee0-908a-11e2-a385-0016357f605f#0##52006696##5484079db1b5974c1e2324dcdae82e72
 * KBDOMS-20140917190643-00516-dia-prod-dom-02.kb.dk.arc##digitalobject://Uid:dk:kb:doms:2007-01/68fdace0-908d-11e2-a385-0016357f605f#0##47143591##68dd4da18072d0024622c10d5a563043
 */
public class ImportPrevalidator {
    
    protected static final String PREFIX_FOUND_FILE = "found";
    protected static final String PREFIX_NOT_FOUND_ARCHIVE = ".not_found";
    
    protected static final String SUFFFIX_FILES = ".txt";
    
    protected final File foundArcFiles;
    protected final File notFoundArchiveFiles;
    
    
    public ImportPrevalidator(File outputDir) {
        this.foundArcFiles = new File(outputDir, PREFIX_FOUND_FILE);
        this.notFoundArchiveFiles = new File(outputDir, PREFIX_NOT_FOUND_ARCHIVE);
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
