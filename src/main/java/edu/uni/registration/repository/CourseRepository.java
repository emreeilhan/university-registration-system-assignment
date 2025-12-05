package edu.uni.registration.repository;

import edu.uni.registration.model.Course;
import java.util.*;

public class CourseRepository implements Repository<Course, String> {

    private final Map<String, Course> storage = new HashMap<>();

    @Override
    public Optional<Course> findById(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(code));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Course save(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (course.getCode() == null || course.getCode().isBlank()) {
            throw new IllegalArgumentException("Course code cannot be null or blank");
        }
        storage.put(course.getCode(), course);
        return course;
    }

    @Override
    public void deleteById(String code) {
        if (code == null || code.isBlank()) {
            return; // Silently ignore if code is null or blank
        }
        storage.remove(code);
    }

    // Helper methods
    public boolean existsByCode(String code) {
        return code != null && storage.containsKey(code);
    }

    public long count() {
        return storage.size();
    }

    public List<Course> findByTitleContaining(String titleFragment) {
        if (titleFragment == null || titleFragment.isBlank()) {
            return List.of();
        }

        String lower = titleFragment.toLowerCase(Locale.ROOT);
        List<Course> result = new ArrayList<>();

        for (Course course : storage.values()) {
            if (course.getTitle() != null &&
                    course.getTitle().toLowerCase(Locale.ROOT).contains(lower)) {
                result.add(course);
            }
        }

        return result;
    }

    void clear() {
        storage.clear();
    }
}
