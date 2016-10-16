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

    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) throws IOException {
        List<String> newLines = new ArrayList<>();
        List<String> lines = Files.lines(path).collect(toList());
        boolean updated = false;
        for (String line : lines) {
            String updatedLine = line;
            if (!updated && line.contains("<version>") && line.contains("</version>")) {
                updatedLine = getVersionLineLeadingSpaces(line) + "<version>" + model.getVersion() + "</version>";
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
        int leadingSpacesIndex = line.indexOf("<version>");
        return line.substring(0, leadingSpacesIndex);
    }
}