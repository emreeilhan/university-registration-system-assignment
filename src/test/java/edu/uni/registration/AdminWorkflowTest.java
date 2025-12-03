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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminWorkflowTest {

    private CatalogService catalogService;
    private RegistrationService registrationService;
    private CourseRepository courseRepo;
    private SectionRepository sectionRepo;
    private PersonRepository personRepo;
    private StudentRepository studentRepo;
    private TranscriptRepository transcriptRepo;

    @BeforeEach
    void setUp() {
        courseRepo = new CourseRepository();
        sectionRepo = new SectionRepository();
        personRepo = new PersonRepository();
        studentRepo = new StudentRepository();
        transcriptRepo = new TranscriptRepository();

        catalogService = new CatalogServiceImpl(courseRepo, sectionRepo, personRepo);
        
        PrerequisiteValidator prereqVal = new PrerequisiteValidator();
        EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
        registrationService = new RegistrationServiceImpl(studentRepo, sectionRepo, prereqVal, transcriptRepo, personRepo, enrollmentRepo);
        
        // Setup Admin
        Admin admin = new Admin("A1", "Admin", "User", "admin@uni.edu");
        personRepo.save(admin);
    }

    @Test
    void shouldCreateCourseAndSection_whenAdminRequestsCreation() {
        Result<Course> cRes = catalogService.createCourse("CS500", "Advanced AI", 3);
        assertTrue(cRes.isOk());
        assertEquals("CS500", cRes.get().getCode());

        Result<Section> sRes = catalogService.createSection("SEC-AI", cRes.get(), "Fall 2024", 30);
        assertTrue(sRes.isOk());
        assertEquals("SEC-AI", sRes.get().getId());
        assertTrue(sectionRepo.findById("SEC-AI").isPresent());
    }

    @Test
    void shouldAssignInstructor_whenValidSectionAndInstructor() {
        Course c = new Course("CS101", "Intro", 3);
        courseRepo.save(c);
        Section s = new Section("S1", c, "Fall", 30);
        sectionRepo.save(s);

        Instructor i = new Instructor("I1", "Alice", "Smith", "a@uni.edu", "CS", "101");
        personRepo.save(i);

        Result<Void> res = catalogService.assignInstructor("S1", "I1");
        
        assertTrue(res.isOk());
        assertEquals(i, s.getInstructor());
        assertTrue(i.getAssignedSections().contains(s));
    }

    @Test
    void shouldEnrollStudent_whenAdminOverridesPrerequisites() {
        Course c1 = new Course("CS101", "Intro", 3);
        Course c2 = new Course("CS102", "Advanced", 3);
        c2.addPrerequisite("CS101");
        courseRepo.save(c1); 
        courseRepo.save(c2);

        Section s = new Section("SEC-ADV", c2, "Fall", 10);
        sectionRepo.save(s);

        Student student = new Student("S1", "John", "Doe", "j@uni.edu", "CS", 1);
        studentRepo.save(student);
        transcriptRepo.save(new Transcript(student));

        Result<Enrollment> normalRes = registrationService.enrollStudentInSection("S1", "SEC-ADV");
        assertTrue(normalRes.isFail(), "Normal enrollment should fail - prereqs not met");
        assertEquals("Prereqs not met", normalRes.getError());

        Result<Enrollment> forceRes = registrationService.adminOverrideEnroll("S1", "SEC-ADV", "A1", "Dean approved");
        assertTrue(forceRes.isOk(), "Admin override should succeed");
        assertEquals(Enrollment.EnrollmentStatus.ENROLLED, forceRes.get().getStatus());
        assertEquals(1, s.getRoster().size());
    }
}




