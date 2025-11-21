package edu.uni.registration.model;

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

    
    public abstract String role();
    
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
