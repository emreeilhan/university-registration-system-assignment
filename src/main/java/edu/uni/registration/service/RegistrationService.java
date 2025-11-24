package edu.uni.registration.service;

import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Section;
import edu.uni.registration.util.Result;

import java.util.List;

/**
 * Service interface handling student registration workflows.
 * Defines operations for enrolling, dropping, and viewing schedules.
 */
public interface RegistrationService {

    /**
     * Enrolls a student in a specific section.
     * Validates capacity, prerequisites, and schedule conflicts.
     *
     * @param studentId The ID of the student.
     * @param sectionId The ID of the section to enroll in.
     * @return A Result containing the Enrollment object on success, or an error message on failure.
     */
    Result<Enrollment> enrollStudentInSection(String studentId, String sectionId);

    /**
     * Drops a student from a section.
     * If a seat opens up, this may trigger automatic promotion from the waitlist.
     *
     * @param studentId The ID of the student.
     * @param sectionId The ID of the section to drop.
     * @return A Result indicating success or failure.
     */
    Result<Void> dropStudentInSection(String studentId, String sectionId);

    /**
     * Retrieves the current schedule (list of enrolled sections) for a student.
     *
     * @param studentId The ID of the student.
     * @param term      Optional term filter (e.g., "Fall"). If null, returns all terms.
     * @return A Result containing the list of sections.
     */
    Result<List<Section>> getCurrentSchedule(String studentId, String term);

    /**
     * Retrieves the student's transcript details (entries and GPA).
     *
     * @param studentId The ID of the student.
     * @return A Result containing the Transcript object.
     */
    Result<edu.uni.registration.model.Transcript> getTranscript(String studentId);

    /**
     * Allows an admin to bypass standard enrollment rules (capacity, prerequisites).
     * This action is logged for audit purposes.
     *
     * @param studentId The ID of the student.
     * @param sectionId The ID of the section.
     * @param adminId   The ID of the admin performing the override.
     * @param reason    The justification for the override.
     * @return A Result containing the new Enrollment.
     */
    Result<Enrollment> adminOverrideEnroll(String studentId, String sectionId, String adminId, String reason);
}
