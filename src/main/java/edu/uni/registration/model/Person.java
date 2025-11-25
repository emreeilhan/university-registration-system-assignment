package edu.uni.registration.model;

/**
 * Abstract base class for all system users (Student, Instructor, Admin).
 * Keeps common fields like name and email to avoid duplication.
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
     * Returns the role of this person (STUDENT, INSTRUCTOR, or ADMIN).
     *
     * @return a string representing the person's role
     */
    public abstract String role();
    
    /**
     * Returns a formatted string showing the person's profile information.
     * Each subclass provides different details based on their role.
     *
     * @return a formatted profile string
     */
    public abstract String displayProfile();

    // Getters and Setters - standard boilerplate
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
