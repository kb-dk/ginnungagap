package dk.kb.ginnungagap.retriever;

import com.canto.cumulus.Catalog;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.Server;
import com.canto.cumulus.Server.CatalogInfo;

import dk.kb.ginnungagap.config.CumulusConfiguration;

public class CumulusDataRetriever implements DataRetriever {

    Server cumulusServer;
    
    CumulusConfiguration configuration;
    
    public CumulusDataRetriever(CumulusConfiguration conf) {
        this.configuration = conf;
        
        try {
        cumulusServer = Server.openConnection(configuration.getWriteAccess(), configuration.getServerUrl(), 
                configuration.getUserName(), configuration.getUserPassword());
        
        } catch (Exception e) {
            throw new IllegalStateException("", e);
        }
    }
    
    public void gnu(String catalogName) {
//        cumulusServer.getCatalogIDs(arg0, arg1)
        int catalogID = cumulusServer.findCatalogID(catalogName);
//        CatalogInfo ci = cumulusServer.getCatalogInfo(catalogID);
//        ci.
        Catalog c = cumulusServer.openCatalog(catalogID);
        RecordItemCollection ric = c.newRecordItemCollection(true);
//        ric.
    }

    @Override
    public Record retrieveNextRecord() {
        // TODO Auto-generated method stub
        return null;
    }
}
