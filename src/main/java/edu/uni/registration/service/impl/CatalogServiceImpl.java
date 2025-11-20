package edu.uni.registration.service.impl;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Section;
import edu.uni.registration.repository.CourseRepository;
import edu.uni.registration.repository.SectionRepository;
import edu.uni.registration.repository.PersonRepository;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.AdminOverrideLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public List<Course> search(CourseQuery query) {
        List<Course> all = courseRepository.findAll();
        if (query == null) {
            return all;
        }
        List<Course> result = new ArrayList<>();
        for (Course c : all) {
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
            if (ok) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public Course createCourse(String code, String title, int credits) {
        Course c = new Course(code, title, credits);
        return courseRepository.save(c);
    }

    @Override
    public Section createSection(String id, Course course, String term, int capacity) {
        Section s = new Section(id, course, term, capacity);
        return sectionRepository.save(s);
    }

    @Override
    public void assignInstructor(String sectionId, Instructor instructor) {
        Section s = sectionRepository.findById(sectionId).orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));
        s.setInstructor(instructor);
        if (instructor != null) {
            instructor.addAssignedSection(s);
        }
    }

    @Override
    public void adminOverrideCapacity(String sectionId, int newCapacity, String adminId, String reason) {
        if (adminId == null || adminId.isBlank()) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
        personRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found: " + adminId));
        
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));
        
        if (newCapacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
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
        System.out.println(log);
    }

    public List<AdminOverrideLog> getOverrideLogs() {
        return new ArrayList<>(overrideLogs);
    }
}


