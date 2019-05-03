package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.workflow.ValidationWorkflow;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class ValidationControllerTest extends ExtendedTestCase {
    @Test
    public void testGetWorkflow() {
        ValidationController controller = new ValidationController();
        Model model = Mockito.mock(Model.class);
        ValidationWorkflow workflow = Mockito.mock(ValidationWorkflow.class);

        controller.workflow = workflow;

        String path = controller.getWorkflow(model);
        Assert.assertEquals(path, ValidationController.PATH);

        Mockito.verify(model).addAttribute(Mockito.eq("workflow"), Mockito.eq(workflow));
        Mockito.verifyNoMoreInteractions(model);

        Mockito.verifyZeroInteractions(workflow);
    }

    @Test
    public void testRunWorkflow() {
        ValidationController controller = new ValidationController();
        ValidationWorkflow workflow = Mockito.mock(ValidationWorkflow.class);

        controller.workflow = workflow;
        String catalog = UUID.randomUUID().toString();

        RedirectView redirectView = controller.runWorkflow(catalog);
        Assert.assertEquals(redirectView.getUrl(), "../" + ValidationController.PATH);

        Mockito.verify(workflow).startManually(Mockito.eq(catalog));
        Mockito.verifyNoMoreInteractions(workflow);
    }
}
