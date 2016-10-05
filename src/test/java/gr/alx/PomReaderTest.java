package gr.alx;

import org.apache.maven.model.Model;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
public class PomReaderTest {

    private PomReader pr;

    @Before
    public void setUp() {
        pr = new PomReader();
    }

    @Test
    public void shoudGetAllPomPaths() throws Exception {

        List<Path> files = pr.getAllPomPaths();

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
        Model model = pr.readPomFile(pr.getAllPomPaths().get(0));

        assertThat(model).isNotNull();
        assertThat(model.getVersion()).isNotNull();
    }
}