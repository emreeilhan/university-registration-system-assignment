package edu.uni.registration;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Section;
import edu.uni.registration.model.Student;
import edu.uni.registration.validation.CapacityValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapacityValidatorTest {

    private final CapacityValidator validator = new CapacityValidator();

    @Test
    void shouldReturnFalse_whenSectionIsFull() {
        Course course = new Course("CS101", "Intro", 3);
        Section section = new Section("SEC1", course, "Fall", 1);
        Student student = new Student("S1", "John", "Doe", "email", "CS", 1);
        Enrollment enrollment = new Enrollment(student, section);
        
        section.addEnrollment(enrollment);
        
        assertFalse(validator.hasCapacity(section));
    }

    @Test
    void shouldReturnTrue_whenCapacityAvailable() {
        Course course = new Course("CS101", "Intro", 3);
        Section section = new Section("SEC1", course, "Fall", 5);
        
        assertTrue(validator.hasCapacity(section));
    }

    @Test
    void shouldReturnFalse_whenSectionIsNull() {
        assertFalse(validator.hasCapacity(null));
    }

    @Test
    void shouldManageWaitlistCapacity_whenSectionFullButWaitlistAvailable() {
        Course course = new Course("CS101", "Intro", 3);
        Section section = new Section("SEC1", course, "Fall", 1);
        section.setWaitlistCapacity(2);

        Student s1 = new Student("S1", "John", "Doe", "email", "CS", 1);
        Enrollment e1 = new Enrollment(s1, section);
        e1.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        section.addEnrollment(e1);

        assertFalse(validator.hasCapacity(section), "Section should be full");
        assertTrue(validator.hasWaitlistCapacity(section), "Waitlist should be available");

        Student s2 = new Student("S2", "Jane", "Doe", "email", "CS", 1);
        Enrollment e2 = new Enrollment(s2, section);
        e2.setStatus(Enrollment.EnrollmentStatus.WAITLISTED);
        section.addEnrollment(e2);

        assertTrue(validator.hasWaitlistCapacity(section), "Waitlist still has 1 spot");

        Student s3 = new Student("S3", "Jim", "Doe", "email", "CS", 1);
        Enrollment e3 = new Enrollment(s3, section);
        e3.setStatus(Enrollment.EnrollmentStatus.WAITLISTED);
        section.addEnrollment(e3);

        assertFalse(validator.hasWaitlistCapacity(section), "Waitlist should now be full");
    }
}
