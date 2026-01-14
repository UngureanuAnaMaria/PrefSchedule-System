package com.example.prefSchedule.service;

import com.example.prefSchedule.client.StableMatchClient;
import com.example.prefSchedule.dto.request.CoursePrefs;
import com.example.prefSchedule.dto.request.MatchingRequest;
import com.example.prefSchedule.dto.request.StudentPrefs;
import com.example.prefSchedule.dto.response.MatchingResponse;
import com.example.prefSchedule.entity.*;
import com.example.prefSchedule.repository.PackRepository;
import com.example.prefSchedule.repository.StudentAssignmentRepository;
import com.example.prefSchedule.repository.StudentPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PackService {
    @Autowired
    private PackRepository repo;

    @Autowired
    private StudentPreferenceRepository preferenceRepository;

    @Autowired
    private StudentsRankingService rankingService;

    @Autowired
    private StudentAssignmentRepository assignmentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StableMatchClient stableMatchClient;

    public Pack save(Pack pack) {
        return repo.save(pack);
    }

    public List<Pack> findAll() {
        return repo.findAll();
    }

    public Optional<Pack> findById(Long id) {
        return repo.findById(id);
    }

    public List<Pack> findByName(String name) {
        return repo.findByName(name);
    }

    public List<Pack> findByYear(Integer year) {
        return repo.findByYear(year);
    }

    public List<Pack> findBySemester(Integer semester) {
        return repo.findBySemester(semester);
    }

    public List<Pack> findByYearAndSemester(Integer year, Integer semester) {
        return repo.findByYearAndSemester(year, semester);
    }

    public void updateName(Long id, String newName) {
        int result = repo.updateName(id, newName);
        if (result == 0) {
            throw new RuntimeException("Pack not found with id: " + id);
        }
    }

    public void updateYear(Long id, Integer newYear) {
        int result = repo.updateYear(id, newYear);
        if (result == 0) {
            throw new RuntimeException("Pack not found with id: " + id);
        }
    }

    public void updateSemester(Long id, Integer newSemester) {
        int result = repo.updateSemester(id, newSemester);
        if (result == 0) {
            throw new RuntimeException("Pack not found with id: " + id);
        }
    }

    public void deleteById(Long id) {
        Optional<Pack> pack = repo.findById(id);
        if (!pack.isPresent()) {
            throw new RuntimeException("Pack not found with id: " + id);
        }
        repo.deleteById(id);
    }

    public void deleteByName(String name) {
        List<Pack> packs = repo.findByName(name);
        if (packs.isEmpty()) {
            throw new RuntimeException("Packs not found with name: " + name);
        }
        repo.deleteByName(name);
    }

    public void deleteByYear(Integer year) {
        List<Pack> packs = repo.findByYear(year);
        if (packs.isEmpty()) {
            throw new RuntimeException("Packs not found with year: " + year);
        }
        repo.deleteByYear(year);
    }

    public void deleteBySemester(Integer semester) {
        List<Pack> packs = repo.findBySemester(semester);
        if (packs.isEmpty()) {
            throw new RuntimeException("Packs not found with semester: " + semester);
        }
        repo.deleteBySemester(semester);
    }

    public void deleteByYearAndSemester(Integer year, Integer semester) {
        List<Pack> packs = repo.findByYearAndSemester(year, semester);
        if (packs.isEmpty()) {
            throw new RuntimeException("Packs not found with year: " + year + " and semester: " + semester);
        }
        repo.deleteByYearAndSemester(year, semester);
    }

    public MatchingResponse matchStudentsForPack(Long packId) {
        // 1. Preluăm pachetul și cursurile sale
        Pack pack = repo.findById(packId)
                .orElseThrow(() -> new RuntimeException("Pack not found"));

        List<Course> packCourses = pack.getCourses();
        Set<String> packCourseCodes = packCourses.stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());

        // 2. Preluăm TOȚI studenții din anul respectiv (nu doar cei care au pus preferințe)
        List<Student> allStudentsFromYear = studentService.findByYear(pack.getYear());

        // 3. Preluăm preferințele existente
        List<StudentPreference> allPrefs = preferenceRepository.findByCourseIn(packCourses);
        Map<String, List<StudentPreference>> prefsByStudent = allPrefs.stream()
                .collect(Collectors.groupingBy(p -> p.getStudent().getCode()));

        // 4. Construim lista de StudentPrefs pentru TOȚI studenții
        List<StudentPrefs> studentRequests = new ArrayList<>();

        for (Student student : allStudentsFromYear) {
            String studentCode = student.getCode();
            List<String> sortedCourseCodes = new ArrayList<>();

            // Dacă studentul are preferințe, le sortăm și le adăugăm
            if (prefsByStudent.containsKey(studentCode)) {
                sortedCourseCodes = prefsByStudent.get(studentCode).stream()
                        .sorted(Comparator.comparingInt(StudentPreference::getPriority))
                        .map(p -> p.getCourse().getCode())
                        .filter(packCourseCodes::contains)
                        .collect(Collectors.toList());
            }
            // Dacă nu are preferințe (lista rămâne goală), algoritmul va primi o listă goală pentru el

            studentRequests.add(new StudentPrefs(studentCode, sortedCourseCodes));
        }

        // 5. Construim lista de CoursePrefs (Ranking-ul de la instructori)
        // Folosim codurile tuturor studenților din an pentru ranking-ul profesorilor
        List<String> allStudentCodes = allStudentsFromYear.stream()
                .map(Student::getCode)
                .collect(Collectors.toList());

        List<CoursePrefs> courseRequests = new ArrayList<>();

        for (Course course : packCourses) {
            // Instructorii vor avea un ranking pentru TOȚI studenții (chiar și cei fără opțiuni)
            List<Map<String, Object>> rankedStudents = rankingService.rankStudentsForCourse(course.getCode(), allStudentCodes);

            List<String> sortedStudentCodes = rankedStudents.stream()
                    .map(m -> (String) m.get("studentCode"))
                    .collect(Collectors.toList());

            int capacity = (course.getGroupCount() != null) ? course.getGroupCount() * 30 : 30;
            courseRequests.add(new CoursePrefs(course.getCode(), capacity, sortedStudentCodes));
        }

        // 6. Trimitere către StableMatch
        MatchingRequest request = new MatchingRequest();
        request.setStudents(studentRequests);
        request.setCourses(courseRequests);

        System.out.println(request);

        return stableMatchClient.solveMatching(request, "gale-shapley");
    }

    public MatchingResponse matchAndSave(Long packId) {
        // 1. Obținem matching-ul (logica anterioară)
        MatchingResponse response = matchStudentsForPack(packId);

        if (response.isSuccess() && response.getAssignments() != null) {
            // 2. Ștergem asignările vechi pentru acest pachet dacă este o re-evaluare
            assignmentRepository.deleteByPackId(packId);

            // 3. Salvăm noile asignări
            List<StudentAssignment> toSave = response.getAssignments().stream()
                    .map(a -> new StudentAssignment(
                            null,
                            a.getStudentCode(),
                            a.getCourseCode(),
                            a.getStudentPreferenceRank(),
                            a.getCoursePreferenceRank(),
                            LocalDateTime.now(),
                            packId
                    ))
                    .collect(Collectors.toList());

            assignmentRepository.saveAll(toSave);
        }

        return response;
    }
}