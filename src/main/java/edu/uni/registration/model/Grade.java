package edu.uni.registration.model;

/**
 * Letter grade enum. Each grade has point value and whether it counts towards GPA.
 */
public enum Grade {
    A(4.0, true),
    B(3.0, true),
    C(2.0, true),
    D(1.0, true),
    F(0.0, true),
    I(0.0, false),
    W(0.0, false); 

    private final double points;
    private final boolean countsTowardsGpa;

    Grade(double points, boolean countsTowardsGpa) {
        this.points = points;
        this.countsTowardsGpa = countsTowardsGpa;
    }

    public double getPoints() {
        return points;
    }

    public boolean countsTowardsGpa() {
        return countsTowardsGpa;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
