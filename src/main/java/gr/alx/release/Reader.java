package gr.alx.release;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * This interface represents the different type of readers one for each type of file.
 * <p>
 * Created by TRIFYLLA on 7/10/2016.
 */
public interface Reader {

    /**
     * Retrieve all paths starting by a default base paths, filtering for specific file types.
     *
     * @return a list of {@link Path}s of specific type
     * @throws IOException in case a read error occurs
     */
    List<Path> getAllPaths() throws IOException;

    /**
     * Reads the file to an implementation-specific file representation.
     *
     * @param path the path to the file
     * @return the java representation of the file
     * @throws IOException in case a read error occurs
     */
    FileRepresentation readFile(Path path) throws IOException, XmlPullParserException;
}
