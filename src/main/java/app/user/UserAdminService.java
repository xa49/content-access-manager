package app.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAdminService {

    private static final String DEFAULT_AUTHORITY = "GET_CODES";
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;


    public void addUser(String  username, String  password) {
        userDetailsManager.createUser(User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .authorities(DEFAULT_AUTHORITY)
                .build());
    }

    public void deleteUser(String username) {
        userDetailsManager.deleteUser(username);
    }
}
