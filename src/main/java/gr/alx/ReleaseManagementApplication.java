package gr.alx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ReleaseManagementApplication {

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
