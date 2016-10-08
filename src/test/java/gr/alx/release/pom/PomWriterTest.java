package gr.alx.release.pom;

import gr.alx.release.FileRepresentation;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
public class PomWriterTest {

    private static final String TEST_POM_XML = "testPom.xml";
    private PomWriter cut;
    private PomReader pr;

    @Before
    public void setUp() {
        cut = new PomWriter();
        pr = new PomReader();
    }

    @Test
    public void shouldWriteNewVersionToFile() throws IOException, XmlPullParserException {
        FileRepresentation model = new MavenFileRepresentation(new Model());
        model.setVersion("0.0.2-SNAPSHOT");

        cut.writeNewVersion(Paths.get(TEST_POM_XML), "0.0.1-SNAPSHOT", model);

        FileRepresentation testModel = pr.readFile(Paths.get(TEST_POM_XML));
        assertThat(testModel.getVersion()).isEqualTo("0.0.2-SNAPSHOT");
    }

}