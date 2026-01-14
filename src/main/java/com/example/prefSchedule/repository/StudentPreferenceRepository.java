package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.StudentPreference;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPreferenceRepository extends JpaRepository<StudentPreference, Long> {
    List<StudentPreference> findByStudentId(Long studentId);
    List<StudentPreference> findByCourseId(Long courseId);
    Optional<StudentPreference> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<StudentPreference> findByCourseIn(List<Course> courses);

    List<StudentPreference> findByStudentIdOrderByPriorityAsc(Long studentId);

    // De asemenea, asigură sortarea pentru cursuri multiple:
    List<StudentPreference> findByCourseInOrderByPriorityAsc(List<Course> courses);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Modifying
    @Transactional
    @Query("UPDATE StudentPreference sp SET sp.priority = :newPriority WHERE sp.id = :id")
    int updatePriority(@Param("id") Long id, @Param("newPriority") Integer newPriority);

    @Modifying
    @Transactional
    void deleteByStudentId(Long studentId);

    @Modifying
    @Transactional
    void deleteByCourseId(Long courseId);

    @Modifying
    @Transactional
    void deleteByStudentIdAndCourseId(Long studentId, Long courseId);
}