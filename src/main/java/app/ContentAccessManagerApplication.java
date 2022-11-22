package app;

import app.resource.ResourceAccessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.UUID;

@SpringBootApplication
@Slf4j
public class ContentAccessManagerApplication implements CommandLineRunner {

    @Autowired
    ResourceAccessService accessService;

    public static void main(String[] args) {
        SpringApplication.run(ContentAccessManagerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        accessService.registerNewResource("dummy1", Path.of("src/main/resources/static/dummy.pdf"));
        accessService.registerNewResource("dummy2", Path.of("src/main/resources/static/pdf-sample.pdf"));
        accessService.registerNewResource("missing-file", Path.of(UUID.randomUUID().toString()));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JsonMapper()
                .findAndRegisterModules()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
