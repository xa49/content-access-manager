package app.user;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserAdminControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${endpoints.useradmin}")
    String endpoint;
    @Value("${admin.new-user.username-header}")
    String newUserUserNameHeader;
    @Value("${admin.new-user.password-header}")
    String newUserPasswordHeader;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithAnonymousUser
    void anonymousUserCannotAccessEndpoint() {
        given().header(newUserUserNameHeader, "test")
                .header(newUserPasswordHeader, "test")
                .when().post(endpoint)
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @WithMockUser(authorities = "GET_CODES")
    void regularUserCannotAccessEndpoint() {
        given().header(newUserUserNameHeader, "test")
                .header(newUserPasswordHeader, "test")
                .when().post(endpoint)
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

    @Test
    @WithMockUser(authorities = "ADD_USER")
    void adminCanAddAndDeleteUser_addedToUserDetailsManager() {
        given().header(newUserUserNameHeader, "test")
                .header(newUserPasswordHeader, "test")
                .when().post(endpoint)
                .then()
                .status(HttpStatus.CREATED);

        var user = userDetailsManager.loadUserByUsername("test");
        assertTrue(passwordEncoder.matches("test", user.getPassword()));

        when().delete(endpoint + "/{username}", "test")
                .then()
                .status(HttpStatus.NO_CONTENT);
        assertFalse(userDetailsManager.userExists("test"));
    }

    @Test
    @WithMockUser(authorities = "GET_CODES")
    void nonAdminCannotDeleteUser() {
        when().delete(endpoint + "/{username}", "alice")
                .then()
                .status(HttpStatus.FORBIDDEN);
    }


}