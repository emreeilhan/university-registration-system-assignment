package edu.uni.registration.service;

import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Section;
import edu.uni.registration.util.Result;

import java.util.List;

/**
 * Service for student registration. Handles enrollments, drops, schedules, and admin overrides.
 */
public interface RegistrationService {

    /**
     * Enrolls a student in a section. Validates capacity, prerequisites, and schedule conflicts.
     */
    Result<Enrollment> enrollStudentInSection(String studentId, String sectionId);

    /**
     * Drops a student from a section. May auto-promote from waitlist if seat opens.
     */
    Result<Void> dropStudentInSection(String studentId, String sectionId);

    /**
     * Gets student's current schedule. Optional term filter (null = all terms).
     */
    Result<List<Section>> getCurrentSchedule(String studentId, String term);

    /**
     * Gets student's transcript (entries and GPA).
     */
    Result<edu.uni.registration.model.Transcript> getTranscript(String studentId);

    /**
     * Admin override: bypasses capacity and prerequisites. Logged for audit.
     */
    Result<Enrollment> adminOverrideEnroll(String studentId, String sectionId, String adminId, String reason);
}
