package gr.alx;

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
public class PomReader {

    List<Path> getAllPomPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(file -> file.getFileName().toString().equalsIgnoreCase("pom.xml"))
                .collect(toList());

    }

    Model readPomFile(Path path) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(Files.newInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return model;
    }
}
