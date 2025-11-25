package edu.uni.registration.service.impl;

import edu.uni.registration.model.*;
import edu.uni.registration.model.Enrollment.EnrollmentStatus;
import edu.uni.registration.validation.PrerequisiteValidator;
import edu.uni.registration.service.RegistrationService;
import edu.uni.registration.repository.*;
import edu.uni.registration.util.AdminOverrideLog;
import edu.uni.registration.util.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RegistrationService interface.
 * Handles student enrollment, dropping, schedule viewing, and admin overrides.
 */
public class RegistrationServiceImpl implements RegistrationService {

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final PrerequisiteValidator prerequisiteValidator;
    private final TranscriptRepository transcriptRepository;
    private final PersonRepository personRepository;
    private final List<AdminOverrideLog> overrideLogs;

    /**
     * Creates a new RegistrationServiceImpl with the required repositories and validators.
     *
     * @param studentRepository repository for student data
     * @param sectionRepository repository for section data
     * @param prerequisiteValidator validator for checking prerequisites
     * @param transcriptRepository repository for transcript data
     * @param personRepository repository for person data
     * @throws IllegalArgumentException if any parameter is null
     */
    public RegistrationServiceImpl(StudentRepository studentRepository,
                               SectionRepository sectionRepository,PrerequisiteValidator prerequisiteValidator, TranscriptRepository transcriptRepository, PersonRepository personRepository) {
        if (studentRepository == null || sectionRepository == null  || prerequisiteValidator == null || transcriptRepository == null || personRepository == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.prerequisiteValidator = prerequisiteValidator;
        this.transcriptRepository = transcriptRepository;
        this.personRepository = personRepository;
        this.overrideLogs = new ArrayList<>();
    }

    @Override
    public Result<Enrollment> enrollStudentInSection(String studentId, String sectionId) {
        if (studentId == null || sectionId == null) {
            return Result.fail("Student ID and Section ID cannot be null");
        }

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return Result.fail("Student not found: " + studentId);
        }
        Student student = studentOpt.get();

        Optional<Section> sectionOpt = sectionRepository.findById(sectionId);
        if (sectionOpt.isEmpty()) {
            return Result.fail("Section not found: " + sectionId);
        }
        Section section = sectionOpt.get();

        // Check for time conflicts first (Business Rule #6)
        Section conflicting = findFirstConflictSection(student, section);
        if (conflicting != null) {
            return Result.fail("Student " + studentId +
                            " has a time conflict between section " + sectionId +
                            " and section " + conflicting.getId());
        }

        Enrollment enrollment = new Enrollment(student, section);

        // Handle capacity and waitlists
        if (section.isFull()) {
             if (section.isWaitlistFull()) {
                 return Result.fail("Section and waitlist are full");
             }
            enrollment.setStatus(EnrollmentStatus.WAITLISTED);
        } else {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        }
        
        // Prerequisites check (must be done before finalizing enrollment)
        Optional<Transcript> transcriptOpt = transcriptRepository.findById(student.getId());
        if (transcriptOpt.isEmpty()) {
             return Result.fail("Transcript not found for student: " + student.getId());
        }
        Transcript transcript = transcriptOpt.get();

        if (!prerequisiteValidator.hasCompletedPrerequisites(transcript, section.getCourse())) {
            return Result.fail("Student has not completed prerequisites.");
        }

        section.addEnrollment(enrollment);
        return Result.ok(enrollment);
    }

    @Override
    public Result<Void> dropStudentInSection(String studentId, String sectionId) {
        if (studentId == null || sectionId == null) return Result.fail("IDs cannot be null");

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found");
        Student student = studentOpt.get();

        Optional<Section> sectionOpt = sectionRepository.findById(sectionId);
        if (sectionOpt.isEmpty()) return Result.fail("Section not found");
        Section section = sectionOpt.get();

        Enrollment target = null;

        for (Enrollment e : section.getRoster()) {
            if (e.getStudent().getId().equals(student.getId()) &&
                    (e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.WAITLISTED)) {
                target = e;
                break;
            }
        }

        if (target == null) {
            return Result.fail("Student is not enrolled in this section");
        }

        // Save the original status before dropping
        EnrollmentStatus originalStatus = target.getStatus();
        target.setStatus(EnrollmentStatus.DROPPED);

        // Promote from waitlist if a seat opened (we dropped an ENROLLED student)
        if (originalStatus == EnrollmentStatus.ENROLLED) {
            // Find the first waitlisted student and promote them
            for (Enrollment e : section.getRoster()) {
                if (e.getStatus() == EnrollmentStatus.WAITLISTED) {
                    e.setStatus(EnrollmentStatus.ENROLLED);
                    break; // Only promote one student
                }
            }
        }
        
        return Result.ok(null);
    }

