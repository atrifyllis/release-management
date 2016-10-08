package gr.alx.release.bower;

import gr.alx.release.FileRepresentation;
import gr.alx.release.Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alx on 10/8/2016.
 */
public class BowerReader implements Reader {
    @Override
    public List<Path> getAllPaths() throws IOException {
        return null;
    }

    @Override
    public FileRepresentation readFile(Path path) throws IOException, XmlPullParserException {
        return null;
    }
}
