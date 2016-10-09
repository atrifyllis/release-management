package gr.alx.release;

/**
 * Created by alx on 10/9/2016.
 */
public class JsonWriterHelper {

    private JsonWriterHelper() {
    }

    /**
     * Removes the '-SNAPSHOT' part from the version string.
     *
     * @param version the version (possible with the snapshot part)
     * @return the version without the snapshot part
     */
    public static String stripSnapshot(String version) {
        int snapshotIndex = version.indexOf("-SNAPSHOT");
        return snapshotIndex != -1 ? version.substring(0, snapshotIndex) : version;
    }
}
