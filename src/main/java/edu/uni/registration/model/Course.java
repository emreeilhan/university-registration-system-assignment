package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Course {

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

    //Getter methods

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

    //Setter methods
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
    public String toString() {
        return "Course{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", credits=" + credits +
                ", prerequisites=" + prerequisites +
                '}';
    }


}
