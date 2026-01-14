package com.example.prefSchedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Folosim @Lazy pentru a întrerupe dependența circulară
    public SecurityConfig(@Lazy JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()

                        // GET endpoints - mostly public
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/students/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/instructors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/student-grades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/instructor-preferences/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stable-match/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/packs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/student-preferences/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/assignments/**").permitAll()



                                .requestMatchers("/preferences.html").permitAll()
                                .requestMatchers("/index.html").permitAll()
                                .requestMatchers("/header.html").permitAll()
                                .requestMatchers("/profile.html").permitAll()
                                .requestMatchers("/assignment.html").permitAll()
                                .requestMatchers("/assignments-view.html").permitAll()
                                .requestMatchers("/teaching-courses.html").permitAll()
                                .requestMatchers("/create-users.html").permitAll()
                                .requestMatchers("/manage-users.html").permitAll()
                                .requestMatchers("/manage-packs.html").permitAll()
                                .requestMatchers("/manage-courses.html").permitAll()


                        //trebuie modificat cu role based - acum doar pt testare
                        .requestMatchers(HttpMethod.POST, "/api/student-preferences/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/student-grades/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/instructor-preferences/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/instructor-preferences/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stable-match/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/assignments/**").permitAll()

                        // Protected endpoints - role based
//                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
//                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                                .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/**").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}