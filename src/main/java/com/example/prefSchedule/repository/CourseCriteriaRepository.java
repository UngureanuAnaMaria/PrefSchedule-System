package com.example.prefSchedule.repository;

import com.example.prefSchedule.dto.CourseSearchCriteriaDTO;
import com.example.prefSchedule.entity.Course;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseCriteriaRepository {

    //injectat automat de JPA configurat conform application.properties
    @PersistenceContext
    private EntityManager entityManager;

    public List<Course> findByCriteria(CourseSearchCriteriaDTO criteria) {
        //"constructor de queries"
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(Course.class);
        //entitate de baza
        Root<Course> root = query.from(Course.class);

        //predicat = WHERE
        List<Predicate> predicates = buildPredicates(criteria, cb, root);

        // conjunctie de predicate
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    public List<Course> findByCriteriaWithPagination(CourseSearchCriteriaDTO criteria, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> query = cb.createQuery(Course.class);
        Root<Course> root = query.from(Course.class);

        List<Predicate> predicates = buildPredicates(criteria, cb, root);
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    //pt paginare
    public long countByCriteria(CourseSearchCriteriaDTO criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Course> root = query.from(Course.class);

        List<Predicate> predicates = buildPredicates(criteria, cb, root);
        query.select(cb.count(root));
        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> buildPredicates(CourseSearchCriteriaDTO criteria, CriteriaBuilder cb, Root<Course> root) {
        List<Predicate> predicates = new ArrayList<>();

        // filtru
        //root.get("code") -> referinta catre col code
        if (criteria.getCode() != null && !criteria.getCode().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("code")),
                    "%" + criteria.getCode().toLowerCase() + "%"));
        }

        if (criteria.getName() != null && !criteria.getName().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")),
                    "%" +criteria.getName().toLowerCase() + "%"));
        }

        if (criteria.getType() != null) {
            predicates.add(cb.equal(root.get("type"), criteria.getType()));
        }

        if (criteria.getAbbreviation() != null && !criteria.getAbbreviation().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("abbreviation")),
                    "%" + criteria.getAbbreviation().toLowerCase() + "%"));
        }

        if (criteria.getGroupCount() != null) {
            predicates.add(cb.equal(root.get("groupCount"), criteria.getGroupCount()));
        }

        if (criteria.getInstructorId() != null) {
            predicates.add(cb.equal(root.get("instructor").get("id"), criteria.getInstructorId()));
        }

        if (criteria.getPackId() != null) {
            predicates.add(cb.equal(root.get("pack").get("id"), criteria.getPackId()));
        }

        if (criteria.getPackYear() != null) {
            predicates.add(cb.equal(root.get("pack").get("year"), criteria.getPackYear()));
        }

        if (criteria.getPackSemester() != null) {
            predicates.add(cb.equal(root.get("pack").get("semester"), criteria.getPackSemester()));
        }

        return predicates;
    }
}