package gr.alx.release.bower;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by alx on 10/9/2016.
 */
public class BowerReaderTest {

    private BowerReader cut;

    @Before
    public void setUp() {
        cut = new BowerReader(new ObjectMapper());
    }

    @Test
    public void shouldGetAllPaths() throws IOException {
        List<Path> packagePaths = cut.getAllPaths();

        assertThat(packagePaths.size()).isEqualTo(3);
    }

    @Test
    public void shouldReadPackageFile() throws IOException {

        BowerFileRepresentation packageJson = cut.readFile(cut.getAllPaths().get(0));

        assertThat(packageJson.getVersion()).isEqualTo("0.0.1");
    }
}
