package gr.alx.release.pom;

import gr.alx.release.Reader;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
@Slf4j
public class PomReader implements Reader {

    @Override
    public List<Path> getAllPaths(List<Path> paths) {
        return paths.stream()
                .filter(path -> "pom.xml".equalsIgnoreCase(path.getFileName().toString()))
                .distinct()
                .collect(toList());

    }

    @Override
    public MavenFileRepresentation readFile(Path path) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(Files.newInputStream(path));
        return new MavenFileRepresentation(model);
    }
}
