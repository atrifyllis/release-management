package gr.alx.release.packagejson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.alx.release.FileRepresentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by alx on 10/7/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageFileRepresentation implements FileRepresentation {

    private String name;
    private String version;

    @Override
    public String getArtifactId() {
        return name;
    }
}
