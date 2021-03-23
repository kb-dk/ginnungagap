package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.workflow.ImportWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for the preservation workflow view.
 */
@Controller
public class ImportationController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(ImportationController.class);

    /** The path.*/
    protected static final String PATH = "import";

    /** The importation workflow.*/
    @Autowired
    protected ImportWorkflow workflow;

    /**
     * View for the workflows.
     * @param model The model.
     * @return The path to the workflow.
     */
    @RequestMapping("/" + PATH)
    public String getWorkflow(Model model) {
        model.addAttribute("workflow", workflow);
        
        return PATH;
    }
    
    /**
     * The run method for the workflows.
     * @param catalog The catalog to run upon.
     * @return The redirect back to the workflow view, when the given workflow is started.
     */
    @RequestMapping("/" + PATH + "/run")
    public RedirectView runWorkflow(@RequestParam(value="catalog", required=false, defaultValue="")
                                                String catalog) {
        log.info("\n ***************");
        log.info("Running the update importation workflow (for catalog: " + catalog + ").");
        log.info("\n ***************");
        workflow.startManually(catalog);
        
        try {
            synchronized(this) {
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("../" + PATH,true);
    }
}
