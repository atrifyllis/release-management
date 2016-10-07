package gr.alx;

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
public class PomWriter implements Writer<FileRepresentation> {

    @Override
    public String writeNewVersion(Path path, String oldVersion, FileRepresentation model) {
        List<String> newLines = new ArrayList<>();
        try {
            List<String> lines = Files.lines(path).collect(toList());
            boolean updated = false;
            for (String line : lines) {
                if (line.contains("<version>") && line.contains("</version>") && !updated) {
                    line = "    <version>" + model.getVersion() + "</version>";
                    updated = true;
                }
                newLines.add(line);
            }
            Files.write(path, newLines);
        } catch (IOException e) {
            log.error("An error occurred while writing to file.", e);
        }
        return "Updating pom version for artifact: " + model.getArtifactId()
                + " from: " + oldVersion
                + " to: " + model.getVersion();
    }
}
