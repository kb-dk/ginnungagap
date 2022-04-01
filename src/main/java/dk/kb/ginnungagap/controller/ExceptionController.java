package dk.kb.ginnungagap.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling exceptions.
 */
@Controller
public class ExceptionController implements ErrorController {

    /** The path.*/
    protected static final String PATH = "error";
    /** The attribute for the status code.*/
    protected static final String ATTRIBUTE_STATUS_CODE = "javax.servlet.error.status_code";
    /** The attribute for the exception.*/
    protected static final String ATTRIBUTE_EXCEPTION = "javax.servlet.error.exception";

    /**
     * Handling the case when a request receives an exception.
     * @param request The request containing the exception.
     * @param model The model for the view.
     * @return The error page.
     */
    @RequestMapping("/" + PATH)
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute(ATTRIBUTE_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(ATTRIBUTE_EXCEPTION);
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("error", exception);
        return PATH;
    }
    
//    @Override
    public String getErrorPath() {
        return PATH;
    }
}
