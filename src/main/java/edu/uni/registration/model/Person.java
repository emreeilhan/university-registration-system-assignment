package edu.uni.registration.model;

/**
 * Base class for all people in the university system.
 * This includes students, instructors, and admins.
 * Each person has a unique ID, name, and email address.
 */
public abstract class Person {

    private final String id;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Creates a new person with the given information.
     *
     * @param id the unique identifier for this person
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param email the person's email address
     */
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

    // Getters and Setters
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
