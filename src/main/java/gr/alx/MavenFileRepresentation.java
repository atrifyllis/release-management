package gr.alx;

import org.apache.maven.model.Model;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public class MavenFileRepresentation implements FileRepresentation {

    private String version;
    private String artifactId;

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

    public String getArtifactId() {
        return artifactId;
    }
}
