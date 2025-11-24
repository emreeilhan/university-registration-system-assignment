package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Locale;

/**
 * Represents a course offered by the university.
 * Each course has a unique code, title, credit hours, and optional prerequisites.
 * 
 * Implements Searchable interface to enable keyword-based searching.
 */
public class Course implements Searchable {

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

    /**
     * Checks if this course matches the given search keyword.
     * Performs case-insensitive matching against the course code and title.
     * 
     * @param keyword the search term to match against
     * @return true if the course code or title contains the keyword (case-insensitive), false otherwise
     */
    @Override
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        
        // Check if keyword matches course code
        if (code != null && code.toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
            return true;
        }
        
        // Check if keyword matches course title
        if (title != null && title.toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
            return true;
        }
        
        return false;
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
