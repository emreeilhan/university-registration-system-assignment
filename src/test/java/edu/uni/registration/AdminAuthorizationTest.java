package edu.uni.registration;

import edu.uni.registration.model.*;
import edu.uni.registration.repository.*;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.service.RegistrationService;
import edu.uni.registration.service.impl.CatalogServiceImpl;
import edu.uni.registration.service.impl.RegistrationServiceImpl;
import edu.uni.registration.util.Result;
import edu.uni.registration.validation.PrerequisiteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AdminAuthorizationTest {

    private CatalogService catalogService;
    private RegistrationService registrationService;
    private PersonRepository personRepo;
    private SectionRepository sectionRepo;
    private StudentRepository studentRepo;
    private TranscriptRepository transcriptRepo;
    private EnrollmentRepository enrollmentRepo;

    @BeforeEach
    void setUp() {
        CourseRepository courseRepo = new CourseRepository();
        sectionRepo = new SectionRepository();
        personRepo = new PersonRepository();
        studentRepo = new StudentRepository();
        transcriptRepo = new TranscriptRepository();
        enrollmentRepo = new EnrollmentRepository();

        catalogService = new CatalogServiceImpl(courseRepo, sectionRepo, personRepo);
        registrationService = new RegistrationServiceImpl(studentRepo, sectionRepo, new PrerequisiteValidator(), transcriptRepo, personRepo, enrollmentRepo);

        // People
        Admin admin = new Admin("A1", "Real", "Admin", "admin@uni.edu");
        Instructor instructor = new Instructor("I1", "Not", "Admin", "i@uni.edu", "CS", "101");
        Student student = new Student("S1", "Jane", "Doe", "s@uni.edu", "CS", 1);
        personRepo.save(admin);
        personRepo.save(instructor);
        personRepo.save(student);
        studentRepo.save(student);
        transcriptRepo.save(student.getTranscript());

        // Course/Section
        Course course = new Course("CS101", "Intro", 3);
        courseRepo.save(course);
        Section section = new Section("SEC-1", course, "Fall", 1);
        section.addMeetingTime(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "101"));
        sectionRepo.save(section);
    }

    @Test
    void nonAdminCannotOverrideEnrollment() {
        Result<Enrollment> res = registrationService.adminOverrideEnroll("S1", "SEC-1", "I1", "Nope");
        assertTrue(res.isFail());
        assertTrue(res.getError().toLowerCase().contains("admin"));
    }

    @Test
    void adminOverrideCapacityRequiresAdmin() {
        Result<Void> res = catalogService.adminOverrideCapacity("SEC-1", 10, "I1", "Nope");
        assertTrue(res.isFail());
        assertTrue(res.getError().toLowerCase().contains("admin"));
    }

    @Test
    void waitlistPromotionKeepsGradesAndStatus() {
        // Fill capacity
        Result<Enrollment> first = registrationService.enrollStudentInSection("S1", "SEC-1");
        assertTrue(first.isOk());
        assertEquals(Enrollment.EnrollmentStatus.ENROLLED, first.get().getStatus());

        // Add second student to waitlist
        Student waitlisted = new Student("S2", "Mike", "Mouse", "m@uni.edu", "Math", 1);
        studentRepo.save(waitlisted);
        personRepo.save(waitlisted);
        transcriptRepo.save(waitlisted.getTranscript());
        Result<Enrollment> second = registrationService.enrollStudentInSection("S2", "SEC-1");
        assertTrue(second.isOk());
        assertEquals(Enrollment.EnrollmentStatus.WAITLISTED, second.get().getStatus());
        assertTrue(sectionRepo.findById("SEC-1").orElseThrow().isFull());
        assertFalse(sectionRepo.findById("SEC-1").orElseThrow().isWaitlistFull());

        // Drop first, should auto-promote second
        Result<Void> dropRes = registrationService.dropStudentInSection("S1", "SEC-1");
        assertTrue(dropRes.isOk());
        Enrollment promoted = enrollmentRepo.findByStudentAndSection(waitlisted, sectionRepo.findById("SEC-1").orElseThrow()).orElseThrow();
        assertEquals(Enrollment.EnrollmentStatus.ENROLLED, promoted.getStatus());
    }
}
