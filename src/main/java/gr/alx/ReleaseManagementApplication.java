package gr.alx;

import gr.alx.release.manager.ConsoleReleaseManager;
import lombok.extern.slf4j.Slf4j;


/**
 * Entry point of Application.
 */
@Slf4j
class ReleaseManagementApplication {

    /**
     * Main method that loads the spring boot application.
     *
     * @param args user run arguments
     */
    public static void main(String[] args) {

        ConsoleReleaseManager rm = new ConsoleReleaseManager();
        try {
            rm.run(args);
        } catch (Exception e) {
            log.error("An error occurred while starting the application.", e);
        }
    }
}
