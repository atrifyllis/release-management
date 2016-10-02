package gr.alx;

/**
 * Created by alx on 10/2/2016.
 */
public class Version {

    Integer major;
    Integer minor;
    Integer build;
    boolean isSnapshot;

    public Version(Integer major, Integer minor, Integer build, boolean isSnapshot) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.isSnapshot = isSnapshot;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getBuild() {
        return build;
    }

    public void setBuild(Integer build) {
        this.build = build;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }

    public void setSnapshot(boolean snapshot) {
        isSnapshot = snapshot;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(major).append(".");
        sb.append(minor).append(".");
        sb.append(build);
        if (isSnapshot) {
            sb.append("-SNAPSHOT");
        }
        return sb.toString();
    }
}
