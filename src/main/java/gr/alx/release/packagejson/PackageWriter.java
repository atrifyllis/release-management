package gr.alx.release.packagejson;

import gr.alx.release.FileRepresentation;
import gr.alx.release.JsonWriterHelper;
import gr.alx.release.Writer;
import gr.alx.release.configuration.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
@Slf4j
public class PackageWriter implements Writer {

    private final Configuration configuration;

    public PackageWriter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException {
        String versionWithoutSnapshot = JsonWriterHelper.stripSnapshot(model.getVersion());
        List<String> properties = configuration.getIncludes().getPackagejson();
        List<String> lines = Files.lines(path).collect(toList());

        List<String> newLines = lines.stream()
                .map(updateLine(versionWithoutSnapshot, properties))
                .collect(toList());

        Files.write(path, newLines);

        return "Updating package.json version for artifact: " + model.getArtifactId()
                + " from: " + oldVersion
                + " to: " + versionWithoutSnapshot;
    }

    private Function<String, String> updateLine(String versionWithoutSnapshot, List<String> properties) {
        return line -> {
            Optional<String> prop = properties.stream()
                    .filter(property -> line.contains("\"" + property + "\"" + ":"))
                    .findFirst();
            return prop.isPresent() ?
                    leadingSpaces(line, prop.get()) + "\"" + prop.get() + "\": \"" + versionWithoutSnapshot + "\"," :
                    line;
        };
    }

    private String leadingSpaces(String line, String property) {
        int leadingSpacesIndex = line.indexOf(property);
        return line.substring(0, leadingSpacesIndex - 1);
    }
}
