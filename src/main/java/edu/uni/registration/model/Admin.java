package edu.uni.registration.model;

/**
 * Represents an administrator in the university system.
 * Admins have special privileges like overriding capacity limits
 * and bypassing prerequisite checks.
 */
public class Admin extends Person {
    /**
     * Creates a new admin with the given information.
     *
     * @param id the unique admin ID
     * @param firstName the admin's first name
     * @param lastName the admin's last name
     * @param email the admin's email
     */
    public Admin(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    /**
     * Returns the role of this person, which is always "ADMIN".
     *
     * @return the string "ADMIN"
     */
    @Override
    public String role() {
        return "ADMIN";
    }
    
    /**
     * Returns a formatted string showing the admin's profile
     * with full access permissions.
     *
     * @return a formatted profile string
     */
    @Override
    public String displayProfile() {
        return String.format("Admin Profile: %s\nRole: System Administrator\nPermissions: Full Access",
                getFullName());
    }
    
    @Override
    public String toString() {
        return "Admin -> " + super.toString();
    }

}
