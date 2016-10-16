package gr.alx.release.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.configuration.Configuration;
import gr.alx.release.configuration.Configurator;
import gr.alx.release.types.FileReader;
import gr.alx.release.types.FileRepresentation;
import gr.alx.release.types.Reader;
import gr.alx.release.types.bower.BowerReader;
import gr.alx.release.types.bower.BowerWriter;
import gr.alx.release.types.packagejson.PackageReader;
import gr.alx.release.types.packagejson.PackageWriter;
import gr.alx.release.types.pom.PomReader;
import gr.alx.release.types.pom.PomWriter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alx on 10/16/2016.
 */
@Slf4j
public abstract class CommonReleaseManager {

    static final int MAX_COMMAND_LENGTH = 2;
    private static final int SHORT_VERSION_SIZE = 3;
    private static final int LONG_VERSION_SIZE = 4;


    private static final String BUILD = "build";
    private static final String RELEASE = "release";

    private static final List ALLOWED_ACTIONS = Arrays.asList(RELEASE, "bump");
    static final List ALLOWED_BUMP_TYPES = Arrays.asList("major", "minor", BUILD, "prod", "snapshot");

    private static final String AN_ERROR_OCCURRED_DURING_VERSION_UPDATE = "An error occurred during version update";
    static final String INVALID_VERSION_FORMAT = "Invalid version format. The allowed format is of the form: " +
            "ddd.ddd.ddd[-SNAPSHOT] (i.e. 1.0.2)";
    private static final String UPDATED_FILES_MESSAGE = "\nUpdated %d files in total\n";

    static final String ALLOWED_ACTIONS_MESSAGE =
            "Allowed actions are:\n" +
                    "1) release [version]\n" +
                    "2) bump [type]";

    private final List<FileHandler> fileHandlers = new ArrayList<>();
    private FileReader fileReader;

    void initialiseManager() {
        try {
            Configuration configuration = new Configurator().getConfiguration("configuration.yml");
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
            log.error("An error occurred while pre-loading files.", e);
        }
    }

    /**
     * Prints in a console the various output messages.
     * Console is implementation-specific that is why it is left abstract.
     *
     * @param s the output message
     */
    protected abstract void printInConsole(String s);

    /**
     * Parse user arguments and perform manual or automatic release actions.
     *
     * @param command the command entered by the user
     */
    public void doRelease(String command) {
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

    private void preLoadFiles(Configuration config) throws IOException {
        printInConsole("Please wait while pre-loading files...");
        fileReader = new FileReader(config);
        printInConsole("Files successfully loaded.");
    }

    private String generateNewVersionFromPath(Path path, String type, Reader reader)
            throws IOException, XmlPullParserException {
        FileRepresentation model = reader.readFile(path);
        return bumpUpVersion(model.getVersion(), type);
    }
}
