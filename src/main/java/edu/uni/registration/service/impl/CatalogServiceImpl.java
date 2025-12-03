package edu.uni.registration.service.impl;

import edu.uni.registration.model.Admin;
import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Section;
import edu.uni.registration.repository.CourseRepository;
import edu.uni.registration.repository.SectionRepository;
import edu.uni.registration.repository.PersonRepository;
import edu.uni.registration.service.CatalogService;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;
import edu.uni.registration.util.AdminOverrideLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Course catalog management: CRUD, search, instructor assignment.
 */
public class CatalogServiceImpl implements CatalogService {
    private final CourseRepository courseRepo;
    private final SectionRepository sectionRepo;
    private final PersonRepository personRepo;
    private final List<AdminOverrideLog> logs;

    public CatalogServiceImpl(CourseRepository courseRepo, SectionRepository sectionRepo, PersonRepository personRepo) {
        this.courseRepo = courseRepo;
        this.sectionRepo = sectionRepo;
        this.personRepo = personRepo;
        this.logs = new ArrayList<>();
    }

    @Override
    public Result<List<Course>> search(CourseQuery query) {
        List<Course> all = courseRepo.findAll();
        if (query == null) {
            return Result.ok(all);
        }

        query.setSectionRepository(sectionRepo);
        
        List<Course> result = new ArrayList<>();
        for (Course c : all) {
            if (query.isSatisfiedBy(c)) {
                result.add(c);
            }
        }
        return Result.ok(result);
    }


    @Override
    public Result<Course> createCourse(String code, String title, int credits) {
        if (code == null || title == null) return Result.fail("Missing info");
        
        Course c = new Course(code, title, credits);
        courseRepo.save(c);
        return Result.ok(c);
    }

    @Override
    public Result<Section> createSection(String id, Course course, String term, int capacity) {
        if (id == null || course == null) return Result.fail("Missing info");
        
        Section s = new Section(id, course, term, capacity);
        sectionRepo.save(s);
        return Result.ok(s);
    }

    @Override
    public Result<Void> assignInstructor(String secId, String insId) {
        var pOpt = personRepo.findById(insId);
        if (pOpt.isEmpty()) return Result.fail("Instructor not found");
        
        if (!(pOpt.get() instanceof Instructor)) return Result.fail("Not an instructor");
        Instructor ins = (Instructor) pOpt.get();
        
        var secOpt = sectionRepo.findById(secId);
        if (secOpt.isEmpty()) return Result.fail("Section not found");
        
        Section s = secOpt.get();
        s.setInstructor(ins);
        ins.addAssignedSection(s);
        
        return Result.ok(null);
    }

    @Override
    public Result<Void> adminOverrideCapacity(String secId, int newCap, String adminId, String reason) {
        var adminOpt = personRepo.findById(adminId);
        if (adminOpt.isEmpty() || !(adminOpt.get() instanceof Admin)) {
            return Result.fail("Invalid admin");
        }
        
        var secOpt = sectionRepo.findById(secId);
        if (secOpt.isEmpty()) return Result.fail("Section not found");
        Section s = secOpt.get();

        int old = s.getCapacity();
        s.setCapacity(newCap);

        logs.add(new AdminOverrideLog(adminId, "CAPACITY: " + old + "->" + newCap, secId, reason));
        return Result.ok(null);
    }

    @Override
    public Result<List<Section>> getInstructorSections(String insId) {
        var pOpt = personRepo.findById(insId);
        if (pOpt.isEmpty()) return Result.fail("Not found");
        
        if (pOpt.get() instanceof Instructor) {
            return Result.ok(((Instructor) pOpt.get()).getAssignedSections());
        }
        return Result.fail("Not an instructor");
    }

    public List<AdminOverrideLog> getOverrideLogs() {
        return new ArrayList<>(logs);
    }

    @Override
    public Result<Course> updateCourse(String code, String newTitle, Integer newCredits) {
        var cOpt = courseRepo.findById(code);
        if (cOpt.isEmpty()) return Result.fail("Course not found");

        Course c = cOpt.get();
        if (newTitle != null) c.setTitle(newTitle);
        if (newCredits != null) c.setCredits(newCredits);

        courseRepo.save(c);
        return Result.ok(c);
    }

    @Override
    public Result<List<Section>> getSectionsByCourseCode(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return Result.fail("Course code required");
        }
        List<Section> matches = new ArrayList<>();
        for (Section s : sectionRepo.findAll()) {
            if (s.getCourse().getCode().equalsIgnoreCase(courseCode)) {
                matches.add(s);
            }
        }
        return Result.ok(matches);
    }
}
