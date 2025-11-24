package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an instructor in the university system.
 * Instructors can be assigned to sections and post grades for students.
 */
public class Instructor extends Person {
    private String department;
    private String officeNumber;
    private final List<Section> assignedSections;

    /**
     * Creates a new instructor with the given information.
     *
     * @param id the unique instructor ID
     * @param firstName the instructor's first name
     * @param lastName the instructor's last name
     * @param email the instructor's email
     * @param department the department the instructor belongs to
     * @param officeNumber the instructor's office number
     */
    public Instructor(String id, String firstName, String lastName, String email,
                      String department, String officeNumber) {
        super(id, firstName, lastName, email);
        this.department = department;
        this.officeNumber = officeNumber;
        this.assignedSections = new ArrayList<>();
    }

    /**
     * Returns the role of this person, which is always "INSTRUCTOR".
     *
     * @return the string "INSTRUCTOR"
     */
    @Override
    public String role() {
        return "INSTRUCTOR";
    }
    
    /**
     * Returns a formatted string showing the instructor's profile,
     * including name, department, office number, and number of assigned sections.
     *
     * @return a formatted profile string
     */
    @Override
    public String displayProfile() {
        return String.format("Instructor Profile: %s\nDepartment: %s\nOffice: %s\nAssigned Sections: %d",
                getFullName(),
                department,
                officeNumber,
                assignedSections.size());
    }

    /**
     * Gets the instructor's department.
     *
     * @return the department name
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Gets the instructor's office number.
     *
     * @return the office number
     */
    public String getOfficeNumber() {
        return officeNumber;
    }

    /**
     * Gets a read-only list of sections assigned to this instructor.
     *
     * @return an unmodifiable list of sections
     */
    public List<Section> getAssignedSections() {
        return Collections.unmodifiableList(assignedSections);
    }

    /**
     * Sets the instructor's department.
     *
     * @param department the new department name
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Sets the instructor's office number.
     *
     * @param officeNumber the new office number
     */
    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    /**
     * Adds a section to the instructor's list of assigned sections.
     *
     * @param section the section to add
     * @throws IllegalArgumentException if section is null
     */
    public void addAssignedSection(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }
        assignedSections.add(section);
    }

    /**
     * Removes a section from the instructor's assigned sections.
     *
     * @param section the section to remove
     * @return true if the section was removed, false if it wasn't found
     */
    public boolean removeAssignedSection(Section section) {
        if (section == null) {
            return false;
        }
        return assignedSections.remove(section);
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Department: " + department +
                ", Office: " + officeNumber +
                ", AssignedSections: " + assignedSections.size();
    }
}
