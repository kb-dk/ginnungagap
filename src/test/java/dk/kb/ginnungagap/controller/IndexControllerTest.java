package dk.kb.ginnungagap.controller;

import dk.kb.ginnungagap.GinnungagapConstants;
import dk.kb.ginnungagap.config.BitmagConfiguration;
import dk.kb.ginnungagap.config.Configuration;
import dk.kb.ginnungagap.config.LocalConfiguration;
import dk.kb.ginnungagap.config.TransformationConfiguration;
import dk.kb.ginnungagap.config.ViewableCumulusConfiguration;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class IndexControllerTest extends ExtendedTestCase {
    @Test
    public void testGetIndex() {
        IndexController controller = new IndexController();
        RedirectView redirectView = controller.getIndex();

        Assert.assertEquals(redirectView.getUrl(), IndexController.PATH);
    }

    @Test
    public void testGetGinnungagap() {
        IndexController controller = new IndexController();
        Configuration conf = Mockito.mock(Configuration.class);
        ViewableCumulusConfiguration viewableCumulusConfiguration = Mockito.mock(ViewableCumulusConfiguration.class);
        LocalConfiguration localConfiguration = Mockito.mock(LocalConfiguration.class);
        BitmagConfiguration bitmagConfiguration = Mockito.mock(BitmagConfiguration.class);
        TransformationConfiguration transformationConfiguration = Mockito.mock(TransformationConfiguration.class);

        Mockito.when(conf.getViewableCumulusConfiguration()).thenReturn(viewableCumulusConfiguration);
        Mockito.when(conf.getLocalConfiguration()).thenReturn(localConfiguration);
        Mockito.when(conf.getBitmagConf()).thenReturn(bitmagConfiguration);
        Mockito.when(conf.getTransformationConf()).thenReturn(transformationConfiguration);

        GinnungagapConstants constants = Mockito.mock(GinnungagapConstants.class);
        String version = UUID.randomUUID().toString();
        Mockito.when(constants.getBuildVersion()).thenReturn(version);

        Model model = Mockito.mock(Model.class);

        controller.conf = conf;
        controller.constants = constants;

        String path = controller.getGinnungagap(model);

        Assert.assertEquals(path, IndexController.PATH);

        Mockito.verify(model).addAttribute(Mockito.eq("cumulusConf"), Mockito.eq(viewableCumulusConfiguration));
        Mockito.verify(model).addAttribute(Mockito.eq("localConf"), Mockito.eq(localConfiguration));
        Mockito.verify(model).addAttribute(Mockito.eq("bitmagConf"), Mockito.eq(bitmagConfiguration));
        Mockito.verify(model).addAttribute(Mockito.eq("transformationConf"), Mockito.eq(transformationConfiguration));
        Mockito.verify(model).addAttribute(Mockito.eq("version"), Mockito.eq(version));
        Mockito.verifyNoMoreInteractions(model);

        Mockito.verify(conf).getViewableCumulusConfiguration();
        Mockito.verify(conf).getLocalConfiguration();
        Mockito.verify(conf).getBitmagConf();
        Mockito.verify(conf).getTransformationConf();
        Mockito.verifyNoMoreInteractions(conf);

        Mockito.verify(constants).getBuildVersion();
        Mockito.verifyNoMoreInteractions(constants);
    }
}
