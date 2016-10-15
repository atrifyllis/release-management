package gr.alx.release;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.bower.BowerReader;
import gr.alx.release.bower.BowerWriter;
import gr.alx.release.configuration.Configuration;
import gr.alx.release.configuration.Configurator;
import gr.alx.release.packagejson.PackageReader;
import gr.alx.release.packagejson.PackageWriter;
import gr.alx.release.pom.PomReader;
import gr.alx.release.pom.PomWriter;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central point of the application which manages the whole release process.
 * Created by alx on 10/2/2016.
 */
@Slf4j
public class FXReleaseManager {

    private static final String AN_ERROR_OCCURRED_DURING_VERSION_UPDATE = "An error occurred during version update";
    private static final String RELEASE = "release";
    private static final List ALLOWED_ACTIONS = Arrays.asList(RELEASE, "bump");
    private static final String BUILD = "build";
    private static final List ALLOWED_BUMP_TYPES = Arrays.asList("major", "minor", BUILD, "prod", "snapshot");
    private static final String INVALID_VERSION_FORMAT = "Invalid version format. The allowed format is of the form: " +
            "ddd.ddd.ddd[-SNAPSHOT] (i.e. 1.0.2)";
    private static final String ALLOWED_ACTIONS_MESSAGE =
            "Allowed actions are:\n" +
                    "1) release [version]\n" +
                    "2) bump [type]";
    private static final int MAX_COMMAND_LENGTH = 2;
    private static final int LONG_VERSION_SIZE = 4;
    private static final int SHORT_VERSION_SIZE = 3;
    private static final String UPDATED_FILES_MESSAGE = "\nUpdated %d files in total\n";

    private final List<FileHandler> fileHandlers = new ArrayList<>();
    private FileReader fileReader;
    private Label console;

    /**
     * Initialisation constructor which initialise all dependent classes.
     */
    public FXReleaseManager(Label console) {
        try {
            Configuration configuration = new Configurator().getConfiguration("configuration.yml");
            this.console = console;
            ObjectMapper objectMapper = new ObjectMapper();
            preLoadFiles(configuration);
            fileHandlers.addAll(
                    Arrays.asList(
                            new FileHandler(new PomReader(), new PomWriter()),
                            new FileHandler(new PackageReader(objectMapper), new PackageWriter(configuration)),
                            new FileHandler(new BowerReader(objectMapper), new BowerWriter(configuration))
                    )
            );
        } catch (IOException e) {
            log.error("An error occurred while initialising ConsoleReader.", e);
        }
    }

    private void preLoadFiles(Configuration config) throws IOException {
        printInConsole("Please wait while pre-loading files...");
        fileReader = new FileReader(config);
        printInConsole("Files successfully loaded.");
    }

    /**
     * Parse user arguments and perform manual or automatic release actions.
     *
     * @param command the command entered by the user
     */
    public void doRelease(String command) {
        if (Arrays.asList(command.split(" ")).size() == MAX_COMMAND_LENGTH) {
            List<String> arguments = Arrays.asList(command.split(" "));
            String action = arguments.get(0);
            String version = arguments.get(1);

            if (!ALLOWED_ACTIONS.contains(action)) {
                printInConsole(ALLOWED_ACTIONS_MESSAGE);
            } else if ("bump".equalsIgnoreCase(action)) {
                doAutomaticVersion(version);
            } else if (RELEASE.equalsIgnoreCase(action)) {
                doManualVersion(version);
            }
        } else {
            printInConsole(ALLOWED_ACTIONS_MESSAGE);
        }

    }

    private void doAutomaticVersion(String type) {
        AtomicInteger totalFiles = new AtomicInteger();
        if (!ALLOWED_BUMP_TYPES.contains(type)) {
            printInConsole("Allowed bump types are: " + ALLOWED_BUMP_TYPES);
        } else {
            List<Path> allPaths = fileReader.getAllPaths();
            fileHandlers.forEach(handler -> {
                try {
                    List<Path> paths = handler.getReader().getAllPaths(allPaths);
                    String newVersion = generateNewVersionFromPath(paths.get(0), type, handler.getReader());
                    paths.forEach(path -> {
                        updateVersionInFile(path, newVersion, handler);
                        totalFiles.incrementAndGet();
                    });
                } catch (IOException | XmlPullParserException e) {
                    printInConsole(AN_ERROR_OCCURRED_DURING_VERSION_UPDATE);
                    log.error(AN_ERROR_OCCURRED_DURING_VERSION_UPDATE, e);
                }
            });
            printInConsole(String.format(UPDATED_FILES_MESSAGE, totalFiles.get()));
        }
    }

    private void doManualVersion(String version) {
        AtomicInteger totalFiles = new AtomicInteger();
        if (!validVersion(version)) {
            printInConsole(INVALID_VERSION_FORMAT);
        } else {
            List<Path> allPaths = fileReader.getAllPaths();
            fileHandlers.forEach(handler -> handler.getReader().getAllPaths(allPaths)
                    .forEach(path -> {
                        updateVersionInFile(path, version, handler);
                        totalFiles.incrementAndGet();
                    }));
            printInConsole(String.format(UPDATED_FILES_MESSAGE, totalFiles.get()));
        }
    }

    private void updateVersionInFile(Path path, String newVersion, FileHandler fileHandler) {
        try {
            FileRepresentation model = fileHandler.getReader().readFile(path);
            String oldVersion = model.getVersion();
            model.setVersion(newVersion);
            String writeMessage = fileHandler.getWriter().writeNewVersion(path, oldVersion, model);
            printInConsole(writeMessage);
        } catch (IOException | XmlPullParserException e) {
            String error = "An error occurred during processing of the file: " + path;
            printInConsole(error);
            log.error(error, e);
        }
    }

    boolean validVersion(String version) {
        String validVersionRegEx = "\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?(-SNAPSHOT)?";
        return version.matches(validVersionRegEx);
    }

    Version splitVersion(String version) {
        List<String> versionParts = Arrays.asList(version.split("\\.|-"));
        if (versionParts.size() != SHORT_VERSION_SIZE && versionParts.size() != LONG_VERSION_SIZE) {
            throw new IllegalArgumentException("Version is not valid: " + version);
        }
        return new Version(
                Integer.valueOf(versionParts.get(0)),
                Integer.valueOf(versionParts.get(1)),
                Integer.valueOf(versionParts.get(2)),
                versionParts.size() == LONG_VERSION_SIZE
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
            case BUILD:
                version.setBuild(version.getBuild() + 1);
                break;
            case "prod":
                version.setSnapshot(false);
                break;
            case "snapshot":
                version.setSnapshot(true);
                break;
            default:
                break;
        }
        return version.toString();
    }

    private String generateNewVersionFromPath(Path path, String type, Reader reader)
            throws IOException, XmlPullParserException {
        FileRepresentation model = reader.readFile(path);
        return bumpUpVersion(model.getVersion(), type);
    }

    private void printInConsole(String writeMessage) {

        String previousText = console.getText();
        String newText = previousText + "\n" + writeMessage;
        console.setText(newText);

    }
}

