package dk.kb.ginnungagap;

import static org.testng.Assert.assertTrue;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Catalog;
import com.canto.cumulus.Cumulus;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.GUID;
import com.canto.cumulus.Item;
import com.canto.cumulus.ItemCollection;
import com.canto.cumulus.RecordItem;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.Server.CatalogInfo;

import dk.kb.ginnungagap.config.CumulusConfiguration;
import dk.kb.ginnungagap.cumulus.CumulusServer;

public class CumulusTest extends ExtendedTestCase {

    @BeforeClass
    public void setup() {
        Cumulus.CumulusStart();
    }
    
    @AfterClass
    public void stop() {
        Cumulus.CumulusStop();
    }
    
    @Test
    public void testRandomStuff() throws Exception {
        CumulusConfiguration conf = new CumulusConfiguration(false, "cumulus-core-test-01.kb.dk", "audio-adm", "");
        CumulusServer s = new CumulusServer(conf);
        assertTrue(s.getServer().isAlive());
        
        Collection<String> collectionNames = s.getServer().getCollectionNames();
        System.err.println("Number of collections: " + collectionNames.size());
        for(String name : collectionNames) {
            System.err.println("Collection: " + name);
        }
        System.err.println();
        
        Collection<Integer> catalogIDs = s.getServer().getCatalogIDs(false, false);
        System.err.println("Number of catalogIDs: " + catalogIDs.size());
        for(Integer i : catalogIDs) {
            CatalogInfo ci = s.getServer().getCatalogInfo(i);
            System.err.println("Catalog: #" + i + ", " + ci.getCatalogName()
                    + ", " + ci.getCatalogLocation()
                    + ", " + ci.getDescription()
                    + ", " + ci.getDisplayName()
                    + ", " + (ci.canMigrate() ? "can Migrate" : "cannot Migrate")
                    + ", " + (ci.isDamaged() ? "is damaged" : "is not damages")
                    + ", " + ci.isPublishedToInternet()
                    + ", " + ci.isShared());
        }
        
        ItemCollection ic = s.getServer().getAllVocabulariesItemCollection();
        System.err.println("Number of items: " + ic.getItemTotalCount() + ", " + ic.getItemCount());
        System.err.println("Table name: " + ic.getTableName());
        System.err.println("Layout tablename: " + ic.getLayout().getTableName());
        for(FieldDefinition fd : ic.getLayout()) {
            System.err.println(fd.getFieldUID() + " -> " + fd.getName() + " -> " + fd.getFieldType());
//            fd.get
        }
//        for(GUID guid : ic.getLayout().getFieldUIDs()) {
//            System.err.println(guid + " \t-> " + ic.getLayout().getFieldDefinition(guid));
//        }
        System.err.println();
        
        String catalogName = "Audio OM";
        int catalogId = s.getServer().findCatalogID(catalogName);
        Catalog catalog = s.getServer().openCatalog(catalogId);
//        catalog.getAllVocabulariesItemCollection()
        RecordItemCollection ric = catalog.newRecordItemCollection(true);
        Iterator<Item> iri = ric.iterator();
        Item item;
        int i = 0;
        while(iri.hasNext() && (item = iri.next()) != null && (i++) < 2) {
            System.err.println("Item: " + item.getID() + ", " + item.getItemIdentifier() +
                    ", " + item.getCatalogID() + ", " + item.getDisplayString());
//            RecordItem ri = (RecordItem) item;
//            ri.
        }
//        FieldDefinition.FieldTypeAudio
//        catalog.
//        serverCatalog.newRecordItemCollection(findAll)
    }
    
//    @Test
//    public void testFromKbDoms() throws Exception {
//
//        Cumulus.CumulusStart();
//        Cumulus.CumulusStop();
//
//        Cumulus.CumulusStart("no");
//        Cumulus.CumulusStop();
//        
//        System.out.println("Cumulus Java SDK version: "
//                + Cumulus.getVersion());
//    }
}
