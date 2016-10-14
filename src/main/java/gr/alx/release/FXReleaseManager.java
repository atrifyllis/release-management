package gr.alx.release;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.bower.BowerReader;
import gr.alx.release.bower.BowerWriter;
import gr.alx.release.packagejson.PackageReader;
import gr.alx.release.packagejson.PackageWriter;
import gr.alx.release.pom.PomReader;
import gr.alx.release.pom.PomWriter;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    static final List ALLOWED_BUMP_TYPES = Arrays.asList("major", "minor", BUILD, "prod", "snapshot");
    static final String INVALID_VERSION_FORMAT = "Invalid version format. The allowed format is of the form: " +
            "ddd.ddd.ddd[-SNAPSHOT] (i.e. 1.0.2)";
    static final String ALLOWED_ACTIONS_MESSAGE =
            "Allowed actions are:\n" +
                    "1) release [version]\n" +
                    "2) bump [type]";
    private static final int MAX_COMMAND_LENGTH = 2;
    private static final int LONG_VERSION_SIZE = 4;
    private static final int SHORT_VERSION_SIZE = 3;
    private static final String UPDATED_FILES_MESSAGE = "\nUpdated %d files in total\n";

    //    private ConsoleReader console;
    private final List<FileHandler> fileHandlers = new ArrayList<>();
    private FileReader fileReader;

    private Label console;

    public FXReleaseManager() {

    }

    /**
     * Initialisation constructor which initialise all dependent classes.
     */
    public FXReleaseManager(Label console) {
        try {
            this.console = console;
            ObjectMapper objectMapper = new ObjectMapper();
            // printInConsole(getAsciiArt());
            preLoadFiles();
            fileHandlers.addAll(
                    Arrays.asList(
                            new FileHandler(new PomReader(), new PomWriter()),
                            new FileHandler(new PackageReader(objectMapper), new PackageWriter()),
                            new FileHandler(new BowerReader(objectMapper), new BowerWriter())
                    )
            );
        } catch (IOException e) {
            log.error("An error occurred while initialising ConsoleReader.", e);
        }
    }

    private void preLoadFiles() throws IOException {
        printInConsole("Please wait while pre-loading files...");
        // flush the console before loading files
//        console.flush();
        fileReader = new FileReader();
        printInConsole("Files successfully loaded.");
    }

    private void setUpConsole() throws IOException {
//        console = new ConsoleReader();
//        File historyFile = new File(".rmhistory");
//        console.setHistory(new FileHistory(historyFile));
//        console.setHistoryEnabled(true);
    }

    /**
     * This is the entry point method.
     *
     * @param args parameters (if any) passed by the user.
     */
    public void run(String... args) {
        printInConsole("\nPlease enter a release command:\n");
        String line = null;
        while (line != null) {
            if ("quit".equalsIgnoreCase(line) || "exit".equalsIgnoreCase(line)) {
                break;
            } else if (Arrays.asList(line.split(" ")).size() == MAX_COMMAND_LENGTH) {
                doRelease(line);
            } else {
                printInConsole(ALLOWED_ACTIONS_MESSAGE);
            }
        }

    }

    private String getAsciiArt() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("asciiArt.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        return br.lines().collect(Collectors.joining("\n"));
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

    void doAutomaticVersion(String type) {
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

    void doManualVersion(String version) {
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

    void updateVersionInFile(Path path, String newVersion, FileHandler fileHandler) {
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

    void printInConsole(String writeMessage) {

        String previousText = console.getText();
        String newText = previousText + "\n" + writeMessage;
        console.setText(newText);

    }
}

