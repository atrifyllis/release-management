package gr.alx.release;

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
public class FileReader {

    List<Path> allPaths = new ArrayList<>();

    /**
     * Cache list of file on creation.
     *
     * @throws IOException
     */
    public FileReader() throws IOException {
        allPaths = loadAllPaths();
    }

    /**
     * Retrieves cached list of paths (or reloads it if empty).
     *
     * @return the list of all file paths from root folder
     */
    public List<Path> getAllPaths() {
        try {
            return allPaths.size() != 0 ? allPaths : loadAllPaths();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<Path> loadAllPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(filterFileTypes())
                .filter(path -> !path.toString().contains("target"))
                .filter(path -> !path.toString().contains("node_modules"))
                .filter(path -> !path.toString().contains("bower_components"))
                .filter(path -> !path.toString().contains("automation"))
                .distinct()
                .collect(toList());
    }

    private Predicate<Path> filterFileTypes() {
        return path -> "pom.xml".equalsIgnoreCase(path.getFileName().toString()) ||
                "package.json".equalsIgnoreCase(path.getFileName().toString()) ||
                "bower.json".equalsIgnoreCase(path.getFileName().toString());
    }

}
