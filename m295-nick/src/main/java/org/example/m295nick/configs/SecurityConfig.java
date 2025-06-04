package org.example.m295nick.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("adminPass"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("userPass"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Bei reiner REST‐API in der Regel deaktiviert
                .authorizeHttpRequests(auth -> auth
                        // Swagger & OpenAPI öffentlich:
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // VEHICLE:
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/**").hasAnyRole("ADMIN","USER")
                        // RENTAL:
                        .requestMatchers(HttpMethod.POST, "/api/v1/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/rentals/**").hasAnyRole("ADMIN","USER")
                        // Alle anderen Anfragen erfordern Authentifizierung
                        .anyRequest().authenticated()
                )
                .httpBasic(); // HTTP‐Basic Authentifizierung
        return http.build();
    }
}
