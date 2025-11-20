package edu.uni.registration;

import edu.uni.registration.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranscriptTest {

    private Student student;
    private Transcript transcript;
    private Course course1;
    private Course course2;
    private Section section1;
    private Section section2;

    @BeforeEach
    void setUp() {
        student = new Student("S1", "Jane", "Doe", "jane@uni.edu", "CS", 1);
        transcript = student.getTranscript();
        course1 = new Course("CS101", "Intro", 3);
        course2 = new Course("CS102", "Advanced", 4);
        section1 = new Section("SEC1", course1, "Fall", 30);
        section2 = new Section("SEC2", course2, "Spring", 30);
    }

    @Test
    void testCalculateGPA() {
        transcript.addEntry(new TranscriptEntry(section1, Grade.A)); // 4.0 * 3 = 12
        transcript.addEntry(new TranscriptEntry(section2, Grade.B)); // 3.0 * 4 = 12
        
        // Total points: 24, Total credits: 7
        // GPA: 24 / 7 = 3.428...
        
        assertEquals(3.43, transcript.getGpa(), 0.01);
    }

    @Test
    void testExcludeIncomplete() {
        transcript.addEntry(new TranscriptEntry(section1, Grade.A));
        transcript.addEntry(new TranscriptEntry(section2, Grade.I));
        
        assertEquals(4.0, transcript.getGpa());
        assertEquals(3, transcript.getTotalCredits());
    }

    @Test
    void testEmptyTranscript() {
        assertEquals(0.0, transcript.getGpa());
        assertEquals(0, transcript.getTotalCredits());
    }
}


