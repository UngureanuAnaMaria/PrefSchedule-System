package com.example.prefSchedule.dto;

import com.example.prefSchedule.entity.Course;

public class CourseSearchCriteriaDTO {
    private String code;
    private String name;
    private Course.CourseType type;
    private String abbreviation;
    private Integer groupCount;
    private Long instructorId;
    private Long packId;
    private Integer packYear;
    private Integer packSemester;

    // Constructors
    public CourseSearchCriteriaDTO() {}

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Course.CourseType getType() { return type; }
    public void setType(Course.CourseType type) { this.type = type; }

    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }

    public Integer getGroupCount() { return groupCount; }
    public void setGroupCount(Integer groupCount) { this.groupCount = groupCount; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

    public Long getPackId() { return packId; }
    public void setPackId(Long packId) { this.packId = packId; }

    public Integer getPackYear() { return packYear; }
    public void setPackYear(Integer packYear) { this.packYear = packYear; }

    public Integer getPackSemester() { return packSemester; }
    public void setPackSemester(Integer packSemester) { this.packSemester = packSemester; }
}