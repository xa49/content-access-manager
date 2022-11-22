package app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${endpoints.useradmin}")
    private String userAdminEndpoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, userAdminEndpoint).hasAuthority("ADD_USER")
                .mvcMatchers(HttpMethod.DELETE, userAdminEndpoint + "/**").hasAuthority("ADD_USER")
                .mvcMatchers(HttpMethod.GET, "/resource").hasAuthority("GET_CODES")
                .mvcMatchers(HttpMethod.GET, "/resource/**").permitAll()
                .anyRequest().denyAll()
                .and().csrf().disable() // !!!
                .build();
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("alice")
                        .password(passwordEncoder().encode("alice"))
                        .authorities("GET_CODES")
                        .build(),
                User.withUsername("admin")
                        .password(passwordEncoder().encode("admin"))
                        .authorities("ADD_USER")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
