package edu.uni.registration;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Student;
import edu.uni.registration.repository.CourseRepository;
import edu.uni.registration.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private CourseRepository courseRepo;
    private StudentRepository studentRepo;

    @BeforeEach
    void setUp() {
        courseRepo = new CourseRepository();
        studentRepo = new StudentRepository();
    }

    @Test
    void shouldSaveAndFindCourse_whenValidCourseProvided() {
        Course c = new Course("CS101", "Intro", 3);
        courseRepo.save(c);
        
        Optional<Course> found = courseRepo.findById("CS101");
        assertTrue(found.isPresent());
        assertEquals("Intro", found.get().getTitle());
    }

    @Test
    void shouldDeleteCourse_whenCourseExists() {
        Course c = new Course("CS101", "Intro", 3);
        courseRepo.save(c);
        courseRepo.deleteById("CS101");
        
        assertFalse(courseRepo.findById("CS101").isPresent());
    }

    @Test
    void shouldSaveAndFindStudent_whenValidStudentProvided() {
        Student s = new Student("S1", "Ali", "Veli", "ali@uni.edu", "CS", 1);
        studentRepo.save(s);
        
        Optional<Student> found = studentRepo.findById("S1");
        assertTrue(found.isPresent());
        assertEquals("Ali", found.get().getFirstName());
    }

    @Test
    void shouldReturnEmpty_whenCourseDoesNotExist() {
        Optional<Course> c = courseRepo.findById("NONEXISTENT");
        assertTrue(c.isEmpty());
    }
}


