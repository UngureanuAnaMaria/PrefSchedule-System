// AuthController.java
package com.example.prefSchedule.controller;

import com.example.prefSchedule.service.JwtService;
import com.example.prefSchedule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", userDetails.getUsername());
        response.put("message", "Login successful");

        // EXTRAGE ROLURILE: IMPORTANT pentru header.html
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        response.put("roles", roles);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegistrationRequest request) {
        try {
            var student = userService.registerStudent(
                    request.getUsername(),
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getCode(),
                    request.getYear()
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Student registered successfully");
            response.put("username", student.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/instructor")
    public ResponseEntity<?> registerInstructor(@RequestBody UserRegistrationRequest request) {
        try {
            var instructor = userService.registerInstructor(
                    request.getUsername(),
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Instructor registered successfully");
            response.put("username", instructor.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserRegistrationRequest request) {
        try {
            var admin = userService.registerAdministrator(
                    request.getUsername(),
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Administrator registered successfully");
            response.put("username", admin.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // În AuthController.java
    @PostMapping("/import/students")
    public ResponseEntity<?> uploadStudentCsv(@RequestParam("file") MultipartFile file) {
        try {
            userService.importStudentsFromCsv(file);
            return ResponseEntity.ok(Map.of("message", "Studenții au fost importați cu succes!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import/instructors")
    public ResponseEntity<?> uploadInstructorCsv(@RequestParam("file") MultipartFile file) {
        try {
            userService.importInstructorsFromCsv(file);
            return ResponseEntity.ok(Map.of("message", "Instructorii au fost importați cu succes!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class StudentRegistrationRequest {
        private String username;
        private String name;
        private String email;
        private String password;
        private String code;
        private int year;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
    }

    public static class UserRegistrationRequest {
        private String username;
        private String name;
        private String email;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}