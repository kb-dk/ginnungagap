package dk.kb.ginnungagap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.kb.cumulus.Constants;
import dk.kb.cumulus.CumulusQuery;
import dk.kb.cumulus.CumulusRecord;
import dk.kb.cumulus.CumulusRecordCollection;
import dk.kb.cumulus.CumulusServer;
import dk.kb.ginnungagap.archive.Archive;
import dk.kb.ginnungagap.archive.BitmagPreserver;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.cumulus.CumulusQueryUtils;
import dk.kb.ginnungagap.exception.ArgumentCheck;
import dk.kb.ginnungagap.transformation.MetadataTransformationHandler;
import dk.kb.ginnungagap.transformation.MetadataTransformer;
import dk.kb.ginnungagap.workflow.ImportWorkflow;
import dk.kb.ginnungagap.workflow.PreservationWorkflow;
import dk.kb.ginnungagap.workflow.UpdatePreservationWorkflow;
import dk.kb.ginnungagap.workflow.ValidationWorkflow;
import dk.kb.ginnungagap.workflow.schedule.Workflow;
import dk.kb.ginnungagap.workflow.schedule.WorkflowScheduler;

/**
 * Class for instantiating the Ginnungagap workflow.
 * 
 * Only argument required is the path to the configuration directory. It only have to be the relative path.
 * It can either be given through the environment variable, GINNUNGAGAP_CONF_DIR,
 * or given as commandline argument to the main class. 
 * If both arguments are given, then the commandline is used.
 * 
 * Will exit if any of the required files are missing.
 * And will throw exception if something is wrong configured wrongly (or if there is bugs in the code :-P ).
 * 
 * Receives the following arguments:
 * 1. Configuration file.
 * 2. Archive - must be local or bitmag (default)
 * 3. retrieve only file and metadata - no packaging or preservation (yes/no; default: no)
 * 
 * e.g.
 * dk.kb.ginningagap.Ginnungagap conf/ginnungagap.yml local
 */
public class Ginnungagap extends AbstractMain {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(Ginnungagap.class);

    /** The number of days between updates.*/
    protected static final Integer NUMBER_OF_DAYS_FOR_UPDATE = 90;
    
    /**
     * Main method. 
     * @param args List of arguments delivered from the commandline.
     * One argument is required; the configuration file, and any other arguments will be ignored.
     */
    public static void main(String ... args) {
        // How do you instantiate the primordial void ??

        String confPath = null;
        if(args.length < 1) {
            printParametersAndExit();
        } else {
            confPath = args[0];
        }
        try {
            String archiveType = ARCHIVE_BITMAG;
            boolean fileOnly = false;
            if(args.length > 1) {
                archiveType = args[1];
            }
            if(args.length > 2 ) {
                fileOnly = isYes(args[2]);
            }
            if(args.length > 4) {
                System.out.println("Maximum 3 arguments; the configuration file, archive-type, file-only. "
                        + "All the other arguments are ignored!");
            }

            Configuration conf = instantiateConfiguration(confPath);
            
            MetadataTransformationHandler transformationHandler = instantiateTransformationHandler(conf, 
                    TRANSFORMATION_SCRIPT_FOR_METS, TRANSFORMATION_SCRIPT_FOR_REPRESENTATION, 
                    TRANSFORMATION_SCRIPT_FOR_INTELLECTUEL_ENTITY);

            try (Archive archive = instantiateArchive(archiveType, conf);
                    CumulusServer cumulusServer = new CumulusServer(conf.getCumulusConf())) {
                if(fileOnly) {
                    extractFilesOnly(cumulusServer, conf, 
                            transformationHandler.getTransformer(TRANSFORMATION_SCRIPT_FOR_METS));
                } else {
                    BitmagPreserver preserver = new BitmagPreserver(archive, conf.getBitmagConf());

                    Collection<Workflow> workflows = instantiateWorkflows(conf, cumulusServer, transformationHandler, 
                            preserver, archive);
                    runWorkflows(workflows, conf.getWorkflowConf().getInterval());
                }
                System.out.println("Finished!");
            }
        } catch (ArgumentCheck e) {
            log.warn("Argument failure.", e);
            printParametersAndExit();
        } catch (Exception e) {
            System.out.println("Failed!");
            throw new RuntimeException("Unexpected failure.", e);
        }
    }

