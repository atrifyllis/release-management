package gr.alx;

import gr.alx.release.ReleaseManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of Spring Boot Application.
 */
@SpringBootApplication
@Slf4j
public class ReleaseManagementApplication {

    /**
     * Main method that loads the spring boot application.
     *
     * @param args user run arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ReleaseManagementApplication.class, args);

        ReleaseManager rm = new ReleaseManager();
        try {
            rm.run(args);
        } catch (Exception e) {
            log.error("An error occurred while starting the application.", e);
        }
    }
}
