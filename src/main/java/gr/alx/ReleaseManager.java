package gr.alx;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/2/2016.
 */
@Component
public class ReleaseManager implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        try {
            ConsoleReader console = new ConsoleReader();
            console.setPrompt("prompt> ");
            String line = null;
            while ((line = console.readLine()) != null) {
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                } else if (line.equalsIgnoreCase("release")) {
                    doRelease("build");
                }
                console.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doRelease(String type) throws IOException {
        List<Path> pomPaths = getAllPomPaths();
        pomPaths.forEach(path -> handleVersion(path, type));
    }


    List<Path> getAllPomPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(file -> file.getFileName().toString().equalsIgnoreCase("pom.xml"))
                .collect(toList());

    }

    void handleVersion(Path path, String type) {
        Model model = readPomFile(path);
        String newVersion = bumpUpVersion(model.getVersion(), type);
        model.setVersion(newVersion);
        writeNewVersion(path, model);
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

    String bumpUpVersion(String pomVersion, String type) {
        Version version = splitVersion(pomVersion);
        switch (type) {
            case "major":
                version.setMajor(version.getMajor() + 1);
                break;
            case "minor":
                version.setMinor(version.getMinor() + 1);
                break;
            case "build":
                version.setBuild(version.getBuild() + 1);
                break;
        }
        return version.toString();
    }

    private Version splitVersion(String version) {
        String major = version.substring(0, version.indexOf("."));
        String minor = version.substring(version.indexOf(".") + 1, version.lastIndexOf("."));
        String build = version.substring(version.lastIndexOf(".") + 1,
                version.lastIndexOf("-") != -1 ? version.lastIndexOf("-") : version.length());
        boolean isSnapshot = version.indexOf("-SNAPSHOT") != -1;
        return new Version(Integer.parseInt(major), Integer.parseInt(minor), Integer.parseInt(build), isSnapshot);
    }


    void writeNewVersion(Path path, Model model) {
        MavenXpp3Writer writer = new MavenXpp3Writer();
        OutputStream outputStream = null;
        try {
            outputStream = Files.newOutputStream(path);
            writer.write(outputStream, model);
            System.out.println("Updating pom version for artifact: " + model.getArtifactId() + " to: " + model.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