    @Override
    public Result<List<Section>> getCurrentSchedule(String studentId, String term) {
        if (studentId == null) return Result.fail("Student ID cannot be null");
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found");
        Student student = studentOpt.get();

        List<Section> allSections = sectionRepository.findAll();
        List<Section> result = new ArrayList<>();

        // Using a standard loop to filter sections logic manually
        // Check if student is enrolled in the section
        for (Section section : allSections) {

            if (term != null && !term.isBlank()) {
                if (!term.equals(section.getTerm())) {
                    continue;
                }
            }

            boolean enrolledHere = false;
            for (Enrollment e : section.getRoster()) {
                if (e.getStudent().getId().equals(student.getId()) &&
                        e.getStatus() == EnrollmentStatus.ENROLLED) {
                    enrolledHere = true;
                    break;
                }
            }

            if (enrolledHere) {
                result.add(section);
            }
        }
        return Result.ok(result);
    }

    @Override
    public Result<Transcript> getTranscript(String studentId) {
        if (studentId == null) return Result.fail("Student ID cannot be null");
        
        // Ensure student exists
        if (studentRepository.findById(studentId).isEmpty()) {
            return Result.fail("Student not found: " + studentId);
        }

        Optional<Transcript> tOpt = transcriptRepository.findById(studentId);
        if (tOpt.isEmpty()) {
            // If no transcript found (maybe new student), return empty/new one or fail depending on logic.
            // Here we assume it might not exist yet if no grades are posted, but usually created on student creation.
            // Let's return a fail or create on fly if desired. For now fail if not found.
            return Result.fail("No transcript record found for student.");
        }
        return Result.ok(tOpt.get());
    }

    // Helper to find conflicts
    // It loops through all sections to check enrollment and time overlap.
    private Section findFirstConflictSection(Student student, Section target) {
        List<Section> all = sectionRepository.findAll();

        for (Section existing : all) {
            boolean enrolledHere = false;
            // Check if student is in this section
            for (Enrollment e : existing.getRoster()) {
                if (e.getStudent().getId().equals(student.getId()) &&
                        e.getStatus() == EnrollmentStatus.ENROLLED) {
                    enrolledHere = true;
                    break;
                }
            }

            if (!enrolledHere) continue;

            // If enrolled, check for time overlap
            for (TimeSlot a : existing.getMeetingTimes()) {
                for (TimeSlot b : target.getMeetingTimes()) {
                    if (a.overlaps(b)) {
                        return existing;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Result<Enrollment> adminOverrideEnroll(String studentId, String sectionId, String adminId, String reason) {
        if (adminId == null || adminId.isBlank()) {
            return Result.fail("Admin ID cannot be null");
        }
        if (personRepository.findById(adminId).isEmpty()) {
            return Result.fail("Admin not found: " + adminId);
        }
        
        Optional<Student> sOpt = studentRepository.findById(studentId);
        if (sOpt.isEmpty()) return Result.fail("Student not found");
        
        Optional<Section> secOpt = sectionRepository.findById(sectionId);
        if (secOpt.isEmpty()) return Result.fail("Section not found");
        
        Student student = sOpt.get();
        Section section = secOpt.get();
        
        Enrollment enrollment = new Enrollment(student, section);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        section.addEnrollment(enrollment);
        
        AdminOverrideLog log = new AdminOverrideLog(
            adminId,
            "ENROLLMENT_OVERRIDE: Student " + studentId + " enrolled in " + sectionId,
            sectionId,
            reason != null ? reason : "No reason provided"
        );
        overrideLogs.add(log);
        
        return Result.ok(enrollment);
    }

    /**
     * Gets a copy of all admin override logs for audit purposes.
     *
     * @return a list of admin override logs
     */
    public List<AdminOverrideLog> getOverrideLogs() {
        return new ArrayList<>(overrideLogs);
    }
}
