package gr.alx.release.bower;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.FileRepresentation;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by alx on 10/9/2016.
 */
public class BowerWriterTest {
    private static final String TEST_BOWER_JSON = "testBower.json";
    private BowerWriter cut;
    private BowerReader pr;

    @Before
    public void setUp() {
        cut = new BowerWriter();
        pr = new BowerReader(new ObjectMapper());
    }

    @Test
    public void shouldWriteNewVersionToFile() throws IOException {
        FileRepresentation model = new BowerFileRepresentation("ct-common-ui", "0.0.2");

        cut.writeNewVersion(Paths.get(TEST_BOWER_JSON), "0.0.1", model);

        BowerFileRepresentation testModel = pr.readFile(Paths.get(TEST_BOWER_JSON));
        assertThat(testModel.getVersion()).isEqualTo("0.0.2");
        assertThat(testModel.getDependencies().getCtCommonUi()).isEqualTo("0.0.2");
        assertThat(testModel.getDependencies().getCtProductManagerUi()).isEqualTo("0.0.2");
    }
}