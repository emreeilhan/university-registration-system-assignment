package edu.uni.registration.model;

import java.util.Optional;

/**
 * Interface for entities that can be graded.
 */
public interface Gradable {
    void assignGrade(Grade grade);
    Optional<Grade> getGrade();
    boolean hasGrade();
}


