package gr.alx.release.manager;

import java.util.Arrays;

/**
 * Created by alx on 10/16/2016.
 */
public enum AllowedActions {
    RELEASE("release"),
    BUMP("bump");

    private final String action;

    AllowedActions(String action) {
        this.action = action;
    }

    static boolean isInvalidAction(String action) {
        return Arrays.stream(values())
                .noneMatch(a -> a.name().equalsIgnoreCase(action));
    }
}
