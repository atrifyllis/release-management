package gr.alx.release.types;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This interfaces represents the different type of readers one for each type of file.
 * <p>
 * Created by TRIFYLLA on 7/10/2016.
 */
public interface Writer {

    /**
     * Updates a specific file (path) using the implementation-specific file representation.
     *
     * @param path       the path to the file to be updated
     * @param oldVersion the previous version of the file
     * @param model      the file representation containing the new version
     * @return an informative message
     */
    String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException;
}
