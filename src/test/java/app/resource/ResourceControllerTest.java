package app.resource;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.with;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ResourceControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ResourceAccessService accessService;

    @TempDir
    File tempDir;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @DirtiesContext
    @WithMockUser(authorities = "GET_CODES")
    void getAccessCode_withAuthenticatedUser_succeeds() {
        addSampleResourceNamedHello();

        with().get("/hello")
                .then()
                .status(HttpStatus.OK)
                .body("currentCode", equalTo(accessService.getAccessCodes("hello").getCurrentCode().toString()))
                .body("nextCode", equalTo(accessService.getAccessCodes("hello").getNextCode().toString()));

    }

    @Test
    @DirtiesContext
    @WithAnonymousUser
    void getAccessCode_withUnauthenticatedUser_fails() {
        addSampleResourceNamedHello();
        with().get("/hello")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getResource_withCorrectCode() {
        addSampleResourceNamedHello();
        UUID currentCode = accessService.getAccessCodes("hello").getCurrentCode();

        byte[] response = with().get("/hello/{accessCode}", currentCode.toString())
                .then()
                .status(HttpStatus.OK)
                .extract().asByteArray();

        assertEquals("helloFile", new String(response, StandardCharsets.UTF_8));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getResource_withIncorrectCode() {
        addSampleResourceNamedHello();
        UUID currentCode = accessService.getAccessCodes("hello").getCurrentCode();

        UUID wrongCode = UUID.randomUUID();
        while (wrongCode.equals(currentCode)) {
            wrongCode = UUID.randomUUID();
        }

        with().get("/hello/{accessCode}", wrongCode.toString())
                .then()
                .status(HttpStatus.FORBIDDEN)
                .body("detail", equalTo("Wrong access code " + wrongCode
                        + " for resource: hello"));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getResource_withCorrectCode_fileNotPresent() {
        UUID currentCode =
                accessService.registerNewResource("hello", Path.of(UUID.randomUUID().toString()))
                        .getCurrentCode();

        with().get("/hello/{accessCode}", currentCode.toString())
                .then()
                .status(HttpStatus.NOT_FOUND)
                .body("detail", equalTo("Resource file not found on server for: hello"));
    }

    @Test
    @WithMockUser
    void getResource_nonExistingResource() {

        with().get("/hello/{accessCode}", UUID.randomUUID().toString())
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .body("detail", equalTo("No resource with identifier: hello"));
    }

    private void addSampleResourceNamedHello() {
        File validFile = new File(tempDir, UUID.randomUUID().toString());
        try (PrintWriter writer = new PrintWriter(validFile)) {
            writer.print("helloFile");
        } catch (Exception e) {
            e.printStackTrace();
        }
        accessService.registerNewResource("hello", validFile.toPath());
    }


}