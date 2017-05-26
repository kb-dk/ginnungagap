package dk.kb.ginnungagap.cumulus;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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

import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.RequiredFields;
import dk.kb.ginnungagap.testutils.SetupCumulusTests;
import dk.kb.ginnungagap.testutils.TestFileUtils;
import dk.kb.ginnungagap.testutils.TravisUtils;
import dk.kb.ginnungagap.transformation.XsltMetadataTransformer;

public class CumulusTest extends ExtendedTestCase {

    File outputDir;
    CumulusServer server;

    int MAX_NUMBER_OF_RECORDS = 100000;
    String passwordFilePath = System.getenv("HOME") + "/cumulus-password.yml";

    String catalogName = "Conservation";

    @BeforeClass
    public void setup() throws Exception {
        if(TravisUtils.runningOnTravis()) {
            throw new SkipException("Skipping this test");
        }
        File passwordFile = new File(passwordFilePath);
        if(!passwordFile.isFile()) {
            throw new SkipException("Cannot connect to Cumulus without the password-file: " + passwordFilePath);
        }
        Cumulus.CumulusStart();
        TestFileUtils.setup();
        outputDir = TestFileUtils.getTempDir();

        Configuration conf = SetupCumulusTests.getConfiguration(catalogName);
        server = new CumulusServer(conf.getCumulusConf());
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
        //        int catalogId = server.getServer().findCatalogID(catalogName);
        //        Catalog catalog = server.getServer().openCatalog(catalogId);

        //        String query = "" + Constants.FieldNames.PRESERVATION_STATUS + "\tis\t" + Constants.FieldValues.PRESERVATIONSTATE_READY_FOR_ARCHIVAL
        //                + "\nand\t" + Constants.FieldNames.CATALOG_NAME + "\tis\t" + catalogName;

        String query = "GUID\tcontains\tbe9f16d0-da42-11e2-b191-005056887b70"
                + "\nand\t" + Constants.FieldNames.CATALOG_NAME + "\tis\t" + catalogName;
        EnumSet<FindFlag> flags = EnumSet.of(FindFlag.FIND_MISSING_FIELDS_ARE_ERROR, FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);

        RecordItemCollection items = server.getItems(catalogName, new CumulusQuery(query, flags, CombineMode.FIND_NEW));
        System.err.println("Number of items: " + items.getItemCount());

        RequiredFields rf = new RequiredFields(Arrays.asList("CHECKSUM_ORIGINAL_MASTER"), Arrays.asList("Preservation_status"));

        // Try with resource, to ensure closing it.
        FieldExtractor fe = new FieldExtractor(items.getLayout());
        for(FieldDefinition fd : items.getLayout()) {
            //            System.err.println(fd.getName() + " -> " + fd.getFieldUID().toString());
        }
        Iterator<Item> iri = items.iterator();
        Item item;
        int i = 0;
        while(iri.hasNext() && (item = iri.next()) != null && i < MAX_NUMBER_OF_RECORDS) {
            i++;

            //            Map<String, Field> map = fe.getAllFields(item);
            //            System.err.println("Has QA_error: " + map.containsKey("QA_error"));
            //            System.err.println("Is QA_error writable: " + map.get("QA_error").isFieldEditable());

            CumulusRecord cr = new CumulusRecord(fe, item);
            //            cr.validateRequiredFields(rf);
            //            cr.setPreservationMetadataPackage("Metadata Package");
            //            cr.setPreservationResourcePackage("Resource Package");
            //            cr.setPreservationFailed("Hej Tue\n\nDette er en test for at se, om jeg kan skrive til QA_error feltet, samt ændre på Preservation_status feltet.\n\nMed venlig hilsen\nGinnungagap");
            //            cr.setPreservationFinished();

            File cumulusMetadataFile = new File(outputDir, item.getID() + ".xml");
            File metsMetadataFile = new File(outputDir, item.getID() + ".mets.xml");
            cr.getMetadata(cumulusMetadataFile);
            XsltMetadataTransformer transformer = new XsltMetadataTransformer(new File("src/main/resources/scripts/xslt/transformToMets.xsl"));

            transformer.transformXmlMetadata(new FileInputStream(cumulusMetadataFile), new FileOutputStream(metsMetadataFile));
            //            StreamUtils.copy(cr.getMetadata(new File(outputDir, item.getID() + "_fields.xml")), new FileOutputStream(outputFile));
        }

        assertEquals(outputDir.list().length, 2*i);
    }

    @Test(enabled = false)
    public void testPrintingAssetReferenceToXml() throws Exception {
        List<String> catalogs = Arrays.asList(
                "Audio",
                "Billedarkivet",
                "Conservation",
                "COWI Maps",
                "Games",
                "Letters OM",
                "PDF",
                "Samlingsbilleder",
                "Smaatryk");

        File outputFile = new File(TestFileUtils.getTempDir(), "catalog-" + UUID.randomUUID().toString());
        for(String catalogName : catalogs) {
            writeAssetReferenceForCatalog(catalogName, outputFile);
        }
    }

    protected void writeAssetReferenceForCatalog(String catalogName, File outputFile) throws IOException {
        try (OutputStream os = new FileOutputStream(outputFile, true)) {
            String query = Constants.FieldNames.ARCHIVE_FILENAME + "\thas value"
                    + "\nand\t" + Constants.FieldNames.CATALOG_NAME + "\tis\t" + catalogName;
            EnumSet<FindFlag> flags = EnumSet.of(FindFlag.FIND_MISSING_FIELDS_ARE_ERROR, FindFlag.FIND_MISSING_STRING_LIST_VALUES_ARE_ERROR);

            RecordItemCollection items = server.getItems(catalogName, new CumulusQuery(query, flags, CombineMode.FIND_NEW));
            System.err.println("Number of items: " + items.getItemCount());

            RequiredFields rf = new RequiredFields(Arrays.asList("CHECKSUM_ORIGINAL_MASTER"), Arrays.asList("Preservation_status"));

            // Try with resource, to ensure closing it.
            FieldExtractor fe = new FieldExtractor(items.getLayout());
            Iterator<Item> iri = items.iterator();
            Item item;
            int i = 0;
            while(iri.hasNext() && (item = iri.next()) != null && i < MAX_NUMBER_OF_RECORDS) {
                i++;

                CumulusRecord cr = new CumulusRecord(fe, item);

                String assetReference = cr.getFieldValueForNonStringField(Constants.FieldNames.ASSET_REFERENCE);
                String assetReferenceDirPath;
                if(assetReference.contains("/")) {
                    assetReferenceDirPath = assetReference.substring(0, assetReference.lastIndexOf("/"));
                } else {
                    assetReferenceDirPath = assetReference;
                }
                String line = assetReferenceDirPath + "\n";
                os.write(line.getBytes());
                os.flush();
            }
        }
    }
}
