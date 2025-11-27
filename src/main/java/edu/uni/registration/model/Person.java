package edu.uni.registration.model;

/**
 * Base class for all users (Student, Instructor, Admin).
 * Just holds common stuff like name and email.
 */
public abstract class Person {

    private final String id;
    private String firstName;
    private String lastName;
    private String email;

    public Person(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the person's role (STUDENT, INSTRUCTOR, or ADMIN).
     */
    public abstract String role();
    
    /**
     * Returns a formatted profile string. Each subclass formats it differently.
     */
    public abstract String displayProfile();

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Name: " + getFullName() +
                ", Email: " + email;
    }
}
