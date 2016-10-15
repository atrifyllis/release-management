package gr.alx.release.pom;

import gr.alx.release.FileReader;
import gr.alx.release.FileRepresentation;
import gr.alx.release.configuration.Configurator;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
    private FileReader gr;

    @Before
    public void setUp() throws IOException {
        pr = new PomReader();
        gr = new FileReader(new Configurator().getConfiguration("configuration.yml"));
    }

    @Test
    public void shouldGetAllPomPaths() throws Exception {


        List<Path> files = pr.getAllPaths(gr.getAllPaths());

        assertThat(files.size()).isEqualTo(4);
    }

    @Test
    public void shouldReadPomFile() throws IOException, XmlPullParserException {
        FileRepresentation model = pr.readFile(pr.getAllPaths(gr.getAllPaths()).get(0));

        assertThat(model).isNotNull();
        assertThat(model.getVersion()).isNotNull();
    }
}