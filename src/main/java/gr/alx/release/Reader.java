package gr.alx.release;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public interface Reader {

    List<Path> getAllPaths() throws IOException;

    FileRepresentation readFile(Path path) throws IOException;
}
