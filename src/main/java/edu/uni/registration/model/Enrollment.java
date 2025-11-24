package edu.uni.registration.model;
import java.util.Optional;

/**
 * Represents a student's enrollment in a section.
 * Tracks the enrollment status (enrolled, dropped, or waitlisted) and the grade if assigned.
 * 
 * Implements Gradable interface to enable polymorphic grade assignment.
 */
public class Enrollment implements Gradable {
    private final Student student;
    private final Section section;

    private EnrollmentStatus status;
    private Optional<Grade> grade;

    /**
     * The possible statuses for an enrollment.
     */
    public enum EnrollmentStatus {
        /** Student is actively enrolled in the section */
        ENROLLED,
        /** Student dropped the section */
        DROPPED,
        /** Student is on the waitlist */
        WAITLISTED
    }

    /**
     * Creates a new enrollment linking a student to a section.
     * The enrollment starts with ENROLLED status and no grade.
     *
     * @param student the student being enrolled
     * @param section the section the student is enrolling in
     * @throws IllegalArgumentException if student or section is null
     */
    public Enrollment(Student student, Section section) {
        if (student == null || section == null)
            throw new IllegalArgumentException("Student and section cannot be null.");
        this.student = student;
        this.section = section;
        this.status = EnrollmentStatus.ENROLLED;
        this.grade = Optional.empty();
    }
    /**
     * Gets the student in this enrollment.
     *
     * @return the student object
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Gets the section in this enrollment.
     *
     * @return the section object
     */
    public Section getSection() {
        return section;
    }

    /**
     * Gets the current status of this enrollment.
     *
     * @return the enrollment status
     */
    public EnrollmentStatus getStatus() {
        return status;
    }

    /**
     * Gets the grade assigned to this enrollment, if any.
     *
     * @return an Optional containing the grade, or empty if no grade assigned
     */
    public Optional<Grade> getGrade() {
        return grade;
    }

    /**
     * Sets the status of this enrollment.
     *
     * @param status the new status
     * @throws IllegalArgumentException if status is null
     */
    public void setStatus(EnrollmentStatus status) {
        if(status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    /**
     * Assigns a grade to this enrollment.
     *
     * @param grade the grade to assign (can be null to remove grade)
     */
    public void assignGrade(Grade grade) {
        this.grade = Optional.ofNullable(grade);
    }

    /**
     * Checks if a grade has been assigned to this enrollment.
     *
     * @return true if a grade exists, false otherwise
     */
    public boolean hasGrade() {
        return grade.isPresent();
    }
    @Override
    public String toString() {
        return "Enrollment{" +
                "student=" + (student != null ? student.getId() : "N/A") +
                ", section=" + (section != null ? section.getId() : "N/A") +
                ", status=" + status +
                ", grade=" + grade.orElse(null) +
                '}';
    }
}
