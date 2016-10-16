package gr.alx.release.manager;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains helper methods for working with versions.
 * <p>
 * Created by alx on 10/16/2016.
 */
public class VersionHelper {

    private static final int SHORT_VERSION_SIZE = 3;
    private static final int LONG_VERSION_SIZE = 4;

    /**
     * Split version in dots and dash (if found).
     *
     * @param version the version in string format
     * @return the version splitted in parts
     */
    Version splitVersion(String version) {
        List<String> versionParts = Arrays.asList(version.split("\\.|-"));
        if (isSplitVersionSizeValid(versionParts)) {
            return new Version(
                    Integer.valueOf(versionParts.get(0)),
                    Integer.valueOf(versionParts.get(1)),
                    Integer.valueOf(versionParts.get(2)),
                    versionParts.size() == LONG_VERSION_SIZE
            );
        }
        throw new IllegalArgumentException("Version is not valid: " + version);
    }

    /**
     * Checks if a version is valid using a regular expression.
     *
     * @param version the version string
     * @return true if the version is of the form 'ddd.ddd.ddd[-SNAPSHOT]'
     */
    boolean isVersionValid(String version) {
        String validVersionRegEx = "\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?(-SNAPSHOT)?";
        return version.matches(validVersionRegEx);
    }

    /**
     * Bumps version by one if the type is major,minor or build.
     * Removes snapshot from version if type is prod.
     * Adds snapshot in version if type is snapshot.
     *
     * @param version the old version
     * @param type    the bump type
     * @return the new bumped version
     */
    String bumpUpVersion(Version version, String type) {
        switch (AllowedBumpType.fromString(type)) {
            case MAJOR:
                version.setMajor(version.getMajor() + 1);
                break;
            case MINOR:
                version.setMinor(version.getMinor() + 1);
                break;
            case BUILD:
                version.setBuild(version.getBuild() + 1);
                break;
            case PROD:
                version.setSnapshot(false);
                break;
            case SNAPSHOT:
                version.setSnapshot(true);
                break;
            default:
                break;
        }
        return version.toString();
    }

    private boolean isSplitVersionSizeValid(List<String> versionParts) {
        return versionParts.size() == SHORT_VERSION_SIZE
                || versionParts.size() == LONG_VERSION_SIZE;
    }
}
