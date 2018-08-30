package dk.kb.ginnungagap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.ginnungagap.workflow.PreservationWorkflow;

/**
 * Controller for the preservation workflow view.
 */
@Controller
public class PreservationController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(PreservationController.class);

    /** The CCS workflow.*/
    @Autowired
    protected PreservationWorkflow preservationWorkflow;

    /**
     * View for the workflows.
     * @param model The model.
     * @return The path to the workflow.
     */
    @RequestMapping("/preservation")
    public String getWorkflow(Model model) {
        log.info("Requested the preservation path");
        model.addAttribute("workflow", preservationWorkflow);
        
        return "preservation";
    }
    
    /**
     * The run method for the workflows.
     * Matches the name of the workflow, and starts the given workflow.
     * Will default run the CCS workflow, if the name doesn't match.
     * @param name The name of the workflow to run.
     * @return The redirect back to the workflow view, when the given workflow is started.
     */
    @RequestMapping("/preservation/run")
    public RedirectView runWorkflow() {
        preservationWorkflow.startManually();
        
        try {
            synchronized(this) {
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("../preservation",true);
    }
}
