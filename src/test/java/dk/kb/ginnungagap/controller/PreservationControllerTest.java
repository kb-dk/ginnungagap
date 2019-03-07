package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.workflow.PreservationWorkflow;
import dk.kb.ginnungagap.workflow.ValidationWorkflow;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class PreservationControllerTest extends ExtendedTestCase {
    @Test
    public void testGetWorkflow() {
        PreservationController controller = new PreservationController();
        Model model = Mockito.mock(Model.class);
        PreservationWorkflow workflow = Mockito.mock(PreservationWorkflow.class);

        controller.workflow = workflow;

        String path = controller.getWorkflow(model);
        Assert.assertEquals(path, PreservationController.PATH);

        Mockito.verify(model).addAttribute(Mockito.eq("workflow"), Mockito.eq(workflow));
        Mockito.verifyNoMoreInteractions(model);

        Mockito.verifyZeroInteractions(workflow);
    }

    @Test
    public void testRunWorkflow() {
        PreservationController controller = new PreservationController();
        PreservationWorkflow workflow = Mockito.mock(PreservationWorkflow.class);

        controller.workflow = workflow;
        String catalog = UUID.randomUUID().toString();

        RedirectView redirectView = controller.runWorkflow(catalog);
        Assert.assertEquals(redirectView.getUrl(), "../" + PreservationController.PATH);

        Mockito.verify(workflow).startManually(Mockito.eq(catalog));
        Mockito.verifyNoMoreInteractions(workflow);
    }
}
