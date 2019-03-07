package dk.kb.ginnungagap.controller;

import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;
import java.util.UUID;

public class ExceptionControllerTest extends ExtendedTestCase {
    @Test
    public void testPath() {
        ExceptionController controller = new ExceptionController();
        Assert.assertEquals(controller.getErrorPath(), ExceptionController.PATH);
    }

    @Test
    public void testHandleError() {
        ExceptionController controller = new ExceptionController();
        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Integer statusCode = new Random().nextInt();
        Exception exception = new Exception(UUID.randomUUID().toString());

        Mockito.when(request.getAttribute(Mockito.eq(ExceptionController.ATTRIBUTE_STATUS_CODE))).thenReturn(statusCode);
        Mockito.when(request.getAttribute(Mockito.eq(ExceptionController.ATTRIBUTE_EXCEPTION))).thenReturn(exception);

        controller.handleError(request, model);

        Mockito.verify(request).getAttribute(Mockito.eq(ExceptionController.ATTRIBUTE_STATUS_CODE));
        Mockito.verify(request).getAttribute(Mockito.eq(ExceptionController.ATTRIBUTE_EXCEPTION));
        Mockito.verifyNoMoreInteractions(request);

        Mockito.verify(model).addAttribute(Mockito.anyString(), Mockito.eq(statusCode));
        Mockito.verify(model).addAttribute(Mockito.anyString(), Mockito.eq(exception));
        Mockito.verifyNoMoreInteractions(model);
    }
}
