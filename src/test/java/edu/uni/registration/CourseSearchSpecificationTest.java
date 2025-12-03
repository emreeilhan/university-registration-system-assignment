package edu.uni.registration;

import edu.uni.registration.model.*;
import edu.uni.registration.repository.CourseRepository;
import edu.uni.registration.repository.PersonRepository;
import edu.uni.registration.repository.SectionRepository;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.service.impl.CatalogServiceImpl;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseSearchSpecificationTest {

    private CourseRepository courseRepo;
    private SectionRepository sectionRepo;
    private PersonRepository personRepo;
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        courseRepo = new CourseRepository();
        sectionRepo = new SectionRepository();
        personRepo = new PersonRepository();
        catalogService = new CatalogServiceImpl(courseRepo, sectionRepo, personRepo);

        // Instructors
        Instructor alice = new Instructor("I1", "Alice", "Smith", "a@uni.edu", "CS", "101");
        Instructor bob = new Instructor("I2", "Bob", "Brown", "b@uni.edu", "Math", "202");
        personRepo.save(alice);
        personRepo.save(bob);

        // Courses
        Course cs101 = new Course("CS101", "Intro to CS", 3);
        Course math201 = new Course("MATH201", "Linear Algebra", 4);
        courseRepo.save(cs101);
        courseRepo.save(math201);

        // Sections with meeting times and instructors
        Section csSection = new Section("CS101-01", cs101, "Fall", 30);
        csSection.setInstructor(alice);
        csSection.addMeetingTime(new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "101"));
        sectionRepo.save(csSection);

        Section mathSection = new Section("MATH201-01", math201, "Fall", 30);
        mathSection.setInstructor(bob);
        mathSection.addMeetingTime(new TimeSlot(DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(12, 30), "202"));
        sectionRepo.save(mathSection);
    }

    @Test
    void shouldFilterCourses_byInstructorName() {
        CourseQuery query = new CourseQuery();
        query.setInstructorName("alice");

        Result<List<Course>> res = catalogService.search(query);
        
        assertTrue(res.isOk());
        assertEquals(1, res.get().size());
        assertEquals("CS101", res.get().get(0).getCode());
    }

    @Test
    void shouldFilterCourses_byCreditsAndTimeWindow() {
        CourseQuery query = new CourseQuery();
        query.setMinCredits(4);
        query.setDayOfWeek(DayOfWeek.THURSDAY);
        query.setStartTime(LocalTime.of(10, 30));
        query.setEndTime(LocalTime.of(12, 30));

        Result<List<Course>> res = catalogService.search(query);
        
        assertTrue(res.isOk());
        assertEquals(1, res.get().size());
        assertEquals("MATH201", res.get().get(0).getCode());
    }
}
