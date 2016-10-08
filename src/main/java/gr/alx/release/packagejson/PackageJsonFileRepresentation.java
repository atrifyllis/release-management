package gr.alx.release.packagejson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.alx.release.FileRepresentation;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by alx on 10/7/2016.
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageJsonFileRepresentation implements FileRepresentation {

    private String name;
    private String version;

    @Override
    public String getArtifactId() {
        return name;
    }
}
