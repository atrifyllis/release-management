package gr.alx.release;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by alx on 10/9/2016.
 */
public class JsonWriterHelperTest {

    @Test
    public void shouldStripSnapshot() {
        String newVersion = JsonWriterHelper.stripSnapshot("10.1.1-SNAPSHOT");

        assertThat(newVersion).isEqualTo("10.1.1");
    }

    @Test
    public void shouldReturnVersionWenNoSnapshot() {
        String newVersion = JsonWriterHelper.stripSnapshot("10.1.1");

        assertThat(newVersion).isEqualTo("10.1.1");
    }
}