package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.InstructorPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface InstructorPreferenceRepository extends JpaRepository<InstructorPreference, Long> {
    List<InstructorPreference> findByCourseCode(String courseCode);
    List<InstructorPreference> findByInstructorId(Long instructorId);
    List<InstructorPreference> findByCourseCodeAndInstructorId(String courseCode, Long instructorId);

    @Query("SELECT ip.compulsoryCourseAbbr, ip.weightPercentage " +
            "FROM InstructorPreference ip " +
            "WHERE ip.courseCode = :courseCode")
    List<Object[]> findAbbrAndWeightsByCourseCode(@Param("courseCode") String courseCode);
}
