package gr.alx.release.types.packagejson;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.configuration.Configurator;
import gr.alx.release.types.FileRepresentation;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by TRIFYLLA on 5/10/2016.
 */
public class PackageWriterTest {

    private static final String TEST_PACKAGE_JSON = "testPackage.json";
    private PackageWriter cut;
    private PackageReader pr;

    @Before
    public void setUp() {
        cut = new PackageWriter(new Configurator().getConfiguration("configuration.yml"));
        pr = new PackageReader(new ObjectMapper());
    }

    @Test
    public void shouldWriteNewVersionToFile() throws IOException {
        FileRepresentation model = new PackageFileRepresentation("ct-common-ui", "0.0.2");

        cut.writeNewVersion(Paths.get(TEST_PACKAGE_JSON), "0.0.1", model);

        FileRepresentation testModel = pr.readFile(Paths.get(TEST_PACKAGE_JSON));
        assertThat(testModel.getVersion()).isEqualTo("0.0.2");
    }

}