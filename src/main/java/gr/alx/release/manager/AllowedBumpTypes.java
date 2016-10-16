package gr.alx.release.manager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by alx on 10/16/2016.
 */
public enum AllowedBumpTypes {

    MAJOR("major"),
    MINOR("minor"),
    BUILD("build"),
    PROD("prod"),
    SNAPSHOT("snapshot");


    private final String type;

    AllowedBumpTypes(String type) {
        this.type = type;
    }

    public static List names() {
        return Stream.of(values())
                .map(AllowedBumpTypes::name)
                .collect(toList());
    }

    static boolean isBumpTypeValid(String type) {
        return Arrays.stream(values())
                .anyMatch(t -> t.name().equalsIgnoreCase(type));
    }
}