    /**
     * Instantiates the workflows.
     * @param conf The configuration.
     * @param server The Cumulus server.
     * @param transformer The transformer.
     * @param representationTransformer The representation transformer.
     * @param preserver The bitmag preserver for packaging the data.
     * @param archive The bitrepository archive.
     * @return The list of workflows to be run.
     */
    protected static Collection<Workflow> instantiateWorkflows(Configuration conf, CumulusServer server, 
            MetadataTransformationHandler transformationHandler, BitmagPreserver preserver, Archive archive) {
        List<Workflow> res = new ArrayList<Workflow>();
        for(String workflowName : conf.getWorkflowConf().getWorkflows()) {
            if(workflowName.equalsIgnoreCase(PreservationWorkflow.class.getSimpleName())) {
                res.add(new PreservationWorkflow(conf.getTransformationConf(), server, transformationHandler, 
                        preserver));
            } else if(workflowName.equalsIgnoreCase(ImportWorkflow.class.getSimpleName())) {
                res.add(new ImportWorkflow(conf, server, archive));
            } else if(workflowName.equalsIgnoreCase(ValidationWorkflow.class.getSimpleName())) {
                res.add(new ValidationWorkflow(conf, server, archive));
            } else if(workflowName.equalsIgnoreCase(UpdatePreservationWorkflow.class.getSimpleName())) {
                res.add(new UpdatePreservationWorkflow(conf.getTransformationConf(), server, transformationHandler, 
                        preserver, conf.getWorkflowConf().getUpdateRetentionInDays()));
            } else {
                throw new IllegalStateException("Cannot instantiate a workflow with name: "
                        + workflowName);
            }
        }
        return res;
    }

    /**
     * Prints the parameters for the main class, and then exit.
     * Should only be used, when it has failed on a parameter.
     */
    protected static void printParametersAndExit() {
        System.err.println("Have the following arguments: ");
        System.err.println(" 1. Configuration file.");
        System.err.println(" 2. Archive - must be local or bitmag (default)");
        System.err.println(" 3. retrieve only file and metadata - no packaging or preservation (yes/no; default: no)");
        System.exit(-1);
    }

    /**
     * Extracts only the Cumulus objects as files. 
     * Does not do any kind of transformation or preservation.
     * @param server The cumulus server.
     * @param conf The configuration.
     */
    protected static void extractFilesOnly(CumulusServer server, Configuration conf, MetadataTransformer transformer) {
        for(String catalogName : conf.getCumulusConf().getCatalogs()) {
            log.info("Extracting files for catalog '" + catalogName + "'.");
            CumulusQuery query = CumulusQueryUtils.getPreservationAllQuery(catalogName);
            CumulusRecordCollection items = server.getItems(catalogName, query);

            log.info("Catalog '" + catalogName + "' had " + items.getCount() + " records to be preserved.");
            for(CumulusRecord record : items) {
                try {
                    String filename = record.getFieldValue(Constants.FieldNames.RECORD_NAME);
                    File metadataOutputFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                            filename + ".xml");
                    try (OutputStream metadataOut = new FileOutputStream(metadataOutputFile)) {
                        record.writeFieldMetadata(metadataOut);
                    }
                    File transformedMetadataOutputFile = new File(conf.getTransformationConf().getMetadataTempDir(),
                            record.getFieldValue(Constants.FieldNames.METADATA_GUID));
                    transformer.transformXmlMetadata(new FileInputStream(metadataOutputFile), 
                            new FileOutputStream(transformedMetadataOutputFile));

                    File cumulusFile = record.getFile();
                    File outputFile = new File(conf.getTransformationConf().getMetadataTempDir(), 
                            cumulusFile.getName());
                    FileUtils.copyFile(cumulusFile, outputFile);
                } catch (Exception e) {
                    log.error("Runtime exception caught while trying to handle Cumulus record '" + record + "'. ", e);
                }
            }
        }
    }

    /**
     * Running the workflows continuously at the given interval.
     * If the interval was non-positive, then they are only run once.
     * @param workflows The workflows to run.
     * @param interval The interval between running the workflows. If non-positive, then only run once.
     * @throws InterruptedException If the scheduling fails.
     */
    protected static void runWorkflows(Collection<Workflow> workflows, long interval) throws InterruptedException {
        if(interval > 0) {
            WorkflowScheduler scheduler = new WorkflowScheduler();
            for(Workflow workflow : workflows) {
                log.info("Scheduling workflow: " + workflow.getJobID() + " : " + workflow.getDescription() 
                        + ", at the interval " + interval + " milliseconds");
                scheduler.schedule(workflow, interval);
            }
            synchronized(scheduler) {
                boolean run = true;
                while(run) {
                    try {
                        scheduler.wait();
                        run = false;
                    } catch (InterruptedException e) {
                        log.warn("Scheduler was interrupted when running the workflows. Trying to continue.", e);
                    }
                }
            }
        } else {
            for(Workflow workflow : workflows) {
                log.info("Single run of workflow: " + workflow.getJobID() + " : " + workflow.getDescription());
                workflow.start();                    
            }
        }
    }
}
