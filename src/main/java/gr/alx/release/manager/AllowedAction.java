package gr.alx.release.manager;

import java.util.Arrays;

/**
 * Created by alx on 10/16/2016.
 */
public enum AllowedAction {
    RELEASE("release"),
    BUMP("bump");

    private final String action;

    AllowedAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }


    static boolean isInvalidAction(String action) {
        return Arrays.stream(values())
                .noneMatch(a -> a.name().equalsIgnoreCase(action));
    }
}
