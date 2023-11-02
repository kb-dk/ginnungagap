package dk.kb.ginnungagap.archive;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import dk.kb.metadata.selector.AgentSelector;

/**
 * Constants regarding the creation of the WARC info record.
 */
public class WarcInfoConstants {
    /** The header value for WARC info records.*/
    protected static final String INFO_RECORD_HEADER = 
            "description: http://id.kb.dk/authorities/agents/kbDkCumulusBevaringsService.xml\n"
                    + "version: " + AgentSelector.getApiAgentValue() + "\n"
                    + "conformsTo: ISO 28500\n"
                    + "version: 1.0\n";
    
    /** The list of preservable system properties for the WarcInfo.*/
    public static final Collection<String> SYSTEM_PROPERTIES = Collections.unmodifiableList(Arrays.asList(
            "awt.toolkit",
            "file.encoding",
            "file.encoding.pkg",
            "file.separator",
            "java.awt.graphicsenv",
            "java.class.version",
            "java.runtime.version",
            "java.specification.version",
            "java.vm.info",
            "java.vm.name",
            "java.vm.specification.name",
            "java.vm.version",
            "java.vm.vendor",
            "line.separator",
            "os.arch",
            "os.name",
            "os.version",
            "sun.arch.data.model",
            "sun.boot.library.path",
            "sun.cpu.endian",
            "sun.io.unicode.encoding",
            "sun.java.launcher",
            "sun.jnu.encoding",
            "sun.management.compiler",
            "sun.os.patch.level",
            "user.country",
            "user.home",
            "user.timezone",
            "user.language"
            ));
    
    /** The list of preservable environment variables for the WarcInfo.*/
    public static final Collection<String> ENV_VARIABLES = Collections.unmodifiableList(Arrays.asList(
            "HOSTNAME",
            "SSH_CONNECTION"
            ));
}
