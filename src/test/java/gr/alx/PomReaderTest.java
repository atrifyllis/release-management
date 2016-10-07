package gr.alx;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
    public void shouldGetAllPomPaths() throws Exception {

        List<Path> files = pr.getAllPaths();

        assertThat(files.size()).isEqualTo(4);
    }

    @Test
    public void shouldReadPomFile() throws IOException {
        FileRepresentation model = pr.readFile(pr.getAllPaths().get(0));

        assertThat(model).isNotNull();
        assertThat(model.getVersion()).isNotNull();
    }
}