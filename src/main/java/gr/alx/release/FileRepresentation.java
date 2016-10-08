package gr.alx.release;

/**
 * This interface represents the different type of files handled by the application.
 * <p>
 * Created by TRIFYLLA on 7/10/2016.
 */
public interface FileRepresentation {

    /**
     * Retrieves the version of the file.
     *
     * @return the version of the file
     */
    String getVersion();

    /**
     * Sets a new version for the file.
     *
     * @param version the new version for the file
     */
    void setVersion(String version);

    /**
     * Retrieves the project name contained inside the file.
     *
     * @return the project name
     */
    String getArtifactId();
}

