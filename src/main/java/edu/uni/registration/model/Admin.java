package edu.uni.registration.model;

public class Admin extends Person {
    public Admin(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public String role() {
        return "ADMIN";
    }
    @Override
    public String toString() {
        return "Admin -> " + super.toString();
    }

}
