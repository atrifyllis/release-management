package gr.alx.release.pom;

import gr.alx.release.FileRepresentation;
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
        this.version = model.getVersion();
        this.artifactId = model.getArtifactId();
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
