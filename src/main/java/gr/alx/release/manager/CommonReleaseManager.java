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
import gr.alx.release.types.pom.MavenFileRepresentation;
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
    private VersionHelper versionHelper;

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
            versionHelper = new VersionHelper();
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

        if (AllowedAction.isInvalidAction(action)) {
            printInConsole(ALLOWED_ACTIONS_MESSAGE);
        } else if (AllowedAction.BUMP.toString().equalsIgnoreCase(action)) {
            doAutomaticVersion(version);
        } else if (AllowedAction.RELEASE.toString().equalsIgnoreCase(action)) {
            doManualVersion(version);
        }
    }

    void doAutomaticVersion(String type) {
        AtomicInteger totalFiles = new AtomicInteger();
        if (AllowedBumpType.isBumpTypeValid(type)) {
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
        } else {
            printInConsole("Allowed bump types are: " + AllowedBumpType.names());
        }
    }

    void doManualVersion(String version) {
        AtomicInteger totalFiles = new AtomicInteger();
        if (versionHelper.isVersionValid(version)) {
            List<Path> allPaths = fileReader.getAllPaths();
            fileHandlers.forEach(handler -> handler.getReader().getAllPaths(allPaths)
                    .forEach(path -> {
                        updateVersionInFile(path, version, handler);
                        totalFiles.incrementAndGet();
                    }));
            printInConsole(String.format(UPDATED_FILES_MESSAGE, totalFiles.get()));
        } else {
            printInConsole(INVALID_VERSION_FORMAT);
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

    public Version retrievePomVersion() {
        PomReader pr = new PomReader();
        Path path = pr.getAllPaths(fileReader.getAllPaths()).get(0);
        try {
            MavenFileRepresentation pom = pr.readFile(path);
            return versionHelper.splitVersion(pom.getVersion());
        } catch (IOException | XmlPullParserException e) {
            log.error("Could not read file: " + path);
        }
        return null;
    }

    private void preLoadFiles(Configuration config) throws IOException {
        printInConsole("Please wait while pre-loading files...");
        fileReader = new FileReader(config);
        printInConsole("Files successfully loaded.");
        log.debug("Files have been pre-loaded");
    }

    private String generateNewVersionFromPath(Path path, String type, Reader reader)
            throws IOException, XmlPullParserException {
        FileRepresentation model = reader.readFile(path);
        return versionHelper.bumpUpVersion(versionHelper.splitVersion(model.getVersion()), type);
    }
}
