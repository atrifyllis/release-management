package gr.alx.release.bower;

import gr.alx.release.FileRepresentation;
import gr.alx.release.JsonWriterHelper;
import gr.alx.release.Writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/8/2016.
 */
public class BowerWriter implements Writer {
    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException {
        List<String> newLines = new ArrayList<>();
        String versionWithoutSnapshot = JsonWriterHelper.stripSnapshot(model.getVersion());
        List<String> lines = Files.lines(path).collect(toList());
        for (String line : lines) {
            String updatedLine = line;
            if (line.contains("\"version\":")) {
                updatedLine = "  \"version\": \"" + versionWithoutSnapshot + "\",";
            }

            if (line.contains("\"ct-common-ui\":")) {
                updatedLine = "    \"ct-common-ui\": \"" + versionWithoutSnapshot + "\",";
            }

            if (line.contains("\"ct-product-manager-ui\":")) {
                updatedLine = "    \"ct-product-manager-ui\": \"" + versionWithoutSnapshot + "\",";
            }

            if (line.contains("dist/release/ct-common-ui.") && !line.endsWith(".css\"")) {
                updatedLine = "    \"dist/release/ct-common-ui." + versionWithoutSnapshot + ".js\",";
            }

            newLines.add(updatedLine);
        }
        Files.write(path, newLines);
        return "Updating bower.json version for artifact: " + model.getArtifactId()
                + " from: " + oldVersion
                + " to: " + versionWithoutSnapshot;
    }
}

