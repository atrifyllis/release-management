package gr.alx.release.manager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/16/2016.
 */
public enum AllowedBumpType {

    MAJOR("major"),
    MINOR("minor"),
    BUILD("build"),
    PROD("prod"),
    SNAPSHOT("snapshot");

    private final String type;

    AllowedBumpType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Retrieves a list of all enum types as strings
     *
     * @return the list of types
     */
    public static List names() {
        return Stream.of(values())
                .map(AllowedBumpType::name)
                .collect(toList());
    }

    /**
     * Retrieves the enum type from a string.
     *
     * @param type the type as a string
     * @return the corresponding {@link AllowedBumpType}
     */
    public static AllowedBumpType fromString(String type) {
        return Arrays.stream(AllowedBumpType.values())
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    static boolean isBumpTypeValid(String type) {
        return Arrays.stream(values())
                .anyMatch(t -> t.name().equalsIgnoreCase(type));
    }
}
