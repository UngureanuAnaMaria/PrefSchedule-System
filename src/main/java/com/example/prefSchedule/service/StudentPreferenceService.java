package com.example.prefSchedule.service;

import com.example.prefSchedule.dto.StudentPreferenceRequestDTO;
import com.example.prefSchedule.dto.StudentPreferenceResponseDTO;
import com.example.prefSchedule.entity.*;
        import com.example.prefSchedule.exception.StudentPreferenceException;
import com.example.prefSchedule.repository.StudentPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentPreferenceService {

    @Autowired
    private StudentPreferenceRepository repository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    public StudentPreferenceResponseDTO save(StudentPreferenceRequestDTO request) {
        if (repository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new StudentPreferenceException("Preference already exists for this student and course.");
        }

        Student student = studentService.findById(request.getStudentId())
                .orElseThrow(() -> new StudentPreferenceException("Student not found"));
        Course course = courseService.findById(request.getCourseId())
                .orElseThrow(() -> new StudentPreferenceException("Course not found"));

        if (!courseAvailableForStudent(student, course)) {
            throw new StudentPreferenceException("Course is not available for this student's year");
        }

        StudentPreference preference = new StudentPreference(student, course, request.getPriority());
        return convertToDTO(repository.save(preference));
    }

    public List<StudentPreferenceResponseDTO> findAll() {
        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<StudentPreferenceResponseDTO> findById(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    public List<StudentPreferenceResponseDTO> findByStudentId(Long studentId) {
        return repository.findByStudentIdOrderByPriorityAsc(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StudentPreferenceResponseDTO> findByCourseId(Long courseId) {
        return repository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StudentPreferenceResponseDTO> findByStudentIdOrderByPriorityAsc(Long studentId) {
        // Folosim metoda care ordonează automat rezultatele de la 1 la N
        return repository.findByStudentIdOrderByPriorityAsc(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentPreferenceResponseDTO updatePriority(Long id, Integer newPriority) {
        StudentPreference pref = repository.findById(id)
                .orElseThrow(() -> new StudentPreferenceException("Preference not found"));

        pref.setPriority(newPriority);
        return convertToDTO(repository.save(pref));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new StudentPreferenceException("Cannot delete: Preference not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByStudentId(Long studentId) {
        repository.deleteByStudentId(studentId);
    }

    @Transactional
    public void deleteByCourseId(Long courseId) {
        repository.deleteByCourseId(courseId);
    }

    @Transactional
    public void deleteByStudentIdAndCourseId(Long studentId, Long courseId) {
        repository.deleteByStudentIdAndCourseId(studentId, courseId);
    }

    public List<StudentPreference> getPreferencesForPack(Pack pack) {
        List<Course> packCourses = pack.getCourses();

        if (packCourses == null || packCourses.isEmpty()) {
            return List.of();
        }

        return repository.findByCourseIn(packCourses);
    }

    private boolean courseAvailableForStudent(Student student, Course course) {
        return course.getPack() != null && course.getPack().getYear() == student.getYear();
    }

    private StudentPreferenceResponseDTO convertToDTO(StudentPreference preference) {
        return new StudentPreferenceResponseDTO(
                preference.getId(),
                preference.getStudent().getId(),
                preference.getStudent().getName(),
                preference.getStudent().getCode(),
                preference.getCourse().getId(),
                preference.getCourse().getName(),
                preference.getCourse().getCode(),
                preference.getPriority(),
                preference.getVersion() != null ? "\"" + preference.getVersion() + "\"" : null
        );
    }
}