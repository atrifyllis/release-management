package gr.alx;

import org.apache.maven.model.Model;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by alx on 10/2/2016.
 */
public class ReleaseManagerTest {

    private ReleaseManager rm;

    @Before
    public void setUp() {
        rm = new ReleaseManager();
    }

    @Test
    public void shoudgetAllPomPaths() throws Exception {

        List<Path> files = rm.getAllPomPaths();

        List<String> fileStrings = files.stream()
                .map(f -> {
                    try {
                        return Files.lines(f).collect(Collectors.joining());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .collect(toList());

        fileStrings.forEach(System.out::println);
        assertThat(fileStrings.size()).isEqualTo(1);
    }

    @Test
    public void shouldReadPomFile() throws IOException {
        Model model = rm.readPomFile(rm.getAllPomPaths().get(0));

        assertThat(model).isNotNull();
        assertThat(model.getVersion()).isNotNull();
    }

    @Test
    public void shouldBumpUpBuildSnapshotVersion() throws IOException {
        String newVersion = rm.bumpUpVersion("0.0.1-SNAPSHOT", "build");

        assertThat(newVersion).isEqualTo("0.0.2-SNAPSHOT");
    }

    @Test
    public void shouldBumpUpMajorSnapshotVersion() throws IOException {
        String newVersion = rm.bumpUpVersion("0.0.1-SNAPSHOT", "major");

        assertThat(newVersion).isEqualTo("1.0.1-SNAPSHOT");
    }

    @Test
    public void shouldBumpUpMinorSnapshotVersion() throws IOException {
        String newVersion = rm.bumpUpVersion("0.0.1-SNAPSHOT", "minor");

        assertThat(newVersion).isEqualTo("0.1.1-SNAPSHOT");
    }

    @Test
    public void shouldBumpUpBuildVersion() throws IOException {
        String newVersion = rm.bumpUpVersion("0.0.1", "build");

        assertThat(newVersion).isEqualTo("0.0.2");
    }

    @Test
    public void shouldWriteNewVersionToFile() throws IOException {
        Model model = rm.readPomFile(rm.getAllPomPaths().get(0));
        model.setVersion("0.0.2-SNAPSHOT");

        rm.writeNewVersion(Paths.get("testPom.xml"), model);

        Model testModel = rm.readPomFile(Paths.get("testPom.xml"));
        assertThat(testModel.getVersion()).isEqualTo("0.0.2-SNAPSHOT");
    }

}