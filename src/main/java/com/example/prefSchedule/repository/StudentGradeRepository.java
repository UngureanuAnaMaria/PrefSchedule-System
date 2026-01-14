package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByStudentCode(String studentCode);
    List<StudentGrade> findByCourseCode(String courseCode);
    Optional<StudentGrade> findByStudentCodeAndCourseCode(String studentCode, String courseCode);
    List<StudentGrade> findByGrade(Double grade);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.grade >= :minGrade")
    List<StudentGrade> findGradesAbove(@Param("minGrade") Double minGrade);

    @Query("SELECT sg FROM StudentGrade sg WHERE sg.grade BETWEEN :min AND :max")
    List<StudentGrade> findGradesInHistory(@Param("min") Double min, @Param("max") Double max);

    @Modifying
    @Transactional
    @Query("UPDATE StudentGrade sg SET sg.grade = :newGrade WHERE sg.id = :id")
    int updateGrade(@Param("id") Long id, @Param("newGrade") Double newGrade);

    @Modifying
    @Transactional
    @Query("UPDATE StudentGrade sg SET sg.studentCode = :newStudentCode WHERE sg.id = :id")
    int updateStudentCode(@Param("id") Long id, @Param("newStudentCode") String newStudentCode);

    @Modifying
    @Transactional
    @Query("UPDATE StudentGrade sg SET sg.courseCode = :newCourseCode WHERE sg.id = :id")
    int updateCourseCode(@Param("id") Long id, @Param("newCourseCode") String newCourseCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentGrade sg WHERE sg.studentCode = :studentCode")
    int deleteByStudentCode(@Param("studentCode") String studentCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentGrade sg WHERE sg.courseCode = :courseCode")
    int deleteByCourseCode(@Param("courseCode") String courseCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentGrade sg WHERE sg.grade = :grade")
    int deleteByGrade(@Param("grade") Double grade);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentGrade sg WHERE sg.studentCode = :studentCode AND sg.courseCode = :courseCode")
    int deleteByStudentAndCourse(@Param("studentCode") String studentCode, @Param("courseCode") String courseCode);
}