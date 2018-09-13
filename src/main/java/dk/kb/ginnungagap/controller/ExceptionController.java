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

    /**
     * Handling the case when a request receives an exception.
     * @param request The request containing the exception.
     * @param model The model for the view.
     * @return The error page.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("error", exception);
        return "error";
    }
    
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
