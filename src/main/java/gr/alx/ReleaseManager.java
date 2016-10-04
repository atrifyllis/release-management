package gr.alx;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/2/2016.
 */
@Component
public class ReleaseManager {

    ConsoleReader console;
    private static List allowedActions = Arrays.asList("release", "bump");
    private static List allowedTypes = Arrays.asList("major", "minor", "build", "prod", "snapshot");
    private String validVersionRegEx = "^\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?(-SNAPSHOT)?$";
    private static final String INVALID_VERSION_FORMAT = "Invalid version format. The allowed format is of the form: ddd.ddd.ddd.-SNAPSHOT";

    public ReleaseManager() {
        try {
            console = new ConsoleReader();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(String... args) throws Exception {
        try {
            console.setPrompt("prompt> ");
            String line;
            while ((line = console.readLine()) != null) {
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                } else if (line.equalsIgnoreCase("release")) {
                    doBump("build");
                } else if (Arrays.asList(line.split(" ")).size() == 2) {
                    List<String> arguments = Arrays.asList(line.split(" "));

                    String action = arguments.get(0);
                    String version = arguments.get(1);

                    if (!allowedActions.contains(action)) {
                        console.println("Allowed actions are: " + allowedActions.toString());
                        continue;
                    }

                    if (action.equalsIgnoreCase("bump")) {
                        if (!allowedTypes.contains(version)) {
                            console.println("Allowed types are: " + allowedTypes.toString());
                            continue;
                        }
                        doBump(version);
                    }

                    if (action.equalsIgnoreCase("release")) {
                        if (!validVersion(version)) {
                            console.println(INVALID_VERSION_FORMAT);
                            continue;
                        }
                        doManualRelease(version);
                    }
                }
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

    protected void doBump(String type) throws IOException {
        List<Path> pomPaths = getAllPomPaths();
        pomPaths.forEach(path -> handleVersion(path, type));
    }


    protected List<Path> getAllPomPaths() throws IOException {
        return Files.walk(Paths.get(""))
                .filter(file -> file.getFileName().toString().equalsIgnoreCase("pom.xml"))
                .collect(toList());

    }

    protected void handleVersion(Path path, String type) {
        Model model = readPomFile(path);
        String oldVersion = model.getVersion();
        String newVersion = bumpUpVersion(oldVersion, type);
        model.setVersion(newVersion);
        writeNewVersion(path, oldVersion, model);
    }

    protected Model readPomFile(Path path) {
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


    protected void doManualRelease(String version) throws IOException {
        List<Path> pomPaths = getAllPomPaths();
        pomPaths.forEach(path -> updateVersion(path, version));
    }

    protected void updateVersion(Path path, String newVersion) {
        Model model = readPomFile(path);
        String oldVersion = model.getVersion();
        model.setVersion(newVersion);
        writeNewVersion(path, oldVersion, model);
    }

    protected boolean validVersion(String version) {
        return version.matches(validVersionRegEx);
    }

    protected Version splitVersion(String version) {
        Version splitVersion = null;
        try {
            List<String> versionParts = Arrays.asList(version.split(validVersionRegEx));
            splitVersion = new Version(
                    Integer.parseInt(versionParts.get(0)),
                    Integer.parseInt(versionParts.get(1)),
                    Integer.parseInt(versionParts.get(2)),
                    versionParts.size() == 3
            );
        } catch (PatternSyntaxException e) {
            try {
                console.println(INVALID_VERSION_FORMAT);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return splitVersion;
    }


    protected String bumpUpVersion(String pomVersion, String type) {
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
            case "prod":
                version.setSnapshot(false);
                break;
            case "snapshot":
                version.setSnapshot(true);
        }
        return version.toString();
    }

    protected void writeNewVersion(Path path, String oldVersion, Model model) {
        List<String> newLines = new ArrayList<>();
        try {
            List<String> lines = Files.lines(path).collect(toList());
            boolean updated = false;
            for (String line : lines) {
                if (line.contains("<version>") && line.contains("</version>") && !updated) {
                    line = "    <version>" + model.getVersion() + "</version>";
                    updated = true;
                }
                newLines.add(line);
            }

            Files.write(path, newLines);
            console.println("Updating pom version for artifact: " + model.getArtifactId()
                    + " from: " + oldVersion
                    + " to: " + model.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    private Version splitVersion(String version) {
//        String major = version.substring(0, version.indexOf("."));
//        String minor = version.substring(version.indexOf(".") + 1, version.lastIndexOf("."));
//        String build = version.substring(version.lastIndexOf(".") + 1,
//                version.lastIndexOf("-") != -1 ? version.lastIndexOf("-") : version.length());
//        boolean isSnapshot = version.indexOf("-SNAPSHOT") != -1;
//        return new Version(Integer.parseInt(major), Integer.parseInt(minor), Integer.parseInt(build), isSnapshot);
//    }
}

