package edu.uni.registration.service.impl;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Person;
import edu.uni.registration.model.Section;
import edu.uni.registration.repository.CourseRepository;
import edu.uni.registration.repository.SectionRepository;
import edu.uni.registration.repository.PersonRepository;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.CourseSpecification;
import edu.uni.registration.util.Result;
import edu.uni.registration.util.AdminOverrideLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CatalogService interface.
 * Handles course and section creation, searching, instructor assignment, and admin overrides.
 */
public class CatalogServiceImpl implements CatalogService {
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final PersonRepository personRepository;
    private final List<AdminOverrideLog> overrideLogs;

    /**
     * Creates a new CatalogServiceImpl with the required repositories.
     *
     * @param courseRepository repository for course data
     * @param sectionRepository repository for section data
     * @param personRepository repository for person data
     * @throws IllegalArgumentException if any parameter is null
     */
    public CatalogServiceImpl(CourseRepository courseRepository, SectionRepository sectionRepository, PersonRepository personRepository) {
        if (courseRepository == null || sectionRepository == null || personRepository == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.personRepository = personRepository;
        this.overrideLogs = new ArrayList<>();
    }

    @Override
    public Result<List<Course>> search(CourseQuery query) {
        List<Course> allCourses = courseRepository.findAll();
        
        // Convert CourseQuery to Specification pattern
        CourseSpecification specification = CourseSpecification.fromQuery(query, sectionRepository);
        
        if (specification == null) {
            return Result.ok(allCourses);
        }

        // Filter courses using Specification pattern
        List<Course> filteredCourses = new ArrayList<>();
        for (Course course : allCourses) {
            if (specification.isSatisfiedBy(course)) {
                filteredCourses.add(course);
            }
        }

        return Result.ok(filteredCourses);
    }


    @Override
    public Result<Course> createCourse(String code, String title, int credits) {
        if (code == null || title == null) return Result.fail("Code and Title cannot be null");
        if (credits <= 0) return Result.fail("Credits must be positive");
        
        Course c = new Course(code, title, credits);
        courseRepository.save(c);
        return Result.ok(c);
    }

    @Override
    public Result<Section> createSection(String id, Course course, String term, int capacity) {
        if (id == null || course == null || term == null) return Result.fail("Section details cannot be null");
        if (capacity < 0) return Result.fail("Capacity cannot be negative");
        
        Section s = new Section(id, course, term, capacity);
        sectionRepository.save(s);
        return Result.ok(s);
    }

    @Override
    public Result<Void> assignInstructor(String sectionId, String instructorId) {
        if (sectionId == null || instructorId == null) return Result.fail("IDs cannot be null");
        
        Optional<Person> pOpt = personRepository.findById(instructorId);
        if (pOpt.isEmpty()) return Result.fail("Instructor not found: " + instructorId);
        
        Person p = pOpt.get();
        if (!(p instanceof Instructor)) return Result.fail("User is not an instructor");
        Instructor instructor = (Instructor) p;
        
        return sectionRepository.findById(sectionId)
                .map(s -> {
                    s.setInstructor(instructor);
                    instructor.addAssignedSection(s);
                    return Result.<Void>ok(null);
                })
                .orElse(Result.fail("Section not found: " + sectionId));
    }

    @Override
    public Result<Void> adminOverrideCapacity(String sectionId, int newCapacity, String adminId, String reason) {
        if (adminId == null || adminId.isBlank()) {
            return Result.fail("Admin ID cannot be null");
        }
        if (personRepository.findById(adminId).isEmpty()) {
            return Result.fail("Admin not found: " + adminId);
        }
        
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    if (newCapacity < 0) {
                        return Result.<Void>fail("Capacity cannot be negative");
                    }
                    int oldCapacity = section.getCapacity();
                    section.setCapacity(newCapacity);

                    AdminOverrideLog log = new AdminOverrideLog(
                            adminId,
                            "CAPACITY_OVERRIDE: " + oldCapacity + " -> " + newCapacity,
                            sectionId,
                            reason != null ? reason : "No reason provided"
                    );
                    overrideLogs.add(log);
                    return Result.<Void>ok(null);
                })
                .orElse(Result.fail("Section not found: " + sectionId));
    }

    @Override
    public Result<List<Section>> getInstructorSections(String instructorId) {
        if (instructorId == null || instructorId.isBlank()) {
            return Result.fail("Instructor ID cannot be null");
        }
        return personRepository.findById(instructorId)
                .map(person -> {
                    if (person instanceof Instructor) {
                        return Result.ok(((Instructor) person).getAssignedSections());
                    } else {
                        return Result.<List<Section>>fail("User is not an instructor");
                    }
                })
                .orElse(Result.fail("Instructor not found"));
    }

    /**
     * Gets a copy of all admin override logs for audit purposes.
     *
     * @return a list of admin override logs
     */
    public List<AdminOverrideLog> getOverrideLogs() {
        return new ArrayList<>(overrideLogs);
    }

    @Override
    public Result<Course> updateCourse(String code, String newTitle, Integer newCredits) {
        // Purpose: Provide a minimal edit capability for Admin over existing courses
        if (code == null || code.isBlank()) {
            return Result.fail("Course code cannot be null");
        }
        Optional<Course> cOpt = courseRepository.findById(code);
        if (cOpt.isEmpty()) {
            return Result.fail("Course not found: " + code);
        }

        Course course = cOpt.get();

        boolean changed = false;
        if (newTitle != null && !newTitle.isBlank()) {
            course.setTitle(newTitle);
            changed = true;
        }
        if (newCredits != null) {
            if (newCredits <= 0) {
                return Result.fail("Credits must be positive");
            }
            course.setCredits(newCredits);
            changed = true;
        }
        if (!changed) {
            return Result.fail("No changes provided");
        }

        // Persist updated entity
        courseRepository.save(course);
        return Result.ok(course);
    }
}
