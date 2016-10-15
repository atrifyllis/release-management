package gr.alx.release.packagejson;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.FileReader;
import gr.alx.release.configuration.Configurator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by alx on 10/7/2016.
 */
public class PackageReaderTest {
    private PackageReader cut;
    private FileReader gr;

    @Before
    public void setUp() throws IOException {
        cut = new PackageReader(new ObjectMapper());
        gr = new FileReader(new Configurator().getConfiguration("configuration.yml"));
    }

    @Test
    public void shouldGetAllPaths() throws IOException {
        List<Path> packagePaths = cut.getAllPaths(gr.getAllPaths());

        assertThat(packagePaths.size()).isEqualTo(3);
    }

    @Test
    public void shouldReadPackageFile() throws IOException {

        PackageFileRepresentation packageJson = cut.readFile(cut.getAllPaths(gr.getAllPaths()).get(0));

        assertThat(packageJson.getVersion()).isEqualTo("0.0.2");
    }

}