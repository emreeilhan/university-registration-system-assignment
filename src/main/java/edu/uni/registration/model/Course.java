package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Locale;

/**
 * Course entity. Has code, title, credits, and prerequisites.
 * Implements Searchable for keyword searching.
 */
public class Course implements Searchable {

    private final String code;
    private String title;
    private int credits;
    private List<String> prerequisites;

    public Course(String code, String title, int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.prerequisites = new ArrayList<>();

    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public List<String> getPrerequisites() {
        return Collections.unmodifiableList(prerequisites);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCredits(int credits) {
        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }
        this.credits = credits;
    }

    public void addPrerequisite(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException("Prerequisite course code cannot be null or blank");
        }
        prerequisites.add(courseCode);
    }

    public void removePrerequisite(String courseCode) {
        prerequisites.remove(courseCode);
    }

    @Override
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        
        if (code != null && code.toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
            return true;
        }
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
