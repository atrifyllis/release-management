package gr.alx.release.bower;

import gr.alx.release.FileRepresentation;
import gr.alx.release.Writer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by alx on 10/8/2016.
 */
public class BowerWriter implements Writer {
    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException {
        return null;
    }
}
