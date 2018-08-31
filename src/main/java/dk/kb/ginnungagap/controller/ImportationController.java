package dk.kb.ginnungagap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.ginnungagap.workflow.ImportWorkflow;

/**
 * Controller for the preservation workflow view.
 */
@Controller
public class ImportationController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(ImportationController.class);

    /** The CCS workflow.*/
    @Autowired
    protected ImportWorkflow importWorkflow;

    /**
     * View for the workflows.
     * @param model The model.
     * @return The path to the workflow.
     */
    @RequestMapping("/import")
    public String getWorkflow(Model model) {
        model.addAttribute("workflow", importWorkflow);
        
        return "preservation";
    }
    
    /**
     * The run method for the workflows.
     * @return The redirect back to the workflow view, when the given workflow is started.
     */
    @RequestMapping("/import/run")
    public RedirectView runWorkflow() {
        log.info("Running the update importation workflow.");
        importWorkflow.startManually();
        
        try {
            synchronized(this) {
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("../import",true);
    }
}
