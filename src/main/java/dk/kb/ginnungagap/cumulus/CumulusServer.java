package dk.kb.ginnungagap.cumulus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.canto.cumulus.Catalog;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.GUID;
import com.canto.cumulus.ItemCollection;
import com.canto.cumulus.Server;

import dk.kb.ginnungagap.config.CumulusConfiguration;

/**
 * Wrapper for accessing the Cumulus server.
 */
public class CumulusServer {

    /** The configuraiton for the Cumulus server. */
    protected final CumulusConfiguration configuration;
    
    protected final Map<String, Catalog> catalogs = new HashMap<String, Catalog>();
    
    /** The cumulus server access point.*/
    protected Server server;
    
    /** 
     * Constructor.
     * @param configuration The configuration for Cumulus.
     */
    public CumulusServer(CumulusConfiguration configuration) {
        this.configuration = configuration;
        try {
            this.server = Server.openConnection(configuration.getWriteAccess(), configuration.getServerUrl(), 
                    configuration.getUserName(), configuration.getUserPassword());
        } catch (Exception e) {
            throw new IllegalStateException("", e);
        }
    }
    
    /**
     * @return The Cumulus server.
     */
    public Server getServer() {
        return server;
    }
    
    /**
     * Retrieve the catalog for a given catalog name.
     * @param catalogName The name of the catalog.
     * @return The catalog.
     */
    public Catalog getCatalog(String catalogName) {
        if(!catalogs.containsKey(catalogName)) {
            int catalogId = getServer().findCatalogID(catalogName);
            catalogs.put(catalogName, getServer().openCatalog(catalogId));            
        }
        return catalogs.get(catalogName);
    }
    
    
    /**
     * Retrieve the map between the field name and the definition of the Cumulus fields of a given catalog. 
     * @param catalogName The name of the catalog.
     * @return The field mapping between GUID and definition.
     */
    public Map<String, FieldDefinition> getFieldMap(String catalogName) {
        Map<String, FieldDefinition> res = new LinkedHashMap<String, FieldDefinition>();
        Catalog catalog = getCatalog(catalogName);
        ItemCollection ic = catalog.getAllVocabulariesItemCollection();
        for(FieldDefinition fd : ic.getLayout()) {
            res.put(fd.getName(), fd);
        }
        
        return res;
    }
    
    public void gnu() {
//        server.
    }
}
