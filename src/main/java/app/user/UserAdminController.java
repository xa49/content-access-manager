package app.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${endpoints.useradmin}")
@AllArgsConstructor
public class UserAdminController {

    private final UserAdminService adminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestHeader("${admin.new-user.username-header}") String username,
                        @RequestHeader("${admin.new-user.password-header}") String  password) {
        adminService.addUser(username, password);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("username") String  username) {
        adminService.deleteUser(username);
    }
}
