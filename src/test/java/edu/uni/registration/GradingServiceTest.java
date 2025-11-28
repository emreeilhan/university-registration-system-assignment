package edu.uni.registration;

import edu.uni.registration.model.*;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.GradingService;
import edu.uni.registration.service.impl.GradingServiceImpl;
import edu.uni.registration.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class GradingServiceTest {

    private StudentRepository studentRepo;
    private SectionRepository sectionRepo;
    private EnrollmentRepository enrollmentRepo;
    private TranscriptRepository transcriptRepo;
    private GradingService gradingService;

    @BeforeEach
    void setUp() {
        studentRepo = new StudentRepository();
        sectionRepo = new SectionRepository();
        enrollmentRepo = new EnrollmentRepository();
        transcriptRepo = new TranscriptRepository();
        gradingService = new GradingServiceImpl(studentRepo, sectionRepo, enrollmentRepo, transcriptRepo);
    }

    @Test
    void postGradeUpdatesEnrollmentAndTranscript() {
        Student student = new Student("S1", "Jane", "Doe", "j@uni.edu", "CS", 1);
        studentRepo.save(student);
        Course course = new Course("CS101", "Intro", 3);
        Section section = new Section("SEC-1", course, "Fall", 30);
        section.addMeetingTime(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "101"));
        sectionRepo.save(section);

        Enrollment enrollment = new Enrollment(student, section);
        enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        section.addEnrollment(enrollment);
        enrollmentRepo.save(enrollment);

        Result<Void> res = gradingService.postGrade("I1", "SEC-1", "S1", Grade.B);
        assertTrue(res.isOk());

        assertTrue(enrollment.getGrade().isPresent());
        assertEquals(Grade.B, enrollment.getGrade().get());

        Transcript transcript = transcriptRepo.findById("S1").orElseThrow();
        assertEquals(1, transcript.getEntries().size());
        assertEquals(3.0, transcript.getGpa());
    }

    @Test
    void postGradeFailsWhenNotEnrolled() {
        Student student = new Student("S2", "John", "Smith", "s@uni.edu", "Math", 2);
        studentRepo.save(student);
        Course course = new Course("CS102", "DS", 4);
        Section section = new Section("SEC-2", course, "Fall", 30);
        sectionRepo.save(section);

        Result<Void> res = gradingService.postGrade("I1", "SEC-2", "S2", Grade.A);
        assertTrue(res.isFail());
        assertTrue(res.getError().contains("Not enrolled"));
    }
}
