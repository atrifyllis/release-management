package gr.alx;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/7/2016.
 */
public class PackageReader {

    private ObjectMapper objectMapper;

    @Autowired
    public PackageReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Path> getAllPackagePaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(path -> "package.json".equalsIgnoreCase(path.getFileName().toString()))
                .filter(path -> !path.toString().contains("target"))
                .distinct()
                .collect(toList());
    }

    public PackageJson readPackageFile(Path path) throws IOException {
        return objectMapper.readValue(Files.newInputStream(path), PackageJson.class);
    }
}
