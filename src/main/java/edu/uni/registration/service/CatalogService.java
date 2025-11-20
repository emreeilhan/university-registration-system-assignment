package edu.uni.registration.service;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Instructor;
import edu.uni.registration.model.Section;
import edu.uni.registration.util.CourseQuery;
import edu.uni.registration.util.Result;

import java.util.List;

public interface CatalogService {
    Result<List<Course>> search(CourseQuery query);
    Result<Course> createCourse(String code, String title, int credits);
    Result<Section> createSection(String id, Course course, String term, int capacity);
    Result<Void> assignInstructor(String sectionId, Instructor instructor);
    Result<Void> adminOverrideCapacity(String sectionId, int newCapacity, String adminId, String reason);
}


