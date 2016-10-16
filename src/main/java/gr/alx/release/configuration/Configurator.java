package gr.alx.release.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by alx on 10/15/2016.
 */
@Slf4j
public class Configurator {

    private final ObjectMapper om = new ObjectMapper(new YAMLFactory());

    public Configuration getConfiguration(String path) {
        try {
            Path configPath = Paths.get(path);
            InputStream configStream = Files.newInputStream(configPath);
            return om.readValue(configStream, Configuration.class);
        } catch (IOException e) {
            log.error("Error while reading file: " + path, e);
        }
        return null;
    }
}
