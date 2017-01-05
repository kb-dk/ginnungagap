package dk.kb.ginnungagap.convert;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;

import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.jaccept.structure.ExtendedTestCase;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecord;
import org.jwat.arc.ArcRecordBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConversionTest extends ExtendedTestCase {

    @Test
    public void testJWATReadingArcFile() throws Exception {

        File arcDir = new File("/home/jolf/data");
        Assert.assertTrue(arcDir.isDirectory());
        for(File f : arcDir.listFiles()) {
            try {
                ArcReader reader = ArcReaderFactory.getReader(new FileInputStream(f));
                System.out.println("File: " + f.getName() + " - is complient: " + reader.isCompliant());
                //            reader.

                //            System.out.println(reader.versionHeader);
                System.out.println("errors: " + reader.diagnostics.hasErrors());
                System.out.println("warnings: " + reader.diagnostics.hasWarnings());

                ArcRecordBase record;
                Iterator<ArcRecordBase> iterator = reader.iterator();

                while(iterator.hasNext()) {
                    record = iterator.next();
                    System.out.println(new String(record.header.headerBytes));
                }
            } catch (Throwable e) {
                System.err.println("FAILED: " + f.getName());
                e.printStackTrace();
            }
        }
    }

    @Test(enabled = false)
    public void testArchiveIOReadingArcFile() throws Exception {
        //        ArchiveRecord 
        File arcDir = new File("/home/jolf/data");
        Assert.assertTrue(arcDir.isDirectory());
        for(File f : arcDir.listFiles()) {
            try {
                ArchiveReader reader = ArchiveReaderFactory.get(f);
                System.out.println("File: " + f.getName() + " - is valid: " + reader.isValid());

                reader = ArchiveReaderFactory.get(f);
                for(ArchiveRecord record : reader) {
                    for(Map.Entry<String, Object> entry : record.getHeader().getHeaderFields().entrySet()) {
                        System.out.print(entry.getValue() + " ");
                    }
                    System.out.println();
                }

                System.out.println("\n");
            } catch (Throwable e) {
                System.err.println("FAILED: " + f.getName());
                e.printStackTrace();
            }
        }
    }
}
