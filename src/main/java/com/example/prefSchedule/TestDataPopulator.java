package com.example.prefSchedule;

import com.example.prefSchedule.entity.*;
import com.example.prefSchedule.repository.*;
import com.example.prefSchedule.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TestDataPopulator {

    @Autowired private CourseService courseService;
    @Autowired private InstructorService instructorService;
    @Autowired private PackService packService;
    @Autowired private StudentService studentService;
    @Autowired private AdministratorRepository adminRepository;
    @Autowired private StudentGradeRepository gradeRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public void generateFullMatchingScenario() {
        // 1. Creare Admin
        Administrator admin = new Administrator(
                "admin", "System Admin", "admin@univ.ro",
                passwordEncoder.encode("admin123")
        );
        adminRepository.save(admin);

        // 2. Creare Instructori (câte unul responsabil de fiecare an, pentru testare)
        List<Instructor> instructors = new ArrayList<>();
        String[] profNames = {"Alexandru Matei", "Simona Enea", "Gabriel Lupu"};

        for (int i = 0; i < 3; i++) {
            Instructor instr = new Instructor(
                    "prof_" + (i+1),
                    profNames[i],
                    "prof" + (i+1) + "@univ.ro",
                    passwordEncoder.encode("pass123")
            );
            instr.setRoles(List.of(Role.INSTRUCTOR));
            instructors.add(instructorService.save(instr));
        }

        // 3. Iterăm prin anii de studiu 1, 2, 3 pentru Cursuri și Studenți
        for (int year = 1; year <= 3; year++) {
            // Un singur pachet pentru toate opționalele din anul respectiv
            Pack optPack = packService.save(new Pack(year, 1, "Pachet Optionale Anul " + year));

            // Instructorul alocat acestui an (ciclic)
            Instructor yearInstructor = instructors.get(year - 1);

            // 4 Cursuri OBLIGATORII per an (Pack = null)
            for (int j = 1; j <= 4; j++) {
                Course comp = new Course();
                comp.setType(Course.CourseType.COMPULSORY);
                comp.setCode("COMP-" + year + "-" + j);
                comp.setName("Obligatorie " + j + " An " + year);
                comp.setAbbreviation("OBL-" + year + "-" + j);
                comp.setInstructor(yearInstructor);
                comp.setPack(null); // Obligatoriile nu au pachet conform cerinței
                courseService.save(comp);
            }

            // 4 Cursuri OPȚIONALE per an (Asociate pachetului anului)
            for (int j = 1; j <= 4; j++) {
                Course opt = new Course();
                opt.setType(Course.CourseType.OPTIONAL);
                opt.setCode("OPT-" + year + "-" + j);
                opt.setName("Optionala " + j + " An " + year);
                opt.setAbbreviation("OPT-" + year + "-" + j);
                opt.setInstructor(yearInstructor);
                opt.setPack(optPack);
                opt.setGroupCount(2); // Capacitate mai mare pentru testare
                courseService.save(opt);
            }

            // 4. Creare 5 Studenți reali per an (Total 15 studenți)
            for (int sIdx = 1; sIdx <= 5; sIdx++) {
                String studentCode = "S" + year + "0" + sIdx;
                Student s = new Student(
                        studentCode,
                        "Student " + sIdx + " An " + year,
                        "s" + year + "_" + sIdx + "@stud.ro",
                        year,
                        "user_" + studentCode.toLowerCase(),
                        passwordEncoder.encode("pass123")
                );
                s.setRoles(List.of(Role.STUDENT));
                studentService.save(s);

                // Generăm note automate la cursurile obligatorii ale anului curent pentru ranking
                for (int j = 1; j <= 4; j++) {
                    double randomGrade = 5.0 + (Math.random() * 5.0); // Note între 5 și 10
                    gradeRepository.save(new StudentGrade(studentCode, "COMP-" + year + "-" + j, Math.round(randomGrade * 100.0) / 100.0));
                }
            }
        }

        System.out.println("✓ Generare completă!");
        System.out.println("- 1 Admin");
        System.out.println("- 3 Instructori");
        System.out.println("- 15 Studenți cu note la cursurile obligatorii");
        System.out.println("- 12 Cursuri Obligatorii (fără pachet)");
        System.out.println("- 12 Cursuri Opționale (grupate în 3 pachete)");
    }
}