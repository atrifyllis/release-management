package gr.alx.release.types.pom;

import gr.alx.release.types.FileRepresentation;
import gr.alx.release.types.Writer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
@Slf4j
public class PomWriter implements Writer {

    public static final String VERSION_START_TAG = "<version>";
    public static final String VERSION_END_TAG = "</version>";

    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> lines = Files.lines(path).collect(toList());
        boolean updated = false;
        for (String line : lines) {
            String updatedLine = line;
            if (!updated && line.contains(VERSION_START_TAG) && line.contains(VERSION_END_TAG)) {
                updatedLine = getVersionLineLeadingSpaces(line) + VERSION_START_TAG + model.getVersion() + VERSION_END_TAG;
                updated = true;
            }
            newLines.add(updatedLine);
        }
        Files.write(path, newLines);
        return "Updating pom version for artifact: " + model.getArtifactId()
                + " from: " + oldVersion
                + " to: " + model.getVersion();
    }

    private String getVersionLineLeadingSpaces(String line) {
        int leadingSpacesIndex = line.indexOf(VERSION_START_TAG);
        return line.substring(0, leadingSpacesIndex);
    }
}
