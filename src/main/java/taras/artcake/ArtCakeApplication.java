package taras.artcake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ArtCakeApplication {

    public static void main(String[] args) {
        // Load .env file before starting Spring Boot
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        } catch (Exception e) {
            System.out.println("Warning: Could not load .env file: " + e.getMessage());
        }

        SpringApplication.run(ArtCakeApplication.class, args);
    }

}
