package dk.kb.ginnungagap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.ginnungagap.workflow.PreservationWorkflow;

/**
 * Controller for the preservation workflow view.
 */
@Controller
public class PreservationController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(PreservationController.class);

    /** The ginnugagap index path.*/
    protected static final String PATH = "preservation";

    /** The preservation workflow.*/
    @Autowired
    protected PreservationWorkflow workflow;

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
        log.info("Running the preservation workflow (for catalog: " + catalog + ").");
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
