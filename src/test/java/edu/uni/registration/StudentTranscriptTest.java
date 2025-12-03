package edu.uni.registration;

import edu.uni.registration.model.*;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.RegistrationService;
import edu.uni.registration.service.impl.RegistrationServiceImpl;
import edu.uni.registration.util.Result;
import edu.uni.registration.validation.PrerequisiteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTranscriptTest {

    private RegistrationService registrationService;
    private StudentRepository studentRepo;
    private TranscriptRepository transcriptRepo;
    private SectionRepository sectionRepo;
    
    @BeforeEach
    void setUp() {
        studentRepo = new StudentRepository();
        transcriptRepo = new TranscriptRepository();
        sectionRepo = new SectionRepository();
        PersonRepository personRepo = new PersonRepository(); // needed for constructor but not used here
        EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
        
        PrerequisiteValidator prereqVal = new PrerequisiteValidator();
        registrationService = new RegistrationServiceImpl(studentRepo, sectionRepo, prereqVal, transcriptRepo, personRepo, enrollmentRepo);
    }

    @Test
    void shouldReturnTranscript_whenStudentExists() {
        Student s = new Student("S1", "Jane", "Doe", "j@uni.edu", "Math", 2);
        studentRepo.save(s);
        
        Transcript t = new Transcript(s);
        Course c = new Course("MATH101", "Calc", 4);
        Section sec = new Section("OLD-SEC", c, "Last Year", 30);
        t.addEntry(new TranscriptEntry(sec, Grade.A));
        transcriptRepo.save(t);

        Result<Transcript> res = registrationService.getTranscript("S1");
        
        assertTrue(res.isOk());
        Transcript retrieved = res.get();
        assertEquals("S1", retrieved.getStudent().getId());
        assertEquals(4.0, retrieved.getGpa());
        assertEquals(1, retrieved.getEntries().size());
    }

    @Test
    void shouldFail_whenStudentDoesNotExist() {
        Result<Transcript> res = registrationService.getTranscript("NON-EXISTENT");
        assertTrue(res.isFail());
        assertTrue(res.getError().contains("Student not found"));
    }
}



