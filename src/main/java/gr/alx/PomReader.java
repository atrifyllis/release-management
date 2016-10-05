package gr.alx;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
@Slf4j
public class PomReader {

    List<Path> getAllPomPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(path -> "pom.xml".equalsIgnoreCase(path.getFileName().toString()))
                .filter(path -> !path.toString().contains("target"))
                .distinct()
                .collect(toList());

    }

    Model readPomFile(Path path) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(Files.newInputStream(path));
        } catch (IOException | XmlPullParserException e) {
            log.error("A problem occured while reading the file.", e);
        }
        return model;
    }
}
