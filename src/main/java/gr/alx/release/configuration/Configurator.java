package gr.alx.release.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by alx on 10/15/2016.
 */
@Slf4j
public class Configurator {

    private final ObjectMapper om = new ObjectMapper(new YAMLFactory());

    /**
     * Retrieves a {@link Configuration} instance from a yaml file.
     *
     * @param path the path to the yaml file
     * @return the configuration instance
     */
    public Configuration getConfiguration(String path) {
        try {
            Path configPath = Paths.get(path);
            InputStream configStream = Files.newInputStream(configPath);
            return om.readValue(configStream, Configuration.class);
        } catch (IOException e) {
            log.warn("Error while reading file: " + path + "reverting to default configuration", e);
            return new Configuration(
                    new ExcludedElement(Arrays.asList("target", "node_modules", "bower_components")),
                    new IncludedElements(
                            new BowerElements(Arrays.asList("version"), new HashMap<>()),
                            Arrays.asList("version")
                    )
            );
        }
    }
}
