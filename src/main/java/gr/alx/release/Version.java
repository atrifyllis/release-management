package gr.alx.release;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by alx on 10/2/2016.
 */
@Getter
@Setter
@AllArgsConstructor
class Version {

    Integer major;
    Integer minor;
    Integer build;
    boolean isSnapshot;

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
