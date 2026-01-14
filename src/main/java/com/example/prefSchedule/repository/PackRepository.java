package com.example.prefSchedule.repository;

import com.example.prefSchedule.entity.Pack;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {
    // Derived queries
    List<Pack> findByYear(Integer year);
    List<Pack> findBySemester(Integer semester);
    List<Pack> findByYearAndSemester(Integer year, Integer semester);

    // JPQL queries
    @Query("SELECT p FROM Pack p WHERE p.name LIKE %:name%")
    List<Pack> findByName(@Param("name") String name);

    // Modifying queries
    @Modifying
    @Transactional
    @Query("UPDATE Pack p SET p.name = :newName WHERE p.id = :id")
    int updateName(@Param("id") Long id, @Param("newName") String newName);

    @Modifying
    @Transactional
    @Query("UPDATE Pack p SET p.year = :newYear WHERE p.id = :id")
    int updateYear(@Param("id") Long id, @Param("newYear") Integer newYear);

    @Modifying
    @Transactional
    @Query("UPDATE Pack p SET p.semester = :newSemester WHERE p.id = :id")
    int updateSemester(@Param("id") Long id, @Param("newSemester") Integer newSemester);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pack p WHERE p.name = :name")
    int deleteByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pack p WHERE p.year = :year")
    int deleteByYear(@Param("year") Integer year);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pack p WHERE p.semester = :semester")
    int deleteBySemester(@Param("semester") Integer semester);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pack p WHERE p.year = :year AND p.semester = :semester")
    int deleteByYearAndSemester(@Param("year") Integer year, @Param("semester") Integer semester);
}