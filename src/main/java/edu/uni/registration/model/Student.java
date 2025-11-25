package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student entity that extends Person.
 * Holds academic info like major, year, and their active enrollments.
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

    /**
     * Returns the role of this person, which is always "STUDENT".
     *
     * @return the string "STUDENT"
     */
    @Override
    public String role() {
        return "STUDENT";
    }
    
    /**
     * Returns a formatted string showing the student's profile,
     * including name, major, year, number of enrollments, and GPA.
     *
     * @return a formatted profile string
     */
    @Override
    public String displayProfile() {
        return String.format("Student Profile: %s\nMajor: %s\nYear: %d\nCurrent Enrollments: %d\nGPA: %.2f",
                getFullName(),
                major,
                year,
                currentEnrollments.size(),
                transcript.getGpa());
    }

    // Standard getters/setters for student details
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

    /**
     * Adds an enrollment to the student's current enrollments list.
     *
     * @param enrollment the enrollment to add
     * @throws IllegalArgumentException if enrollment is null
     */
    public void addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
    
        currentEnrollments.add(enrollment);
    }

    /**
     * Removes an enrollment from the student's current enrollments.
     *
     * @param enrollment the enrollment to remove
     * @return true if the enrollment was removed, false if it wasn't found
     */
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
