package gr.alx.release.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by alx on 10/15/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {

    private ExcludedElement excludes;
    private IncludedElements includes;
}
