package com.example.prefSchedule;
import com.example.prefSchedule.dto.CourseSearchCriteriaDTO;
import com.example.prefSchedule.dto.StudentPreferenceRequestDTO;
import com.example.prefSchedule.dto.StudentPreferenceResponseDTO;
import com.example.prefSchedule.entity.*;
import com.example.prefSchedule.service.*;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Transactional
public class DataPopulator {

    @Autowired
    private CourseService courseService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private PackService packService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentPreferenceService studentPreferenceService;

    private final Faker faker;
    private final Random random;

    public DataPopulator() {
        this.faker = new Faker();
        this.random = new Random();
    }

    public void populateDatabase() {
        System.out.println("=== POPULATING DATABASE WITH FAKE DATA ===");

        cleanDatabase();

        List<Student> students = createStudents(50);
        System.out.println("Created " + students.size() + " students");

        List<Instructor> instructors = createInstructors(10);
        System.out.println("Created " + instructors.size() + " instructors");

        List<Pack> packs = createPacks();
        System.out.println("Created " + packs.size() + " packs");

        List<Course> courses = createCourses(20, instructors, packs);
        System.out.println("Created " + courses.size() + " courses");

        testCourseCrudOperations();
    }

    private void cleanDatabase() {
        studentPreferenceService.findAll().forEach(studentPreferenceResponseDTO -> {
            try {
                studentPreferenceService.deleteById(studentPreferenceResponseDTO.getId());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        courseService.findAll().forEach(course -> courseService.deleteById(course.getId()));
        packService.findAll().forEach(pack -> packService.deleteById(pack.getId()));
        instructorService.findAll().forEach(instructor -> instructorService.deleteById(instructor.getId()));
        studentService.findAll().forEach(student -> studentService.deleteById(student.getId()));
    }

    private List<Instructor> createInstructors(int count) {
        List<Instructor> instructors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Instructor instructor = new Instructor();
            instructor.setName(faker.name().fullName());
            instructor.setEmail(faker.internet().emailAddress());
            instructors.add(instructorService.save(instructor));
        }
        return instructors;
    }

    private List<Pack> createPacks() {
        List<Pack> packs = new ArrayList<>();

        packs.add(createPack("Baze de Date", 2, 1));
        packs.add(createPack("Tehnologii Web", 2, 2));
        packs.add(createPack("Programare Avansata", 2, 2));

        packs.add(createPack("Inteligenta Artificiala", 3, 1));
        packs.add(createPack("Securitatea Informatiei", 3, 1));
        packs.add(createPack("Cloud Computing", 3, 2));

        return packs;
    }

    private Pack createPack(String name, int year, int semester) {
        Pack pack = new Pack();
        pack.setName(name);
        pack.setYear(year);
        pack.setSemester(semester);
        return packService.save(pack);
    }

    private List<Course> createCourses(int count, List<Instructor> instructors, List<Pack> packs) {
        List<Course> courses = new ArrayList<>();

        String[] courseTypes = {"Programare", "Baze de date", "Web", "Mobile", "AI", "Cloud", "Security", "Network"};
        String[] levels = {"Introductory", "Intermediate", "Advanced", "Expert"};

        for (int i = 0; i < count; i++) {
            Course course = new Course();

            String courseType = courseTypes[random.nextInt(courseTypes.length)];
            String level = levels[random.nextInt(levels.length)];
            course.setName(level + " " + courseType + " " + faker.educator().course());
            course.setCode(generateCourseCode());
            course.setAbbreviation(generateAbbreviation(course.getName()));
            course.setDescription(faker.lorem().sentence(10));
            course.setGroupCount(1 + random.nextInt(3)); // 1-3 groups

            // 70% optional, 30% compulsory
            if (random.nextDouble() < 0.7) {
                course.setType(Course.CourseType.OPTIONAL);

                course.setPack(packs.get(random.nextInt(packs.size())));
            } else {
                course.setType(Course.CourseType.COMPULSORY);
                course.setPack(null);
            }

            course.setInstructor(instructors.get(random.nextInt(instructors.size())));

            courses.add(courseService.save(course));
        }

        return courses;
    }

    private String generateCourseCode() {
        String[] prefixes = {"CS", "IT", "MATH", "ENG", "PHY", "WEB", "DB", "AI"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        int number = 100 + random.nextInt(400);
        return prefix + number;
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private String generateAbbreviation(String courseName) {
        // abbr from first letters of words
        String[] words = courseName.split(" ");
        StringBuilder abbreviation = new StringBuilder();
        for(String word : words) {
            if(!word.isEmpty() && Character.isUpperCase(word.charAt(0))) {
                abbreviation.append(word.charAt(0));
            }
        }

        return abbreviation.length() > 0 ? abbreviation.toString() : "CRS";
    }

    private List<Student> createStudents(int count) {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Student student = new Student();
            student.setName(faker.name().fullName());
            student.setEmail(faker.internet().emailAddress());
            student.setCode("S" + String.format("%03d", i + 1)); // S001, S002, etc.
            student.setYear(2 + random.nextInt(2)); // Year 2 or 3
            students.add(studentService.save(student));
        }
        return students;
    }

    private void testCourseCrudOperations() {
        System.out.println("\n=== TESTING COURSE CRUD OPERATIONS ===");

        testCreateOperation();
        testReadOperations();
        testUpdateOperations();
        testDeleteOperations();
        testCustomQueries();
    }

    private void testCreateOperation() {
        System.out.println("\n1. TESTING CREATE OPERATION");

        Course newCourse = new Course();
        newCourse.setName("Special Test Course");
        newCourse.setCode("TEST999");
        newCourse.setAbbreviation("TST");
        newCourse.setType(Course.CourseType.OPTIONAL);
        newCourse.setDescription("A special course created for testing");
        newCourse.setGroupCount(2);

        List<Instructor> instructors = instructorService.findAll();
        List<Pack> packs = packService.findAll();

        if (!instructors.isEmpty() && !packs.isEmpty()) {
            newCourse.setInstructor(instructors.get(0));
            newCourse.setPack(packs.get(0));

            Course savedCourse = courseService.save(newCourse);
            System.out.println("Course created: " + savedCourse.getName() + " (ID: " + savedCourse.getId() + ")");
        }
    }

    private void testReadOperations() {
        System.out.println("\n2. TESTING READ OPERATIONS");

        List<Course> allCourses = courseService.findAll();
        System.out.println("Total courses in database: " + allCourses.size());

        // Read by ID
        if (!allCourses.isEmpty()) {
            Course firstCourse = allCourses.get(0);
            Optional<Course> foundCourse = courseService.findById(firstCourse.getId());
            foundCourse.ifPresent(course ->
                    System.out.println("Found course by ID: " + course.getName())
            );
        }

        List<Course> programmingCourses = courseService.findByName("Program");
        System.out.println("Courses with 'Program' in name: " + programmingCourses.size());//!!!!!!!!!!!!!!!!!!!!!!
    }

    private void testUpdateOperations() {
        System.out.println("\n3. TESTING UPDATE OPERATIONS");

        List<Course> allCourses = courseService.findAll();
        if (!allCourses.isEmpty()) {
            Course courseToUpdate = allCourses.get(0);
            String originalName = courseToUpdate.getName();
            String newName = originalName + " - UPDATED";

            courseService.updateName(courseToUpdate.getId(), newName);

            Optional<Course> updatedCourse = courseService.findById(courseToUpdate.getId());
            updatedCourse.ifPresent(course -> {
                System.out.println("Course name updated from: " + originalName);
                System.out.println("Course name updated to: " + course.getName());

                //revert change
                courseService.updateName(course.getId(), originalName);
            });
        }
    }

    private void testDeleteOperations() {
        System.out.println("\n4. TESTING DELETE OPERATIONS");

        //temp
        Course tempCourse = new Course();
        tempCourse.setName("Temporary Course to Delete");
        tempCourse.setCode("TEMP001");
        tempCourse.setType(Course.CourseType.OPTIONAL);

        List<Instructor> instructors = instructorService.findAll();
        List<Pack> packs = packService.findAll();

        if (!instructors.isEmpty() && !packs.isEmpty()) {
            tempCourse.setInstructor(instructors.get(0));
            tempCourse.setPack(packs.get(0));

            Course savedTempCourse = courseService.save(tempCourse);
            System.out.println("Created temporary course: " + savedTempCourse.getName());

            long countBefore = courseService.findAll().size();
            courseService.deleteById(savedTempCourse.getId());
            long countAfter = courseService.findAll().size();

            System.out.println("Courses before delete: " + countBefore);
            System.out.println("Courses after delete: " + countAfter);
            System.out.println("Delete successful: " + (countBefore > countAfter));
        }
    }

    private void testCustomQueries() {
        System.out.println("\n5. TESTING CUSTOM QUERIES");

//        //test derived query
//        List<Course> year2Courses = courseService.findByAbbreviation("TST");
//        System.out.println("Course with abbreviation 'TST': " + year2Courses.size());
//

        //test JPQL query
        List<Instructor> instructors = instructorService.findAll();
        if (!instructors.isEmpty()) {
            List<Course> instructorCourses = courseService.findByInstructorId(instructors.get(0).getId());
            System.out.println("Courses by instructor object: " + instructorCourses.size());
        }

        //test course types
        List<Course> allCourses = courseService.findAll();
        long compulsoryCount = allCourses.stream()
                .filter(c -> c.getType() == Course.CourseType.COMPULSORY)
                .count();
        long optionalCount = allCourses.stream()
                .filter(c -> c.getType() == Course.CourseType.OPTIONAL)
                .count();

        System.out.println("Compulsory courses: " + compulsoryCount);
        System.out.println("Optional courses: " + optionalCount);
    }

    public void populateSamplePreferences() {
        System.out.println("\n=== CREATING SAMPLE PREFERENCES ===");

        // Utilizarea serviciilor pentru a obține datele existente
        List<Student> students = studentService.findAll();
        List<Course> courses = courseService.findAll();

        if (students.isEmpty() || courses.isEmpty()) {
            System.out.println("No students or courses available for creating preferences");
            return;
        }

        int preferenceCount = 0;

        // Generăm preferințe pentru primii 5 studenți
        for (int s = 0; s < Math.min(5, students.size()); s++) {
            Student student = students.get(s);

            // Filtrare cursuri: anul pachetului trebuie să coincidă cu anul studentului
            List<Course> availableCourses = courses.stream()
                    .filter(course -> course.getPack() != null &&
                            course.getPack().getYear() == student.getYear())
                    .collect(Collectors.toList());

            // Alocăm între 3 și 5 preferințe per student în funcție de disponibilitate
            int maxPreferences = Math.min(3 + (s % 3), availableCourses.size());

            for (int i = 0; i < maxPreferences; i++) {
                Course course = availableCourses.get(i);

                try {
                    // Utilizarea @AllArgsConstructor generat de Lombok pentru StudentPreferenceRequestDTO
                    // Prioritatea este Integer pentru a permite ordine parțială
                    StudentPreferenceRequestDTO requestDTO = new StudentPreferenceRequestDTO(
                            student.getId(),
                            course.getId(),
                            i + 1 // Priority: 1, 2, 3...
                    );

                    // Salvarea prin serviciu care include validările de business
                    studentPreferenceService.save(requestDTO);
                    preferenceCount++;

                    System.out.println("Created preference: Student[" + student.getCode() + "] -> Course[" +
                            course.getCode() + "] (Priority: " + (i + 1) + ")");

                } catch (Exception e) {
                    // Prinde StudentPreferenceException dacă preferința există deja (UniqueConstraint)
                    System.out.println("Could not create preference for " + student.getCode() +
                            " -> " + course.getCode() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Successfully created " + preferenceCount + " sample preferences.");
    }

    public void testCompleteFlow() {
        System.out.println("\n=== TESTING COMPLETE STUDENT PREFERENCE FLOW ===");

        populateDatabase();

        populateSamplePreferences();

        testPreferenceOperations();

        testCriteriaSearch();
    }

    private void testPreferenceOperations() {
        System.out.println("\n=== TESTING PREFERENCE OPERATIONS ===");

        List<StudentPreferenceResponseDTO> allPreferences = studentPreferenceService.findAll();
        System.out.println("Total preferences in database: " + allPreferences.size());

        if (!allPreferences.isEmpty()) {
            StudentPreferenceResponseDTO firstPreference = allPreferences.get(0);
            List<StudentPreferenceResponseDTO> studentPreferences = studentPreferenceService
                    .findByStudentId(firstPreference.getStudentId());
            System.out.println("Preferences for student " + firstPreference.getStudentCode() +
                    ": " + studentPreferences.size());

            List<StudentPreferenceResponseDTO> coursePreferences = studentPreferenceService
                    .findByCourseId(firstPreference.getCourseId());
            System.out.println("Preferences for course " + firstPreference.getCourseCode() +
                    ": " + coursePreferences.size());

            // eTag
            System.out.println("ETag for first preference: " + firstPreference.getETag());

            Optional<StudentPreferenceResponseDTO> foundPreference = studentPreferenceService
                    .findById(firstPreference.getId());
            foundPreference.ifPresent(pref ->
                    System.out.println("Found preference by ID: " + pref.getId())
            );
        }

        testPreferenceErrorCases();
    }

    private void testPreferenceErrorCases() {
        System.out.println("\n=== TESTING PREFERENCE ERROR CASES ===");

        List<Student> students = studentService.findAll();
        List<Course> courses = courseService.findAll();

        if (!students.isEmpty() && !courses.isEmpty()) {
            Student student = students.get(0);
            Course course = courses.get(0);

            //duplicate pref
            try {
                StudentPreferenceRequestDTO duplicateRequest = new StudentPreferenceRequestDTO(
                        student.getId(), course.getId(), 1
                );
                studentPreferenceService.save(duplicateRequest);
                System.out.println("ERROR: Duplicate preference should have been rejected!");
            } catch (Exception e) {
                System.out.println("Correctly rejected duplicate: " + e.getMessage());
            }

            //non-existent student
            try {
                StudentPreferenceRequestDTO invalidRequest = new StudentPreferenceRequestDTO(
                        99999L, course.getId(), 1
                );
                studentPreferenceService.save(invalidRequest);
                System.out.println("ERROR: Non-existent student should have been rejected!");
            } catch (Exception e) {
                System.out.println("Correctly rejected non-existent student: " + e.getMessage());
            }
        }
    }

    private void testCriteriaSearch() {
        System.out.println("\n=== TESTING CRITERIA API SEARCH ===");

        //cursuri optionale din anul 3, semestrul 2
        CourseSearchCriteriaDTO criteria1 = new CourseSearchCriteriaDTO();
        criteria1.setType(Course.CourseType.OPTIONAL);
        criteria1.setPackYear(3);
        criteria1.setPackSemester(2);

        List<Course> optionalYear3Sem2 = courseService.searchCourses(criteria1);
        System.out.println("Optional courses in year 3, semester 2: " + optionalYear3Sem2.size());
        optionalYear3Sem2.forEach(course ->
                System.out.println(" - " + course.getName() + " (Pack: " +
                        (course.getPack() != null ? course.getPack().getName() : "N/A") + ")")
        );

        //cursuri predate de un anumit instructor
        List<Instructor> instructors = instructorService.findAll();
        if (!instructors.isEmpty()) {
            CourseSearchCriteriaDTO criteria2 = new CourseSearchCriteriaDTO();
            criteria2.setInstructorId(instructors.get(0).getId());

            List<Course> instructorCourses = courseService.searchCourses(criteria2);
            System.out.println("\nCourses taught by " + instructors.get(0).getName() + ": " + instructorCourses.size());
            instructorCourses.forEach(course ->
                    System.out.println(" - " + course.getName())
            );
        }

        // comb multiple filtre
        CourseSearchCriteriaDTO criteria3 = new CourseSearchCriteriaDTO();
        criteria3.setType(Course.CourseType.OPTIONAL);
        criteria3.setPackYear(2);
        criteria3.setGroupCount(2);

        List<Course> complexSearch = courseService.searchCourses(criteria3);
        System.out.println("\nOptional year 2 courses with 2 groups: " + complexSearch.size());
        complexSearch.forEach(course ->
                System.out.println(" - " + course.getName() + " (Groups: " + course.getGroupCount() + ")")
        );
    }
}