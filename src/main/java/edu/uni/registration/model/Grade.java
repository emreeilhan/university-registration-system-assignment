package edu.uni.registration.model;

/**
 * Represents a letter grade that can be assigned to a student.
 * Each grade has point value and indicates whether it counts towards GPA calculation.
 */
public enum Grade {
    /** Grade A: 4.0 points, counts towards GPA */
    A(4.0, true),
    /** Grade B: 3.0 points, counts towards GPA */
    B(3.0, true),
    /** Grade C: 2.0 points, counts towards GPA */
    C(2.0, true),
    /** Grade D: 1.0 points, counts towards GPA */
    D(1.0, true),
    /** Grade F: 0.0 points, counts towards GPA */
    F(0.0, true),
    /** Incomplete: 0.0 points, does not count towards GPA */
    I(0.0, false),
    /** Withdrawn: 0.0 points, does not count towards GPA */
    W(0.0, false); 

    private final double points;
    private final boolean countsTowardsGpa;

    /**
     * Creates a grade with the given point value and GPA flag.
     *
     * @param points the point value for GPA calculation
     * @param countsTowardsGpa whether this grade counts towards GPA
     */
    Grade(double points, boolean countsTowardsGpa) {
        this.points = points;
        this.countsTowardsGpa = countsTowardsGpa;
    }

    /**
     * Gets the point value for this grade (used in GPA calculation).
     *
     * @return the grade points
     */
    public double getPoints() {
        return points;
    }

    /**
     * Checks if this grade counts towards GPA calculation.
     *
     * @return true if it counts, false otherwise
     */
    public boolean countsTowardsGpa() {
        return countsTowardsGpa;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
