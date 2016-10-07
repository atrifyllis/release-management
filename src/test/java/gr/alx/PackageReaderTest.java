package gr.alx;

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
    PackageReader cut;

    @Before
    public void setUp() {
        cut = new PackageReader();
    }

    @Test
    public void shouldGetAllPaths() throws IOException {
        List<Path> packagePaths = cut.getAllPackagePaths();

        assertThat(packagePaths.size()).isEqualTo(2);
    }

    @Test
    public void shouldReadPackageFile() throws IOException {

        cut.readPackageFile(cut.getAllPackagePaths().get(0));
    }

}