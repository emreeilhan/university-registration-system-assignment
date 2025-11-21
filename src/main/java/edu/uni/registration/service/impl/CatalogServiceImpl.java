package edu.uni.registration.service.impl;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Person;
import edu.uni.registration.model.Section;
import edu.uni.registration.model.TimeSlot;
import edu.uni.registration.repository.CourseRepository;
import edu.uni.registration.repository.SectionRepository;
import edu.uni.registration.repository.PersonRepository;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;
import edu.uni.registration.util.AdminOverrideLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CatalogServiceImpl implements CatalogService {
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final PersonRepository personRepository;
    private final List<AdminOverrideLog> overrideLogs;

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
        if (query == null) {
            return Result.ok(allCourses);
        }

        // Basic filtering on Course fields
        List<Course> filteredCourses = allCourses.stream()
                .filter(c -> matchesCourseFields(c, query))
                .collect(Collectors.toList());

        // Advanced filtering (Instructor, Time) requires checking Sections
        if (hasSectionCriteria(query)) {
            Set<String> validCourseCodes = findCourseCodesMatchingSectionCriteria(query);
            filteredCourses = filteredCourses.stream()
                    .filter(c -> validCourseCodes.contains(c.getCode()))
                    .collect(Collectors.toList());
        }

        return Result.ok(filteredCourses);
    }

    private boolean matchesCourseFields(Course c, CourseQuery query) {
        boolean ok = true;
        if (query.getCode() != null && !query.getCode().isBlank()) {
            ok &= c.getCode() != null && c.getCode().toLowerCase(Locale.ROOT).contains(query.getCode().toLowerCase(Locale.ROOT));
        }
        if (query.getTitle() != null && !query.getTitle().isBlank()) {
            ok &= c.getTitle() != null && c.getTitle().toLowerCase(Locale.ROOT).contains(query.getTitle().toLowerCase(Locale.ROOT));
        }
        if (query.getMinCredits() != null) {
            ok &= c.getCredits() >= query.getMinCredits();
        }
        if (query.getMaxCredits() != null) {
            ok &= c.getCredits() <= query.getMaxCredits();
        }
        return ok;
    }

    private boolean hasSectionCriteria(CourseQuery query) {
        return (query.getInstructorName() != null && !query.getInstructorName().isBlank()) ||
               query.getDayOfWeek() != null ||
               query.getStartTime() != null ||
               query.getEndTime() != null;
    }

    private Set<String> findCourseCodesMatchingSectionCriteria(CourseQuery query) {
        List<Section> allSections = sectionRepository.findAll();
        Set<String> matchingCourseCodes = new HashSet<>();

        for (Section s : allSections) {
            boolean matches = true;

            // Check Instructor
            if (query.getInstructorName() != null && !query.getInstructorName().isBlank()) {
                if (s.getInstructor() == null || !s.getInstructor().getFullName().toLowerCase(Locale.ROOT)
                        .contains(query.getInstructorName().toLowerCase(Locale.ROOT))) {
                    matches = false;
                }
            }

            // Check Time Window
            if (matches && (query.getDayOfWeek() != null || query.getStartTime() != null || query.getEndTime() != null)) {
                boolean timeMatch = false;
                for (TimeSlot slot : s.getMeetingTimes()) {
                    boolean slotOk = true;
                    if (query.getDayOfWeek() != null && slot.getDayOfWeek() != query.getDayOfWeek()) {
                        slotOk = false;
                    }
                    // Check if slot is within the requested window
                    if (query.getStartTime() != null && slot.getStart().isBefore(query.getStartTime())) {
                         slotOk = false;
                    }
                    if (query.getEndTime() != null && slot.getEnd().isAfter(query.getEndTime())) {
                         slotOk = false;
                    }
                    
                    if (slotOk) {
                        timeMatch = true;
                        break;
                    }
                }
                if (!timeMatch) {
                    matches = false;
                }
            }

            if (matches) {
                matchingCourseCodes.add(s.getCourse().getCode());
            }
        }
        return matchingCourseCodes;
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
    public Result<Void> assignInstructor(String sectionId, Instructor instructor) {
        if (sectionId == null) return Result.fail("Section ID cannot be null");
        
        return sectionRepository.findById(sectionId)
                .map(s -> {
                    s.setInstructor(instructor);
                    if (instructor != null) {
                        instructor.addAssignedSection(s);
                    }
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

    public List<AdminOverrideLog> getOverrideLogs() {
        return new ArrayList<>(overrideLogs);
    }
}
