package edu.uni.registration.service;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Section;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;

import java.util.List;

/**
 * Service interface for managing the course catalog.
 * Handles creation of courses/sections, searching, and instructor assignment.
 */
public interface CatalogService {

    /**
     * Searches for courses based on various criteria defined in CourseQuery.
     * Supports filtering by code, title, credits, instructor, and time window.
     *
     * @param query The search criteria object.
     * @return A Result containing a list of matching courses.
     */
    Result<List<Course>> search(CourseQuery query);

    /**
     * Creates a new course in the catalog.
     *
     * @param code    Unique course code (e.g., "CS101").
     * @param title   Course title.
     * @param credits Number of credits.
     * @return A Result containing the created Course.
     */
    Result<Course> createCourse(String code, String title, int credits);

    /**
     * Creates a new section offering for a course.
     *
     * @param id       Unique section ID (e.g., "SEC1").
     * @param course   The parent course.
     * @param term     The term (e.g., "Fall").
     * @param capacity Maximum number of students.
     * @return A Result containing the created Section.
     */
    Result<Section> createSection(String id, Course course, String term, int capacity);

    /**
     * Assigns an instructor to a specific section.
     *
     * @param sectionId  The ID of the section.
     * @param instructor The Instructor object to assign.
     * @return A Result indicating success or failure.
     */
    Result<Void> assignInstructor(String sectionId, Instructor instructor);

    /**
     * Allows an admin to manually override the capacity of a section.
     * Useful for opening extra seats in special cases. Logged for audit.
     *
     * @param sectionId   The ID of the section.
     * @param newCapacity The new capacity value.
     * @param adminId     The ID of the admin performing the action.
     * @param reason      Reason for the change.
     * @return A Result indicating success or failure.
     */
    Result<Void> adminOverrideCapacity(String sectionId, int newCapacity, String adminId, String reason);
}
