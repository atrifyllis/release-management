package gr.alx.release.types.bower;

import gr.alx.release.configuration.BowerElements;
import gr.alx.release.configuration.Configuration;
import gr.alx.release.types.FileRepresentation;
import gr.alx.release.types.JsonWriterHelper;
import gr.alx.release.types.Writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/8/2016.
 */
public class BowerWriter implements Writer {

    private static final String TRAILING_STRING = "\",";
    private final Configuration configuration;

    public BowerWriter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException {
        String versionWithoutSnapshot = JsonWriterHelper.stripSnapshot(model.getVersion());

        BowerElements bowerElements = configuration.getIncludes().getBower();
        List<String> defaultProperties = bowerElements.getDefaults();
        Map<String, List<String>> propertiesWithEndings = bowerElements.getEndings();

        List<String> lines = Files.lines(path).collect(toList());
        List<String> newLines = lines.stream()
                .map(updateDefaultProperties(versionWithoutSnapshot, defaultProperties))
                .map(updatePropertiesWithEnding(versionWithoutSnapshot, propertiesWithEndings))
                .collect(toList());

        Files.write(path, newLines);

        return "Updating bower.json version for artifact: " + model.getArtifactId()
                + " from: " + oldVersion
                + " to: " + versionWithoutSnapshot;
    }

    private Function<String, String> updateDefaultProperties(String versionWithoutSnapshot, List<String> defaultProperties) {
        return line -> {
            Optional<String> prop = defaultProperties.stream()
                    .filter(property -> line.contains("\"" + property + "\"" + ":"))
                    .findFirst();
            return prop.isPresent() ?
                    leadingSpaces(line, prop.get()) + "\"" + prop.get() + "\": \"" + versionWithoutSnapshot + TRAILING_STRING :
                    line;
        };
    }

    private Function<String, String> updatePropertiesWithEnding(String versionWithoutSnapshot, Map<String, List<String>> propertiesWithEndings) {
        return line -> {
            Optional<Map.Entry<String, List<String>>> propWithEnding = propertiesWithEndings.entrySet().stream()
                    .filter(stringListEntry -> stringListEntry.getValue().stream()
                            .anyMatch(prop -> line.contains("\"" + prop + ".") &&
                                    line.endsWith(stringListEntry.getKey() + TRAILING_STRING)))
                    .findFirst();

            return propWithEnding.isPresent() ?
                    leadingSpaces(line, propWithEnding.get().getValue().get(0)) + "\"" + propWithEnding.get().getValue().get(0) + "." + versionWithoutSnapshot + "." + propWithEnding.get().getKey() + TRAILING_STRING :
                    line;
        };
    }

    private String leadingSpaces(String line, String property) {
        int leadingSpacesIndex = line.indexOf(property);
        return line.substring(0, leadingSpacesIndex - 1);
    }
}

