package gr.alx.release.bower;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.alx.release.packagejson.PackageFileRepresentation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 *
 *
 * NOTE: not using @Data annotation so that we can speify callSuper=true.
 * This way equals/hasCode call super methods
 * <p>
 * Created by alx on 10/7/2016.
 */

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BowerFileRepresentation extends PackageFileRepresentation {

    private Dependencies dependencies;
    private List<String> main;

    /**
     * Constructor calls the parent's constructor
     * @param name the name of the artifact
     * @param version the version of the artifact
     */
    public BowerFileRepresentation(String name, String version) {
        super(name, version);
    }
}
