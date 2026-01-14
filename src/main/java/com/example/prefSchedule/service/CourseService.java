package com.example.prefSchedule.service;

import com.example.prefSchedule.dto.CourseSearchCriteriaDTO;
import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.Course.CourseType;
import com.example.prefSchedule.repository.CourseCriteriaRepository;
import com.example.prefSchedule.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository repo;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private PackService packService;

    @Autowired
    private CourseCriteriaRepository courseCriteriaRepository;

    public Course save(Course course) {
        return repo.save(course);
    }

    public List<Course> findAll() {
        return repo.findAll();
    }

    public Optional<Course> findById(Long id) {
        return repo.findById(id);
    }

    public Optional<Course> findByCode(String code) {
        return repo.findByCode(code);
    }

    public List<Course> findByName(String name) {
        return repo.findByName(name);
    }

    public List<Course> findByType(CourseType type) {
        return repo.findByType(type);
    }

    public Optional<Course> findByAbbreviation(String abbreviation) {
        return repo.findByAbbreviation(abbreviation);
    }

    public List<Course> findByGroupCount(Integer groupCount) {
        return repo.findByGroupCount(groupCount);
    }

    public List<Course> findByInstructorId(Long instructorId) {
        return repo.findByInstructorId(instructorId);
    }

    public List<Course> findByPackId(Long packId) {
        return repo.findByPackId(packId);
    }

    //modificare cu flush
    public void updateCode(Long id, String newCode) {
        Optional<Course> course = repo.findByCode(newCode);
        if(course.isPresent() && !course.get().getId().equals(id)) {
            throw new RuntimeException("Course with code '" + newCode + "' already exists");
        }

        int result = repo.updateCode(id, newCode);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updateName(Long id, String newName) {
        int result = repo.updateName(id, newName);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updateType(Long id, CourseType newType) {
        int result = repo.updateType(id, newType);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updateAbbreviation(Long id, String newAbbreviation) {
        int result = repo.updateAbbreviation(id, newAbbreviation);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updateGroupCount(Long id, Integer newGroupCount) {
        int result = repo.updateGroupCount(id, newGroupCount);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updateDescription(Long id, String newDescription) {
        int result = repo.updateDescription(id, newDescription);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updateInstructor(Long id, Long instructorId) {
        int result = repo.updateInstructor(id, instructorId);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void updatePack(Long id, Long packId) {
        int result = repo.updatePack(id, packId);
        if (result == 0) {
            throw new RuntimeException("Course not found with id: " + id);
        }
    }

    public void deleteById(Long id) {
        Optional<Course> course = repo.findById(id);
        if (!course.isPresent()) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        repo.deleteById(id);
    }

    public void deleteByCode(String code) {
        Optional<Course> course = repo.findByCode(code);
        if (!course.isPresent()) {
            throw new RuntimeException("Course not found with code: " + code);
        }
        repo.deleteByCode(code);
    }

    public void deleteByName(String name) {
        List<Course> courses = repo.findByName(name);
        if (courses.isEmpty()) {
            throw new RuntimeException("Courses not found with name: " + name);
        }
        repo.deleteByName(name);
    }

    public void deleteByType(CourseType type) {
        List<Course> courses = repo.findByType(type);
        if (courses.isEmpty()) {
            throw new RuntimeException("Courses not found with type: " + type);
        }
        repo.deleteByType(type);
    }

    public void deleteByAbbreviation(String abbreviation) {
        Optional<Course> course = repo.findByAbbreviation(abbreviation);
        if (!course.isPresent()) {
            throw new RuntimeException("Courses not found with abbreviation: " + abbreviation);
        }
        repo.deleteByAbbreviation(abbreviation);
    }

    public void deleteByInstructorId(Long instructorId) {
        List<Course> courses = repo.findByInstructorId(instructorId);
        if (courses.isEmpty()) {
            throw new RuntimeException("Courses not found with instructor id: " + instructorId);
        }
        repo.deleteByInstructorId(instructorId);
    }

    public void deleteByPackId(Long packId) {
        List<Course> courses = repo.findByPackId(packId);
        if (courses.isEmpty()) {
            throw new RuntimeException("Courses not found with pack id: " + packId);
        }
        repo.deleteByPackId(packId);
    }

    public void assignInstructorAndPack(Course course, Long instructorId, Long packId) {
        instructorService.findById(instructorId).ifPresent(course::setInstructor);
        packService.findById(packId).ifPresent(course::setPack);
    }

    //!!!!!!!!!!!!!!!!!!

    public List<Course> searchCourses(CourseSearchCriteriaDTO criteria) {
        return courseCriteriaRepository.findByCriteria(criteria);
    }

    public List<Course> searchCoursesWithPagination(CourseSearchCriteriaDTO criteria, int page, int size) {
        return courseCriteriaRepository.findByCriteriaWithPagination(criteria, page, size);
    }

    public long countCourses(CourseSearchCriteriaDTO criteria) {
        return courseCriteriaRepository.countByCriteria(criteria);
    }

    public boolean isCourseInstructor(Long courseId, String username) {
        Optional<Course> course = findById(courseId);
        return course.isPresent() &&
                course.get().getInstructor() != null &&
                course.get().getInstructor().getUsername().equals(username);
    }
}