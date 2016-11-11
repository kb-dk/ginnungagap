package dk.kb.ginnungagap.cumulus;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.lf5.util.StreamUtils;
import org.jaccept.structure.ExtendedTestCase;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.canto.cumulus.Cumulus;
import com.canto.cumulus.FieldDefinition;
import com.canto.cumulus.Item;
import com.canto.cumulus.ItemCollection;
import com.canto.cumulus.RecordItemCollection;
import com.canto.cumulus.Server.CatalogInfo;
import com.canto.cumulus.constants.CombineMode;
import com.canto.cumulus.constants.FindFlag;

import dk.kb.ginnungagap.config.CumulusConfiguration;
import dk.kb.ginnungagap.cumulus.field.Field;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TravisUtils;

public class CumulusTest extends ExtendedTestCase {

    File outputDir;
    CumulusServer server;

    int MAX_NUMBER_OF_RECORDS = 1;

    @BeforeClass
    public void setup() {
        if(TravisUtils.runningOnTravis()) {
            throw new SkipException("Skipping this test");
        }
        Cumulus.CumulusStart();
        TestFileUtils.setup();
        outputDir = TestFileUtils.getTempDir();

        CumulusConfiguration conf = new CumulusConfiguration(true, "cumulus-core-test-01.kb.dk", "bevaring", "????????");
        server = new CumulusServer(conf);
    }

    @AfterClass
    public void stop() {
        Cumulus.CumulusStop();
        //        TestFileUtils.tearDown();
    }

    //    @Test
    public void testRandomStuff() throws Exception {
        assertTrue(server.getServer().isAlive());

        Collection<String> collectionNames = server.getServer().getCollectionNames();
        System.err.println("Number of collections: " + collectionNames.size());
        for(String name : collectionNames) {
            System.err.println("Collection: " + name);
        }
        System.err.println();

        Collection<Integer> catalogIDs = server.getServer().getCatalogIDs(false, false);
        System.err.println("Number of catalogIDs: " + catalogIDs.size());
        for(Integer i : catalogIDs) {
            CatalogInfo ci = server.getServer().getCatalogInfo(i);
            System.err.println("Catalog: #" + i + ", " + ci.getCatalogName()
            + ", " + ci.getCatalogLocation()
            + ", " + ci.getDescription()
            + ", " + ci.getDisplayName()
            + ", " + (ci.canMigrate() ? "can Migrate" : "cannot Migrate")
            + ", " + (ci.isDamaged() ? "is damaged" : "is not damages")
            + ", " + (ci.isPublishedToInternet() ? "is published to internet" : "is not published to internet")
            + ", " + (ci.isShared() ? "is shared" : "is not shared"));
        }

        ItemCollection ic = server.getServer().getAllVocabulariesItemCollection();
        System.err.println("Number of items: " + ic.getItemTotalCount() + ", " + ic.getItemCount());
        System.err.println("Table name: " + ic.getTableName());
        System.err.println("Layout tablename: " + ic.getLayout().getTableName());
    }

    @Test(enabled = false)
    public void testPrintingToXml() throws Exception {
        String catalogName = "Conservation";
//        int catalogId = server.getServer().findCatalogID(catalogName);
//        Catalog catalog = server.getServer().openCatalog(catalogId);

        String query = "" + Constants.FieldNames.PRESERVATION_STATUS + "\tis\t" + Constants.FieldValues.PRESERVATIONSTATE_READY_FOR_ARCHIVAL
                + "\nand\t" + Constants.FieldNames.CATALOG_NAME + "\tis\t" + catalogName;
        EnumSet<FindFlag> flags = EnumSet.of(FindFlag.FIND_MISSING_FIELDS_ARE_ERROR, FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);

        RecordItemCollection items = server.getItems(catalogName, new CumulusQuery(query, flags, CombineMode.FIND_NEW));
        System.err.println("Number of items: " + items.getItemCount());

        // Try with resource, to ensure closing it.
        FieldExtractor fe = new FieldExtractor(items.getLayout());
        for(FieldDefinition fd : items.getLayout()) {
            System.err.println(fd.getName() + " -> " + fd.getFieldUID().toString());
        }
        Iterator<Item> iri = items.iterator();
        Item item;
        int i = 0;
        while(iri.hasNext() && (item = iri.next()) != null && i < MAX_NUMBER_OF_RECORDS) {
            i++;
            
            Map<String, Field> map = fe.getAllFields(item);
            System.err.println("Has QA_error: " + map.containsKey("QA_error"));
            System.err.println("Is QA_error writable: " + map.get("QA_error").isFieldEditable());
            
            CumulusRecord cr = new CumulusRecord(fe, item);
//            cr.setPreservationMetadataPackage("Metadata Package");
//            cr.setPreservationResourcePackage("Resource Package");
//            cr.setPreservationFailed("Hej Tue\n\nDette er en test for at se, om jeg kan skrive til QA_error feltet, samt ændre på Preservation_status feltet.\n\nMed venlig hilsen\nGinnungagap");
//            cr.setPreservationFinished();

            File outputFile = new File(outputDir, item.getID() + ".xml");
            StreamUtils.copy(cr.getMetadata(), new FileOutputStream(outputFile));
        }

        assertEquals(outputDir.list().length, i);
    }
}
