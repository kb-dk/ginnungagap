package dk.kb.ginnungagap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * Spring boot application instantiator.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * Main method.
     * @param args The arguments for the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
