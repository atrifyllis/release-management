package gr.alx.release.types.pom;

import gr.alx.release.types.FileRepresentation;
import org.apache.maven.model.Model;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public class MavenFileRepresentation implements FileRepresentation {

    private String version;
    private final String artifactId;

    /**
     * Constructs an instance of the class using the deserialized model version and artifact id.
     *
     * @param model the deserialized artifact if
     */
    public MavenFileRepresentation(Model model) {
        this.version = getVersionFromModel(model);
        this.artifactId = model.getArtifactId();
    }

    private String getVersionFromModel(Model model) {
        String calculatedVersion;
        String modelVersion = model.getVersion();
        if (modelVersion != null) {
            calculatedVersion = modelVersion;
        } else if (model.getParent() != null) {
            calculatedVersion = model.getParent().getVersion();
        } else {
            calculatedVersion = "no version found in child or parent";
        }
        return calculatedVersion;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }
}
