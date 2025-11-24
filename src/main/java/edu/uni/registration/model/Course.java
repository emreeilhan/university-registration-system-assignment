package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Represents a course offered by the university.
 * Each course has a unique code, title, credit hours, and optional prerequisites.
 */
public class Course {

    private final String code;
    private String title;
    private int credits;
    private List<String> prerequisites;

    /**
     * Creates a new course with the given information.
     *
     * @param code the unique course code (e.g., "CS101")
     * @param title the course title
     * @param credits the number of credit hours (must be positive)
     * @throws IllegalArgumentException if credits is not positive
     */
    public Course(String code, String title, int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.prerequisites = new ArrayList<>();

    }

    /**
     * Gets the course code.
     *
     * @return the course code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the course title.
     *
     * @return the course title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the number of credit hours for this course.
     *
     * @return the credit hours
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Gets a read-only list of prerequisite course codes.
     *
     * @return an unmodifiable list of course codes
     */
    public List<String> getPrerequisites() {
        return Collections.unmodifiableList(prerequisites);
    }

    /**
     * Sets the course title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the number of credit hours for this course.
     *
     * @param credits the new credit hours (must be positive)
     * @throws IllegalArgumentException if credits is not positive
     */
    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.credits = credits;
    }

    /**
     * Adds a prerequisite course code to this course.
     *
     * @param courseCode the code of the prerequisite course
     * @throws IllegalArgumentException if courseCode is null or blank
     */
    public void addPrerequisite(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException("Prerequisite course code cannot be null or blank");
        }
        prerequisites.add(courseCode);
    }

    /**
     * Removes a prerequisite course code from this course.
     *
     * @param courseCode the code of the prerequisite to remove
     */
    public void removePrerequisite(String courseCode) {
        prerequisites.remove(courseCode);
    }

    @Override
    public String toString() {
        return "Course{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", credits=" + credits +
                ", prerequisites=" + prerequisites +
                '}';
    }


}
