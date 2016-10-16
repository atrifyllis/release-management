package gr.alx.release.types;

import gr.alx.release.configuration.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * Read all files from the path keeping only the type of files we want.
 * <p>
 * Created by TRIFYLLA on 10/10/2016.
 */
@Slf4j
public class FileReader {

    private final Configuration configuration;
    private List<Path> allPaths = new ArrayList<>();

    /**
     * Cache list of file on creation.
     * @param configuration the configuration loaded from yaml file
     * @throws IOException if the loading of files fails
     */
    public FileReader(Configuration configuration) throws IOException {
        this.configuration = configuration;
        allPaths = loadAllPaths();
    }

    /**
     * Retrieves cached list of paths (or reloads it if empty).
     *
     * @return the list of all file paths from root folder
     */
    public List<Path> getAllPaths() {
        try {
            return allPaths.isEmpty() ? loadAllPaths() : allPaths;
        } catch (IOException e) {
            log.error("Could not load files", e);
        }
        return new ArrayList<>();
    }

    private List<Path> loadAllPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(filterFileTypes())
                .filter(filterOutExcludedFolders())
                .distinct()
                .collect(toList());
    }

    private Predicate<Path> filterFileTypes() {
        return path -> "pom.xml".equalsIgnoreCase(path.getFileName().toString()) ||
                "package.json".equalsIgnoreCase(path.getFileName().toString()) ||
                "bower.json".equalsIgnoreCase(path.getFileName().toString());
    }

    private Predicate<Path> filterOutExcludedFolders() {
        return path -> configuration.getExcludes().getFolders()
                .stream()
                .noneMatch(path.toString()::contains);
    }
}
