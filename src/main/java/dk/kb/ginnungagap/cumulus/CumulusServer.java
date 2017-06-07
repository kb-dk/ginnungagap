package dk.kb.ginnungagap.cumulus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canto.cumulus.Catalog;
import com.canto.cumulus.Item;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.Server;

import dk.kb.ginnungagap.config.CumulusConfiguration;
import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Wrapper for accessing the Cumulus server.
 */
public class CumulusServer {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(CumulusServer.class);

    /** The configuraiton for the Cumulus server. */
    protected final CumulusConfiguration configuration;
    /** Map between the catalog name and the catalog object.*/
    protected final Map<String, Catalog> catalogs = new HashMap<String, Catalog>();

    /** The cumulus server access point.*/
    protected Server server;

    /** 
     * Constructor.
     * @param configuration The configuration for Cumulus.
     */
    public CumulusServer(CumulusConfiguration configuration) {
        ArgumentCheck.checkNotNull(configuration, "CumulusConfiguration configuration");
        this.configuration = configuration;
        try {
            this.server = Server.openConnection(configuration.getWriteAccess(), configuration.getServerUrl(), 
                    configuration.getUserName(), configuration.getUserPassword());
        } catch (Exception e) {
            throw new IllegalStateException("Could not connect to server '" + configuration.getServerUrl() + "'", e);
        }
    }
    
    /**
     * @return The catalogs for the server.
     */
    public List<String> getCatalogNames() {
        return configuration.getCatalogs();
    }

    /**
     * @return The Cumulus server.
     */
    public Server getServer() {
        if(!server.isAlive()) {
            try {
                server = Server.openConnection(configuration.getWriteAccess(), configuration.getServerUrl(), 
                        configuration.getUserName(), configuration.getUserPassword());
            } catch (Exception e) {
                throw new IllegalStateException("Connection to Cumulus server '" + configuration.getServerUrl() 
                        + "' is no longer alive, and we cannot create a new one.", e);
            }
        }
        return server;
    }

    /**
     * Retrieve the catalog for a given catalog name.
     * @param catalogName The name of the catalog.
     * @return The catalog.
     */
    protected Catalog getCatalog(String catalogName) {
        if(!catalogs.containsKey(catalogName)) {
            int catalogId = getServer().findCatalogID(catalogName);
            catalogs.put(catalogName, getServer().openCatalog(catalogId));            
        }
        return catalogs.get(catalogName);
    }

    /**
     * Extracts the collection of record items from a given catalog limiting by the given query.
     * @param catalogName The name of the catalog.
     * @param query The query for finding the desired items.
     * @return The collection of record items.
     */
    public RecordItemCollection getItems(String catalogName, CumulusQuery query) {
        ArgumentCheck.checkNotNullOrEmpty(catalogName, "String catalogName");
        ArgumentCheck.checkNotNull(query, "CumulusQuery query");
        Catalog catalog = getCatalog(catalogName);
        RecordItemCollection recordCollection = catalog.newRecordItemCollection(true);
        recordCollection.find(query.getQuery(), query.getFindFlags(), query.getCombineMode(),
                query.getLocale());
        return recordCollection;
    }
    
    /**
     * Find the Cumulus record containing a given UUID and belonging to a given catalog.
     * Will only return the first found result. And it will return a null if no results were found. 
     * @param catalogName The name of the catalog, where the Cumulus record is.
     * @param uuid The UUID of the Cumulus record to find.
     * @return The Cumulus record, or null if no record was found.
     */
    public CumulusRecord findCumulusRecord(String catalogName, String uuid) {
        ArgumentCheck.checkNotNullOrEmpty(catalogName, "String catalogName");
        ArgumentCheck.checkNotNullOrEmpty(uuid, "String uuid");
        CumulusQuery query = CumulusQuery.getQueryForSpecificUUID(catalogName, uuid);
        RecordItemCollection items = getItems(catalogName, query);
        if(items == null || !items.iterator().hasNext()) {
            log.warn("Could not find any records for UUID: '" + uuid + "'. Returning a null");            
            return null;
        }
        
        log.info("Catalog '" + catalogName + "' had " + items.getItemCount() + " records to be preserved.");
        
        FieldExtractor fe = new FieldExtractor(items.getLayout());

        Iterator<Item> iterator = items.iterator();
        CumulusRecord res = new CumulusRecord(fe, iterator.next());
        if(iterator.hasNext()) {
            log.warn("More than one record found for '" + uuid + "'. Only using the first found.");
        }
        
        return res;
    }
}
