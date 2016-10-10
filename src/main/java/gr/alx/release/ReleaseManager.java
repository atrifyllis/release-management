package gr.alx.release;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.bower.BowerReader;
import gr.alx.release.bower.BowerWriter;
import gr.alx.release.packagejson.PackageReader;
import gr.alx.release.packagejson.PackageWriter;
import gr.alx.release.pom.PomReader;
import gr.alx.release.pom.PomWriter;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central point of the application which manages the whole release process.
 * Created by alx on 10/2/2016.
 */
@Slf4j
public class ReleaseManager {

    private static final String RELEASE = "release";
    private static final List allowedActions = Arrays.asList(RELEASE, "bump");
    private static final String BUILD = "build";
    static final List allowedBumpTypes = Arrays.asList("major", "minor", BUILD, "prod", "snapshot");
    static final String INVALID_VERSION_FORMAT = "Invalid version format. The allowed format is of the form: " +
            "ddd.ddd.ddd[-SNAPSHOT] (i.e. 1.0.2)";
    static final String ALLOWED_ACTIONS_MESSAGE =
            "Allowed actions are:\n" +
                    "1) release [version]\n" +
                    "2) bump [type]";
    public static final String AN_ERROR_OCCURRED_DURING_VERSION_UPDATE = "An error occurred during version update";

    private ConsoleReader console;
    private final List<FileHandler> fileHandlers = new ArrayList<>();
    private FileReader fileReader;

    /**
     * Initialisation constructor which initialise all dependent classes
     */
    public ReleaseManager() {
        try {
            setUpConsole();
            ObjectMapper objectMapper = new ObjectMapper();
            printInConsole(getAsciiArt());
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
        console.flush();
        fileReader = new FileReader();
        printInConsole("Files successfully loaded.");
    }

    private void setUpConsole() throws IOException {
        console = new ConsoleReader();
        File historyFile = new File(".rmhistory");
        console.setHistory(new FileHistory(historyFile));
        console.setHistoryEnabled(true);
    }

    /**
     * This is the entry point method.
     *
     * @param args parameters (if any) passed by the user.
     */
    public void run(String... args) {
        try {
            printInConsole("Please enter a release command:");
            console.setPrompt("$ ");
            String line;
            while ((line = console.readLine()) != null) {
                if ("quit".equalsIgnoreCase(line) || "exit".equalsIgnoreCase(line)) {
                    break;
                } else if (Arrays.asList(line.split(" ")).size() == 2) {
                    doRelease(line);
                } else {
                    printInConsole(ALLOWED_ACTIONS_MESSAGE);
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while running the release process", e);
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                log.error("An error occurred while finalising the release process.", e);
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
        List<String> arguments = Arrays.asList(command.split(" "));
        String action = arguments.get(0);
        String version = arguments.get(1);

        if (!allowedActions.contains(action)) {
            printInConsole(ALLOWED_ACTIONS_MESSAGE);
        } else if ("bump".equalsIgnoreCase(action)) {
            doAutomaticVersion(version);
        } else if (RELEASE.equalsIgnoreCase(action)) {
            doManualVersion(version);
        }
    }

    void doAutomaticVersion(String type) {
        if (!allowedBumpTypes.contains(type)) {
            printInConsole("Allowed bump types are: " + allowedBumpTypes);
        } else {
            List<Path> allPaths = fileReader.getAllPaths();
            fileHandlers.forEach(handler -> {
                List<Path> paths = null;
                try {
                    paths = handler.getReader().getAllPaths(allPaths);
                    String newVersion = generateNewVersionFromPath(paths.get(0), type, handler.getReader());
                    paths.forEach(path -> updateVersionInFile(path, newVersion, handler));
                } catch (IOException | XmlPullParserException e) {
                    printInConsole(AN_ERROR_OCCURRED_DURING_VERSION_UPDATE);
                    log.error(AN_ERROR_OCCURRED_DURING_VERSION_UPDATE, e);
                }
            });
        }
    }

    void doManualVersion(String version) {
        if (!validVersion(version)) {
            printInConsole(INVALID_VERSION_FORMAT);
        } else {
            List<Path> allPaths = fileReader.getAllPaths();
            fileHandlers.forEach(handler -> {
                try {
                    handler.getReader().getAllPaths(allPaths)
                            .forEach(path -> updateVersionInFile(path, version, handler));
                } catch (IOException e) {
                    printInConsole(AN_ERROR_OCCURRED_DURING_VERSION_UPDATE);
                    log.error(AN_ERROR_OCCURRED_DURING_VERSION_UPDATE, e);
                }
            });
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
        if (versionParts.size() != 3 && versionParts.size() != 4) {
            throw new IllegalArgumentException("Version is not valid: " + version);
        }
        return new Version(
                Integer.valueOf(versionParts.get(0)),
                Integer.valueOf(versionParts.get(1)),
                Integer.valueOf(versionParts.get(2)),
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

    private String generateNewVersionFromPath(Path path, String type, Reader reader) throws IOException, XmlPullParserException {
        FileRepresentation model = reader.readFile(path);
        return bumpUpVersion(model.getVersion(), type);
    }

    void printInConsole(String writeMessage) {
        try {
            console.println(writeMessage);
        } catch (Exception e) {
            log.error("An error occurred while printing in the console the message: " + writeMessage, e);
        }
    }
}

