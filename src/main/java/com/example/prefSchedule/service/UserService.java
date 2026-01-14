package com.example.prefSchedule.service;

import com.example.prefSchedule.entity.*;
import com.example.prefSchedule.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@Lazy pentru a întrerupe circularitatea
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Convert roles to GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    public Student registerStudent(String username, String name, String email, String password, String code, int year) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        Student student = new Student(code, name, email, year, username, passwordEncoder.encode(password));
        return userRepository.save(student);
    }

    public Instructor registerInstructor(String username, String name, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        Instructor instructor = new Instructor(username, name, email, passwordEncoder.encode(password));
        return userRepository.save(instructor);
    }

    public Administrator registerAdministrator(String username, String name, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        Administrator admin = new Administrator(username, name, email, passwordEncoder.encode(password));
        return userRepository.save(admin);
    }

    public void importStudentsFromCsv(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // --- LOGICĂ PENTRU ELIMINARE BOM ---
            fileReader.mark(1);
            if (fileReader.read() != 0xFEFF) {
                fileReader.reset();
            }

            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            for (CSVRecord csvRecord : csvParser) {
                // Se folosesc numele coloanelor exact ca în header-ul CSV
                this.registerStudent(
                        csvRecord.get("username"),
                        csvRecord.get("name"),
                        csvRecord.get("email"),
                        csvRecord.get("password"),
                        csvRecord.get("code"),
                        Integer.parseInt(csvRecord.get("year"))
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la procesarea fișierului CSV pentru studenți: " + e.getMessage());
        }
    }

    public void importInstructorsFromCsv(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            fileReader.mark(1);
            if (fileReader.read() != 0xFEFF) {
                fileReader.reset();
            }

            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            for (CSVRecord csvRecord : csvParser) {
                this.registerInstructor(
                        csvRecord.get("username"),
                        csvRecord.get("name"),
                        csvRecord.get("email"),
                        csvRecord.get("password")
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la procesarea fișierului CSV pentru instructori: " + e.getMessage());
        }
    }

    // Restul metodelor rămân la fel...
    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByUsernameContaining(String username) {
        return userRepository.findByUsernameContaining(username);
    }

    public List<User> findByEmailContaining(String email) {
        return userRepository.findByEmailContaining(email);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public int updateUsername(Long id, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Username already exists: " + newUsername);
        }
        return userRepository.updateUsername(id, newUsername);
    }

    public int updateEmail(Long id, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already exists: " + newEmail);
        }
        return userRepository.updateEmail(id, newEmail);
    }

    public int updatePassword(Long id, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        return userRepository.updatePassword(id, encodedPassword);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public int deleteByUsername(String username) {
        return userRepository.deleteByUsername(username);
    }

    public int deleteByEmail(String email) {
        return userRepository.deleteByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}