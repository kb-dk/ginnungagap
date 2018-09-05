package dk.kb.ginnungagap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import dk.kb.ginnungagap.workflow.ValidationWorkflow;

/**
 * Controller for the preservation workflow view.
 */
@Controller
public class ValidationController {
    /** The log.*/
    protected final Logger log = LoggerFactory.getLogger(ValidationController.class);

    /** The validation workflow.*/
    @Autowired
    protected ValidationWorkflow workflow;
    /**
     * View for the workflows.
     * @param model The model.
     * @return The path to the workflow.
     */
    @RequestMapping("/validation")
    public String getWorkflow(Model model) {
        log.info("Requested the preservation path");
        model.addAttribute("workflow", workflow);
        
        return "validation";
    }
    
    /**
     * The run method for the workflows.
     * @return The redirect back to the workflow view, when the given workflow is started.
     */
    @RequestMapping("/validation/run")
    public RedirectView runWorkflow() {
        workflow.startManually();
        
        try {
            synchronized(this) {
                this.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RedirectView("../validation",true);
    }
}
