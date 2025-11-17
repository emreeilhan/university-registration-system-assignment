package edu.uni.registration.repository;

import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Section;
import edu.uni.registration.model.Student;
import edu.uni.registration.model.Enrollment.EnrollmentStatus;

import java.util.*;

public class EnrollmentRepository implements Repository<Enrollment, String> {

    private final Map<String, Enrollment> storage = new HashMap<>();

    private String keyOf(Enrollment e) {
        return e.getStudent().getId() + ":" + e.getSection().getId();
    }

    private String keyOf(String studentId, String sectionId) {
        return studentId + ":" + sectionId;
    }

    @Override
    public Optional<Enrollment> findById(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public List<Enrollment> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
        String key = keyOf(enrollment);
        storage.put(key, enrollment);
        return enrollment;
    }

    @Override
    public void deleteById(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        storage.remove(key);
    }

    // Helper queries
    public Optional<Enrollment> findByStudentAndSection(Student student, Section section) {
        if (student == null || section == null) {
            return Optional.empty();
        }
        return findById(keyOf(student.getId(), section.getId()));
    }

    public List<Enrollment> findByStudent(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return List.of();
        }
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (studentId.equals(e.getStudent().getId())) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findBySection(String sectionId) {
        if (sectionId == null || sectionId.isBlank()) {
            return List.of();
        }
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (sectionId.equals(e.getSection().getId())) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findByStatus(EnrollmentStatus status) {
        if (status == null) {
            return List.of();
        }
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (status == e.getStatus()) {
                result.add(e);
            }
        }
        return result;
    }
}


