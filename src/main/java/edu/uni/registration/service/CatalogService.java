package edu.uni.registration.service;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Section;
import edu.uni.registration.util.CourseQuery;

import java.util.List;

public interface CatalogService {
    List<Course> search(CourseQuery query);
    Course createCourse(String code, String title, int credits);
    Section createSection(String id, Course course, String term, int capacity);
    void assignInstructor(String sectionId, Instructor instructor);
    void adminOverrideCapacity(String sectionId, int newCapacity, String adminId, String reason);
}


