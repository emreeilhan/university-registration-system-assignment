package edu.uni.registration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Instructor extends Person {
    private String department;
    private String officeNumber;
    private final List<Section> assignedSections;

    public Instructor(String id, String firstName, String lastName, String email,
                      String department, String officeNumber) {
        super(id, firstName, lastName, email);
        this.department = department;
        this.officeNumber = officeNumber;
        this.assignedSections = new ArrayList<>();
    }

    
    @Override
    public String role() {
        return "INSTRUCTOR";
    }

    // Getter methods
    public String getDepartment() {
        return department;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

 
    public List<Section> getAssignedSections() {
        return Collections.unmodifiableList(assignedSections);
    }

    // Setter methods
    public void setDepartment(String department) {
        this.department = department;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    
    public void addAssignedSection(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }
        assignedSections.add(section);
    }

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
