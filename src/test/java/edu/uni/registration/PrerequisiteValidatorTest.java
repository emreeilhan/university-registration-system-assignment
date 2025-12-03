package edu.uni.registration;

import edu.uni.registration.model.*;
import edu.uni.registration.validation.PrerequisiteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrerequisiteValidatorTest {

    private PrerequisiteValidator validator;
    private Student student;
    private Transcript transcript;

    @BeforeEach
    void setUp() {
        validator = new PrerequisiteValidator();
        student = new Student("S1", "Bob", "Smith", "bob@uni.edu", "Math", 2);
        transcript = student.getTranscript();
    }

    @Test
    void shouldReturnTrue_whenPrerequisitePassedWithGradeC() {
        Course prereq = new Course("MATH101", "Calc I", 4);
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");
        
        Section prereqSection = new Section("SEC1", prereq, "Fall", 30);
        transcript.addEntry(new TranscriptEntry(prereqSection, Grade.C));

        assertTrue(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void shouldReturnFalse_whenPrerequisiteFailedWithGradeD() {
        Course prereq = new Course("MATH101", "Calc I", 4);
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");
        
        Section prereqSection = new Section("SEC1", prereq, "Fall", 30);
        transcript.addEntry(new TranscriptEntry(prereqSection, Grade.D));

        assertFalse(validator.hasCompletedPrerequisites(transcript, target),
                "D grade (1.0 points) should not satisfy prerequisite requirement (>= 2.0)");
    }

    @Test
    void shouldReturnTrue_whenCourseHasNoPrerequisites() {
        Course target = new Course("MATH101", "Calc I", 4);
        assertTrue(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void shouldReturnFalse_whenPrerequisiteNotTaken() {
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");
        
        assertFalse(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void shouldReturnTrue_whenAdminOverrideIsActive() {
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");

        assertTrue(validator.hasCompletedPrerequisites(transcript, target, true),
                "Admin override should bypass prerequisite validation");
    }
}


