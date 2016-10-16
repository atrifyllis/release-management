package gr.alx.release.configuration;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Created by alx on 10/15/2016.
 */
public class ConfiguratorTest {

    @Test
    public void shouldReadConfiguration(){
        Configurator configurator = new Configurator();
        Configuration configuration = configurator.getConfiguration("configuration.yml");

        assertThat(configuration).isNotNull();
        assertThat(configuration.getExcludes().getFolders()).contains("target","node_modules","bower_components","automation");
    }
}