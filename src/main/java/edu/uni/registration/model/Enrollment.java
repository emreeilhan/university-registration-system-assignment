package edu.uni.registration.model;
import java.util.Optional;

public class Enrollment {
    private final Student student;
    private final Section section;

    private EnrollmentStatus status;
    private Optional<Grade> grade;

    public enum EnrollmentStatus {
        ENROLLED,
        DROPPED,
        WAITLISTED
    }

    public Enrollment(Student student, Section section) {
        if (student == null || section == null)
            throw new IllegalArgumentException("Student and section cannot be null.");
        this.student = student;
        this.section = section;
        this.status = EnrollmentStatus.ENROLLED;
        this.grade = Optional.empty();
    }
    //Getter methods
    public Student getStudent() {
        return student;
    }
    public Section getSection() {
        return section;
    }
    public EnrollmentStatus getStatus() {
        return status;
    }
    public Optional<Grade> getGrade() {
        return grade;
    }
    //Setter and helper methods
    public void setStatus(EnrollmentStatus status) {
        if(status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    public void assignGrade(Grade grade) {
        this.grade = Optional.ofNullable(grade);
    }

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
