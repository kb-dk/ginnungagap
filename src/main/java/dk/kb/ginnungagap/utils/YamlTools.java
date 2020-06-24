package dk.kb.ginnungagap.utils;

import dk.kb.ginnungagap.exception.YamlException;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

/** Class for reading YAML v 1.1 configuration files. */
public class YamlTools {
    /**
     * Load YAML settings in the given file.
     * @param ymlFile The settings file in YAML format to load
     * @throws YamlException If settings file was not found
     * or experienced wrong datastructure.
     * @return the loaded settings as a {@link LinkedHashMap}
     */
    public static LinkedHashMap<String, LinkedHashMap> loadYamlSettings(File ymlFile)
            throws YamlException {
        InputStream input = null;
        Object loadedSettings = null;
        try {
            input = new FileInputStream(ymlFile);
            loadedSettings = new Yaml().load(input);
            if (!(loadedSettings instanceof LinkedHashMap)) {
                throw new YamlException("Internal error. Unable to read settings. Excepted load method to "
                        + "return a LinkedHashMap, but it returned a " + loadedSettings.getClass().getName() 
                        + " instead");
            }
        } catch (IOException e) {
            throw new YamlException("Internal error. Unable to read settings from file '"
                    + ymlFile.getAbsolutePath() + "'. Reason:  ", e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return (LinkedHashMap<String, LinkedHashMap>) loadedSettings;
    }
}
