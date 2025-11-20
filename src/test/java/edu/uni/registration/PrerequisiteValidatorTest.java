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
    void testPrerequisitePassed() {
        Course prereq = new Course("MATH101", "Calc I", 4);
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");
        
        Section prereqSection = new Section("SEC1", prereq, "Fall", 30);
        transcript.addEntry(new TranscriptEntry(prereqSection, Grade.C)); // C is >= 2.0

        assertTrue(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void testPrerequisiteFailed() {
        Course prereq = new Course("MATH101", "Calc I", 4);
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");
        
        Section prereqSection = new Section("SEC1", prereq, "Fall", 30);
        transcript.addEntry(new TranscriptEntry(prereqSection, Grade.D)); // D is < 2.0 (usually) or check Grade enum

        // Checking Grade enum: D is 1.0. The validator checks >= 2.0
        assertFalse(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void testEmptyPrerequisites() {
        Course target = new Course("MATH101", "Calc I", 4);
        assertTrue(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void testPrerequisiteNotTaken() {
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");
        
        assertFalse(validator.hasCompletedPrerequisites(transcript, target));
    }

    @Test
    void testAdminOverride() {
        Course target = new Course("MATH102", "Calc II", 4);
        target.addPrerequisite("MATH101");

        // Transcript is empty (prereq not taken), but adminOverride is true
        assertTrue(validator.hasCompletedPrerequisites(transcript, target, true),
                "Should allow registration if admin override is active");
    }
}


