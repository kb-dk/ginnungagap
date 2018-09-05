package dk.kb.ginnungagap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.ginnungagap.workflow.UpdatePreservationWorkflow;

/**
 * Controller for the preservation workflow view.
 */
@Controller
public class UpdateController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(UpdateController.class);

    /** The CCS workflow.*/
    @Autowired
    protected UpdatePreservationWorkflow updateWorkflow;

    /**
     * View for the workflows.
     * @param model The model.
     * @return The path to the workflow.
     */
    @RequestMapping("/update")
    public String getWorkflow(Model model) {
        model.addAttribute("workflow", updateWorkflow);
        
        return "update";
    }
    
    /**
     * The run method for the workflows.
     * @return The redirect back to the workflow view, when the given workflow is started.
     */
    @RequestMapping("/update/run")
    public RedirectView runWorkflow() {
        log.info("Running the update preservation workflow.");
        updateWorkflow.startManually();
        
        try {
            synchronized(this) {
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("../update",true);
    }
}
