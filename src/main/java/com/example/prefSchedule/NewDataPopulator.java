package com.example.prefSchedule;

import com.example.prefSchedule.entity.*;
import com.example.prefSchedule.repository.UserRepository;
import com.example.prefSchedule.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NewDataPopulator {

    @Autowired
    private CourseService courseService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private PackService packService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository; // Folosește repository direct

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void populateDatabase() {
        System.out.println("=== POPULATING DATABASE WITH TEST DATA FOR SWAGGER ===");

        createTestUsers();
        createTestData();

        System.out.println("=== DATABASE READY FOR SWAGGER TESTING ===");
        System.out.println("Available test users:");
        System.out.println("- Student: username='student1', password='password123'");
        System.out.println("- Instructor: username='instructor1', password='password123'");
        System.out.println("- Admin: username='admin1', password='password123'");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
    }

    private void createTestUsers() {
        try {
            // Clean existing test users
            userRepository.deleteByUsername("student1");
            userRepository.deleteByUsername("instructor1");
            userRepository.deleteByUsername("admin1");
        } catch (Exception e) {
            // Ignoră dacă nu există
        }

        try {
            // Create test student
            Student student = new Student("S1", "Test Student", "student1@university.com", 2,
                    "student1", passwordEncoder.encode("password123"));
            student.setRoles(List.of(Role.STUDENT));
            userRepository.save(student);

            // Create test instructor
            Instructor instructor = new Instructor("instructor1", "Test Instructor",
                    "instructor1@university.com", passwordEncoder.encode("password123"));
            instructor.setRoles(List.of(Role.INSTRUCTOR));
            userRepository.save(instructor);

            // Create test admin
            Administrator admin = new Administrator("admin1", "Test Admin",
                    "admin1@university.com", passwordEncoder.encode("password123"));
            admin.setRoles(List.of(Role.ADMIN));
            userRepository.save(admin);

            System.out.println("✓ Test users created successfully");
        } catch (Exception e) {
            System.out.println("⚠ Users might already exist: " + e.getMessage());
        }
    }

    private void createTestData() {
        // Get the instructor we just created
        Instructor instructor = (Instructor) userRepository.findByUsername("instructor1")
                .orElseGet(() -> {
                    // Fallback: create a basic instructor
                    Instructor instr = new Instructor();
                    instr.setName("Default Instructor");
                    instr.setEmail("default@university.com");
                    instr.setUsername("defaultinstr");
                    instr.setPassword(passwordEncoder.encode("temp123"));
                    instr.setRoles(List.of(Role.INSTRUCTOR));
                    return instructorService.save(instr);
                });

        // Create sample packs
        List<Pack> packs = new ArrayList<>();

        Pack pack1 = new Pack();
        pack1.setName("Baze de Date");
        pack1.setYear(2);
        pack1.setSemester(1);
        packs.add(packService.save(pack1));

        Pack pack2 = new Pack();
        pack2.setName("Tehnologii Web");
        pack2.setYear(2);
        pack2.setSemester(2);
        packs.add(packService.save(pack2));

        // Create sample courses
        Course course1 = new Course();
        course1.setName("Programare Java Avansata");
        course1.setCode("C2");
        course1.setAbbreviation("PJA");
        course1.setDescription("Curs avansat de programare Java");
        course1.setType(Course.CourseType.OPTIONAL);
        course1.setGroupCount(2);
        course1.setInstructor(instructor);
        course1.setPack(packs.get(0));
        courseService.save(course1);

        Course course2 = new Course();
        course2.setName("Baze de Date Relationale");
        course2.setCode("C1");
        course2.setAbbreviation("BDR");
        course2.setDescription("Fundamentele bazelor de date relationale");
        course2.setType(Course.CourseType.COMPULSORY);
        course2.setGroupCount(1);
        course2.setInstructor(instructor);
        course2.setPack(packs.get(0));
        courseService.save(course2);

        // Create sample courses in Pack "Tehnologii Web"
        Course course3 = new Course();
        course3.setName("Frontend Development");
        course3.setCode("C3");
        course3.setAbbreviation("FD");
        course3.setDescription("Curs despre dezvoltarea frontend");
        course3.setType(Course.CourseType.OPTIONAL);
        course3.setGroupCount(2);
        course3.setInstructor(instructor);
        course3.setPack(pack2);  // <-- aici legăm cursul de pack2
        courseService.save(course3);

        Course course4 = new Course();
        course4.setName("Backend Development");
        course4.setCode("C4");
        course4.setAbbreviation("BD");
        course4.setDescription("Curs despre dezvoltarea backend");
        course4.setType(Course.CourseType.COMPULSORY);
        course4.setGroupCount(1);
        course4.setInstructor(instructor);
        course4.setPack(pack2);  // <-- aici legăm cursul de pack2
        courseService.save(course4);


        System.out.println("✓ Created sample courses for testing");
    }
}