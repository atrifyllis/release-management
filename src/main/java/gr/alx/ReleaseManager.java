package gr.alx;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import org.apache.maven.model.Model;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alx on 10/2/2016.
 */
@Component
public class ReleaseManager {

    private static List allowedActions = Arrays.asList("release", "bump");
    private static List allowedTypes = Arrays.asList("major", "minor", "build", "prod", "snapshot");
    private static String validVersionRegEx = "\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?(-SNAPSHOT)?";
    private static final String INVALID_VERSION_FORMAT = "Invalid version format. The allowed format is of the form: " +
            "ddd.ddd.ddd.-SNAPSHOT";

    ConsoleReader console;
    PomReader pomReader;
    PomWriter pomWriter;

    public ReleaseManager() {
        try {
            console = new ConsoleReader();
            pomReader = new PomReader();
            pomWriter = new PomWriter();
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
                    doBumpVersion("build");
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
                        doBumpVersion(version);
                    }

                    if (action.equalsIgnoreCase("release")) {
                        if (!validVersion(version)) {
                            console.println(INVALID_VERSION_FORMAT);
                            continue;
                        }
                        doManualRelease(version);
                    }
                } else {
                    console.println("Allowed actions are: " + allowedActions.toString());
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

    void doBumpVersion(String type) throws IOException {
        List<Path> pomPaths = pomReader.getAllPomPaths();
        pomPaths.forEach(path -> bumpVersionInPom(path, type));
    }

    void bumpVersionInPom(Path path, String type) {
        Model model = pomReader.readPomFile(path);
        String oldVersion = model.getVersion();
        String newVersion = bumpUpVersion(oldVersion, type);
        model.setVersion(newVersion);
        String writeMessage = pomWriter.writeNewVersion(path, oldVersion, model);
        printInConsole(writeMessage);
    }

    void doManualRelease(String version) throws IOException {
        List<Path> pomPaths = pomReader.getAllPomPaths();
        pomPaths.forEach(path -> updateVersionInPom(path, version));
    }

    void updateVersionInPom(Path path, String newVersion) {
        Model model = pomReader.readPomFile(path);
        String oldVersion = model.getVersion();
        model.setVersion(newVersion);
        String writeMessage = pomWriter.writeNewVersion(path, oldVersion, model);
        printInConsole(writeMessage);
    }

    boolean validVersion(String version) {
        return version.matches(validVersionRegEx);
    }

    Version splitVersion(String version) {
        List<String> versionParts = Arrays.asList(version.split("\\.|-"));
        if (versionParts.size() != 3 && versionParts.size() != 4) {
            throw new IllegalArgumentException("Version is not valid: " + version);
        }
        return new Version(
                Integer.parseInt(versionParts.get(0)),
                Integer.parseInt(versionParts.get(1)),
                Integer.parseInt(versionParts.get(2)),
                versionParts.size() == 4
        );
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
            case "prod":
                version.setSnapshot(false);
                break;
            case "snapshot":
                version.setSnapshot(true);
        }
        return version.toString();
    }

    private void printInConsole(String writeMessage) {
        try {
            console.println(writeMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

