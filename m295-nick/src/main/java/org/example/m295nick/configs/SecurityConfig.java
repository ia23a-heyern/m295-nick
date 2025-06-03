package org.example.m295nick.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Damit @PreAuthorize in den Controllern greift
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var user = User.withUsername("user")
                .password(encoder.encode("userpass"))
                .roles("USER")   // Rolle USER
                .build();

        var admin = User.withUsername("admin")
                .password(encoder.encode("adminpass"))
                .roles("ADMIN")  // Rolle ADMIN
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // a) CSRF ausschalten (REST-API)
                .csrf(csrf -> csrf.disable())
                // b) URL-Zugriffsregeln
                .authorizeHttpRequests(auth -> auth
                        // Swagger-UI & OpenAPI-Docs dürfen ohne Login geladen werden
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // Alle anderen /api/** Endpunkte erfordern Authentifizierung
                        .requestMatchers("/api/**").authenticated()
                        // Falls Du z.B. eine H2-Konsole o.Ä. benutzt, musst Du die URL hier auch ggf. erlauben:
                        // .requestMatchers("/h2-console/**").permitAll()
                        // Alle sonstigen URLs ebenfalls geschützt
                        .anyRequest().authenticated()
                )
                // c) HTTP Basic Auth aktivieren
                .httpBasic();

        return http.build();
    }
}
