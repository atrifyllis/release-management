package gr.alx.release.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by alx on 10/2/2016.
 */
@Getter
@Setter
@AllArgsConstructor
public class Version {

    private Integer major;
    private Integer minor;
    private Integer build;
    private boolean isSnapshot;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(major).append('.')
        .append(minor).append('.')
        .append(build);
        if (isSnapshot) {
            sb.append("-SNAPSHOT");
        }
        return sb.toString();
    }
}
