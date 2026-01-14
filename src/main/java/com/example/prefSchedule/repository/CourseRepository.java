package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.Course.CourseType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Derived queries
    Optional<Course> findByCode(String code);
    List<Course> findByName(String name);
    List<Course> findByType(CourseType type);
    Optional<Course> findByAbbreviation(String abbreviation);
    List<Course> findByGroupCount(Integer groupCount);

    // JPQL queries
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT c FROM Course c WHERE c.pack.id = :packId")
    List<Course> findByPackId(@Param("packId") Long packId);

    // Modifying queries
    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.code = :newCode WHERE c.id = :id")
    int updateCode(@Param("id") Long id, @Param("newCode") String newCode);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.name = :newName WHERE c.id = :id")
    int updateName(@Param("id") Long id, @Param("newName") String newName);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.type = :newType WHERE c.id = :id")
    int updateType(@Param("id") Long id, @Param("newType") CourseType newType);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.abbreviation = :newAbbreviation WHERE c.id = :id")
    int updateAbbreviation(@Param("id") Long id, @Param("newAbbreviation") String newAbbreviation);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.groupCount = :newGroupCount WHERE c.id = :id")
    int updateGroupCount(@Param("id") Long id, @Param("newGroupCount") Integer newGroupCount);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.description = :newDescription WHERE c.id = :id")
    int updateDescription(@Param("id") Long id, @Param("newDescription") String newDescription);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.instructor.id = :instructorId WHERE c.id = :id")
    int updateInstructor(@Param("id") Long id, @Param("instructorId") Long instructorId);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.pack.id = :packId WHERE c.id = :id")
    int updatePack(@Param("id") Long id, @Param("packId") Long packId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.code = :code")
    int deleteByCode(@Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.name = :name")
    int deleteByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.type = :type")
    int deleteByType(@Param("type") CourseType type);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.abbreviation = :abbreviation")
    int deleteByAbbreviation(@Param("abbreviation") String abbreviation);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.instructor.id = :instructorId")
    int deleteByInstructorId(@Param("instructorId") Long instructorId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Course c WHERE c.pack.id = :packId")
    int deleteByPackId(@Param("packId") Long packId);
}