package gr.alx;

import org.apache.maven.model.Model;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
public class PomWriterTest {

    private PomWriter cut;
    private PomReader pr;

    @Before
    public void setUp() {
        cut = new PomWriter();
        pr = new PomReader();
    }

    @Test
    public void shouldWriteNewVersionToFile() throws IOException {
        Model model = new Model();
        model.setVersion("0.0.2-SNAPSHOT");

        cut.writeNewVersion(Paths.get("testPom.xml"), "0.0.1-SNAPSHOT", model);

        Model testModel = pr.readPomFile(Paths.get("testPom.xml"));
        assertThat(testModel.getVersion()).isEqualTo("0.0.2-SNAPSHOT");
    }

}