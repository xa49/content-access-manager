package app;

import app.resource.AccessCodesDto;
import app.resource.ResourceAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;

@SpringBootApplication
@Slf4j
public class ContentAccessManagerApplication implements CommandLineRunner {

    @Autowired
    ResourceAccessService accessService;

    public static void main(String[] args) {
        SpringApplication.run(ContentAccessManagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        AccessCodesDto code1 =
                accessService.registerNewResource("dummy1", Path.of("src/main/resources/static/dummy.pdf"));
        log.info("Access code for dummy1 is {}", code1.getCurrentCode());
        AccessCodesDto code2 =
                accessService.registerNewResource("dummy2", Path.of("src/main/resources/static/pdf-sample.pdf"));
        log.info("Access code for dummy2 is {}", code2.getCurrentCode());
    }
}
