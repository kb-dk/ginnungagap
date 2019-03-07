package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.workflow.UpdatePreservationWorkflow;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class UpdateControllerTest extends ExtendedTestCase {
    @Test
    public void testGetWorkflow() {
        UpdateController controller = new UpdateController();
        Model model = Mockito.mock(Model.class);
        UpdatePreservationWorkflow workflow = Mockito.mock(UpdatePreservationWorkflow.class);

        controller.workflow = workflow;

        String path = controller.getWorkflow(model);
        Assert.assertEquals(path, UpdateController.PATH);

        Mockito.verify(model).addAttribute(Mockito.eq("workflow"), Mockito.eq(workflow));
        Mockito.verifyNoMoreInteractions(model);

        Mockito.verifyZeroInteractions(workflow);
    }

    @Test
    public void testRunWorkflow() {
        UpdateController controller = new UpdateController();
        UpdatePreservationWorkflow workflow = Mockito.mock(UpdatePreservationWorkflow.class);

        controller.workflow = workflow;
        String catalog = UUID.randomUUID().toString();

        RedirectView redirectView = controller.runWorkflow(catalog);
        Assert.assertEquals(redirectView.getUrl(), "../" + UpdateController.PATH);

        Mockito.verify(workflow).startManually(Mockito.eq(catalog));
        Mockito.verifyNoMoreInteractions(workflow);
    }
}
