package gr.alx;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public interface Reader<T extends FileRepresentation> {

    List<Path> getAllPaths() throws IOException;

    T readFile(Path path) throws IOException;
}
