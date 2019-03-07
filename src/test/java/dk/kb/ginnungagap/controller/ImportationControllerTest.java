package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.workflow.ImportWorkflow;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class ImportationControllerTest extends ExtendedTestCase {
    @Test
    public void testGetWorkflow() {
        ImportationController controller = new ImportationController();
        Model model = Mockito.mock(Model.class);
        ImportWorkflow workflow = Mockito.mock(ImportWorkflow.class);

        controller.workflow = workflow;

        String path = controller.getWorkflow(model);
        Assert.assertEquals(path, ImportationController.PATH);

        Mockito.verify(model).addAttribute(Mockito.eq("workflow"), Mockito.eq(workflow));
        Mockito.verifyNoMoreInteractions(model);

        Mockito.verifyZeroInteractions(workflow);
    }

    @Test
    public void testRunWorkflow() {
        ImportationController controller = new ImportationController();
        ImportWorkflow workflow = Mockito.mock(ImportWorkflow.class);

        controller.workflow = workflow;
        String catalog = UUID.randomUUID().toString();

        RedirectView redirectView = controller.runWorkflow(catalog);
        Assert.assertEquals(redirectView.getUrl(), "../" + ImportationController.PATH);

        Mockito.verify(workflow).startManually(Mockito.eq(catalog));
        Mockito.verifyNoMoreInteractions(workflow);
    }
}
