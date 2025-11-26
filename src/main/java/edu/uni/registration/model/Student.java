package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student entity. Holds major, year, enrollments, and transcript.
 */
public class Student extends Person {
    private String major;
    private int year;
    private final Transcript transcript;
    // Using List here to keep track of sections the student is currently in
    private final List<Enrollment> currentEnrollments;

    public Student(String id,String firstName,String lastName,String email,String major,int year) {
        super(id,firstName,lastName,email);
        this.major = major;
        this.year = year;
        this.transcript = new Transcript(this);
        this.currentEnrollments = new ArrayList<>();
    }

    @Override
    public String role() {
        return "STUDENT";
    }
    
    @Override
    public String displayProfile() {
        return String.format("Student Profile: %s\nMajor: %s\nYear: %d\nCurrent Enrollments: %d\nGPA: %.2f",
                getFullName(),
                major,
                year,
                currentEnrollments.size(),
                transcript.getGpa());
    }

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public List<Enrollment> getCurrentEnrollments() {
        return Collections.unmodifiableList(currentEnrollments);
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
    
        currentEnrollments.add(enrollment);
    }

    public boolean removeEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return false;
        }
        return currentEnrollments.remove(enrollment);
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Major: " + major +
                ", Year: " + year +
                ", ActiveEnrollments: " + currentEnrollments.size();
    }
}
