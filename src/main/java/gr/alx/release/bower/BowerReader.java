package gr.alx.release.bower;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/8/2016.
 */
public class BowerReader implements Reader {

    private final ObjectMapper objectMapper;

    /**
     * Constructor used to pass the json object mapper.
     *
     * @param objectMapper the jackson object mapper
     */
    public BowerReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Path> getAllPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(path -> "bower.json".equalsIgnoreCase(path.getFileName().toString()))
                .filter(path -> !path.toString().contains("target"))
                .distinct()
                .collect(toList());
    }

    @Override
    public BowerFileRepresentation readFile(Path path) throws IOException {
        return objectMapper.readValue(Files.newInputStream(path), BowerFileRepresentation.class);
    }
}
