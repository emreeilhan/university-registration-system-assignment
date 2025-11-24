package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a student in the university system.
 * Students can enroll in sections, have a transcript with grades,
 * and track their current course enrollments.
 */
public class Student extends Person {
    private String major;
    private int year;
    private final Transcript transcript;
    private final List<Enrollment> currentEnrollments;

    /**
     * Creates a new student with the given information.
     *
     * @param id the unique student ID
     * @param firstName the student's first name
     * @param lastName the student's last name
     * @param email the student's email
     * @param major the student's major field of study
     * @param year the student's year (1-4 typically)
     */
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

    /**
     * Gets the student's major field of study.
     *
     * @return the major name
     */
    public String getMajor() {
        return major;
    }

    /**
     * Gets the student's year level.
     *
     * @return the year (1-4 typically)
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the student's transcript containing all completed courses and grades.
     *
     * @return the transcript object
     */
    public Transcript getTranscript() {
        return transcript;
    }

    /**
     * Gets a read-only list of the student's current enrollments.
     *
     * @return an unmodifiable list of enrollments
     */
    public List<Enrollment> getCurrentEnrollments() {
        return Collections.unmodifiableList(currentEnrollments);
    }

    /**
     * Sets the student's major.
     *
     * @param major the new major name
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * Sets the student's year level.
     *
     * @param year the new year level
     */
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
