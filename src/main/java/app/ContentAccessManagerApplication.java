package app;

import app.resource.ResourceAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ContentAccessManagerApplication implements CommandLineRunner {

    @Autowired
    ResourceAccessService accessService;

    public static void main(String[] args) {
        SpringApplication.run(ContentAccessManagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        accessService.registerNewResource("dummy1", Path.of("src/main/resources/static/dummy.pdf"));
        accessService.registerNewResource("dummy2", Path.of("src/main/resources/static/pdf-sample.pdf"));
    }
}
