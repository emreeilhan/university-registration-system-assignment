package edu.uni.registration.model;

import java.util.Optional;

/**
 * Interface for entities that can be assigned a grade.
 * Provides a common contract for gradable entities in the system.
 * 
 * This interface enables polymorphic grade assignment behavior,
 * allowing different entity types to be graded uniformly.
 */
public interface Gradable {
    /**
     * Assigns a grade to this gradable entity.
     * 
     * @param grade the grade to assign (can be null to remove grade)
     */
    void assignGrade(Grade grade);
    
    /**
     * Gets the assigned grade if present.
     * 
     * @return Optional containing the grade, or empty if not graded
     */
    Optional<Grade> getGrade();
    
    /**
     * Checks if a grade has been assigned.
     * 
     * @return true if graded, false otherwise
     */
    boolean hasGrade();
}

