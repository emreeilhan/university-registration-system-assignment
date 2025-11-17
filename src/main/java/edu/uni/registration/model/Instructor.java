package edu.uni.registration.model;

public class Instructor extends Person {
    private String department;
    private String officeNumber;

    public Instructor(String id, String firstName, String lastName, String email,
                      String department, String officeNumber) {
        super(id, firstName, lastName, email);
        this.department = department;
        this.officeNumber = officeNumber;
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

    // Setter methods
    public void setDepartment(String department) {
        this.department = department;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Department: " + department +
                ", Office: " + officeNumber;
    }
}
