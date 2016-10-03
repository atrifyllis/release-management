package gr.alx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReleaseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReleaseManagementApplication.class, args);

        ReleaseManager rm = new ReleaseManager();
        try {
            rm.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
