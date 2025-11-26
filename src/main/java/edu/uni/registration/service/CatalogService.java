package edu.uni.registration.service;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Section;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;

import java.util.List;

/**
 * Service for managing the course catalog. Handles courses, sections, searching, and instructor assignment.
 */
public interface CatalogService {

    /**
     * Searches courses by code, title, credits, instructor, or time window.
     */
    Result<List<Course>> search(CourseQuery query);

    Result<Course> createCourse(String code, String title, int credits);

    Result<Section> createSection(String id, Course course, String term, int capacity);

    Result<Void> assignInstructor(String sectionId, String instructorId);

    /**
     * Admin override: changes section capacity. Logged for audit.
     */
    Result<Void> adminOverrideCapacity(String sectionId, int newCapacity, String adminId, String reason);

    Result<List<Section>> getInstructorSections(String instructorId);

    /**
     * Updates course fields. Null args are ignored. Used by Admin CLI.
     */
    Result<Course> updateCourse(String code, String newTitle, Integer newCredits);
}
