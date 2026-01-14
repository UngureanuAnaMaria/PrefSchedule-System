package com.example.prefSchedule.service;

import com.example.prefSchedule.entity.Course;
import com.example.prefSchedule.entity.InstructorPreference;
import com.example.prefSchedule.entity.StudentGrade;
import com.example.prefSchedule.repository.CourseRepository;
import com.example.prefSchedule.repository.InstructorPreferenceRepository;
import com.example.prefSchedule.repository.StudentGradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentsRankingService {
    @Autowired
    private InstructorPreferenceRepository preferenceRepository;

    @Autowired
    private StudentGradeRepository studentGradeRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Double calculateWeightedAverage(String studentCode, String optionalCourseCode) {
        List<InstructorPreference> preferences = preferenceRepository.findByCourseCode(optionalCourseCode);
        List<StudentGrade> studentGrades = studentGradeRepository.findByStudentCode(studentCode);

        if (preferences.isEmpty()) {
            double average = studentGrades.stream()
                    .mapToDouble(StudentGrade::getGrade)
                    .average()
                    .orElse(0.0);
            return average;
        }

        double totalWeight = preferences.stream()
                .mapToDouble(InstructorPreference::getWeightPercentage)
                .sum();

        double weightedSum = 0.0;

        for (InstructorPreference pref : preferences) {
            String abbr = pref.getCompulsoryCourseAbbr();
            Optional<Course> courseOpt = courseRepository.findByAbbreviation(abbr);
            if(!courseOpt.isPresent()) {
                throw new RuntimeException("Course not found");
            }
            Course course = courseOpt.get();
            double weight = pref.getWeightPercentage() / totalWeight;

            Optional<StudentGrade> grade = studentGradeRepository
                    .findByStudentCodeAndCourseCode(studentCode, course.getCode());

            if(grade.isPresent()) {
                weightedSum += grade.get().getGrade() * weight;
            }
        }
        return weightedSum;
    }

    public List<Map<String, Object>> rankStudentsForCourse(String optionalCourseCode, List<String> studentCodes) {
        return studentCodes.stream()
                .map(studentCode -> {
                    double weightedAvg = calculateWeightedAverage(studentCode, optionalCourseCode);
                    return Map.<String, Object>of(
                            "studentCode", studentCode,
                            "weightedAverage", weightedAvg,
                            "courseCode", optionalCourseCode
                    );
                })
                .sorted((a, b) -> Double.compare(
                        (Double) b.get("weightedAverage"),
                        (Double) a.get("weightedAverage"))) // Descending
                .collect(Collectors.toList());
    }
}