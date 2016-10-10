package gr.alx.release.packagejson;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/7/2016.
 */
public class PackageReader implements Reader {

    private final ObjectMapper objectMapper;

    /**
     * Constructor used to pass the json object mapper.
     *
     * @param objectMapper the jackson object mapper
     */
    public PackageReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Path> getAllPaths(List<Path> paths) {
        return paths.stream()
                .filter(path -> "package.json".equalsIgnoreCase(path.getFileName().toString()))
                .distinct()
                .collect(toList());
    }

    @Override
    public PackageFileRepresentation readFile(Path path) throws IOException {
        return objectMapper.readValue(Files.newInputStream(path), PackageFileRepresentation.class);
    }
}
