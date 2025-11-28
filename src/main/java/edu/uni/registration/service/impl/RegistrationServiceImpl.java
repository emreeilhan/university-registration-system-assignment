package edu.uni.registration.service.impl;

import edu.uni.registration.model.*;
import edu.uni.registration.model.Admin;
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
 * RegistrationService implementation. Handles enrollments, drops, schedules, and admin overrides.
 */
public class RegistrationServiceImpl implements RegistrationService {

    private final StudentRepository studentRepo;
    private final SectionRepository sectionRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PrerequisiteValidator validator;
    private final TranscriptRepository transcriptRepo;
    private final PersonRepository personRepo;
    private final List<AdminOverrideLog> logs;

    public RegistrationServiceImpl(StudentRepository studentRepo,
                               SectionRepository sectionRepo,
                               PrerequisiteValidator validator,
                               TranscriptRepository transcriptRepo,
                               PersonRepository personRepo,
                               EnrollmentRepository enrollmentRepo) {
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.validator = validator;
        this.transcriptRepo = transcriptRepo;
        this.personRepo = personRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.logs = new ArrayList<>();
    }

    @Override
    public Result<Enrollment> enrollStudentInSection(String sid, String secId) {
        // Basic checks
        if (sid == null || secId == null) return Result.fail("Missing ID");

        var sOpt = studentRepo.findById(sid);
        if (sOpt.isEmpty()) return Result.fail("Student not found: " + sid);
        Student s = sOpt.get();

        var secOpt = sectionRepo.findById(secId);
        if (secOpt.isEmpty()) return Result.fail("Section not found: " + secId);
        Section sec = secOpt.get();

        var tOpt = transcriptRepo.findById(s.getId());
        if (tOpt.isEmpty()) return Result.fail("No transcript for " + s.getId());
        
        // Prereq check
        if (!validator.hasCompletedPrerequisites(tOpt.get(), sec.getCourse())) {
            return Result.fail("Prereqs not met");
        }
        
        // Time conflict?
        Section conflict = findFirstConflictSection(s, sec);
        if (conflict != null) {
            return Result.fail("Time conflict with " + conflict.getId());
        }

        Enrollment enr = new Enrollment(s, sec);
        
        // Check capacity
        if (sec.isFull()) {
            if (sec.isWaitlistFull()) {
                return Result.fail("Section/Waitlist full");
            }
            enr.setStatus(EnrollmentStatus.WAITLISTED);
        } else {
            enr.setStatus(EnrollmentStatus.ENROLLED);
        }

        sec.addEnrollment(enr);
        enrollmentRepo.save(enr);
        return Result.ok(enr);
    }

    @Override
    public Result<Void> dropStudentInSection(String sid, String secId) {
        if (sid == null || secId == null) return Result.fail("IDs required");

        var sOpt = studentRepo.findById(sid);
        if (sOpt.isEmpty()) return Result.fail("Student not found");
        
        var secOpt = sectionRepo.findById(secId);
        if (secOpt.isEmpty()) return Result.fail("Section not found");
        Section sec = secOpt.get();

        Enrollment target = null;
        for (Enrollment e : sec.getRoster()) {
            if (e.getStudent().getId().equals(sid) &&
                    (e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.WAITLISTED)) {
                target = e;
                break;
            }
        }

        if (target == null) return Result.fail("Not enrolled");

        EnrollmentStatus oldStatus = target.getStatus();
        target.setStatus(EnrollmentStatus.DROPPED);

        // Auto-promote waitlist
        if (oldStatus == EnrollmentStatus.ENROLLED) {
            for (Enrollment e : sec.getRoster()) {
                if (e.getStatus() == EnrollmentStatus.WAITLISTED) {
                    e.setStatus(EnrollmentStatus.ENROLLED);
                    break;
                }
            }
        }
        
        return Result.ok(null);
    }

    @Override
    public Result<List<Section>> getCurrentSchedule(String studentId, String term) {
        if (studentId == null) return Result.fail("Student ID cannot be null");
        Optional<Student> studentOpt = studentRepo.findById(studentId);
        if (studentOpt.isEmpty()) return Result.fail("Student not found");
        
        List<Enrollment> enrollments = enrollmentRepo.findByStudent(studentId);
        List<Section> result = new ArrayList<>();
        // Use enrollmentRepo as source of truth, but also fall back to roster scan
        // in case an enrollment was added directly to a section without repo save.

        for (Enrollment e : enrollments) {
            if (e.getStatus() != EnrollmentStatus.ENROLLED) {
                continue;
            }
            Section section = e.getSection();
            if (term != null && !term.isBlank() && !term.equals(section.getTerm())) {
                continue;
            }
            result.add(section);
        }

        // Fallback scan through sections to catch any roster-only enrollments
        for (Section section : sectionRepo.findAll()) {
            if (term != null && !term.isBlank() && !term.equals(section.getTerm())) {
                continue;
            }
            for (Enrollment e : section.getRoster()) {
                if (e.getStatus() == EnrollmentStatus.ENROLLED && studentId.equals(e.getStudent().getId())) {
                    if (!result.contains(section)) {
                        result.add(section);
                    }
                    break;
                }
            }
        }

        return Result.ok(result);
    }

    @Override
    public Result<Transcript> getTranscript(String studentId) {
        if (studentId == null) return Result.fail("Student ID cannot be null");
        
        // Ensure student exists
        if (studentRepo.findById(studentId).isEmpty()) {
            return Result.fail("Student not found: " + studentId);
        }

        Optional<Transcript> tOpt = transcriptRepo.findById(studentId);
        Transcript transcript = tOpt.orElseGet(() -> transcriptRepo.save(new Transcript(studentRepo.findById(studentId).get())));
        return Result.ok(transcript);
    }

    /**
     * Finds first section with overlapping times for this student. Returns null if no conflict.
     */
    private Section findFirstConflictSection(Student student, Section target) {
        List<Section> all = sectionRepo.findAll();

        for (Section existing : all) {
            boolean enrolledHere = false;
            for (Enrollment e : existing.getRoster()) {
                if (e.getStudent().getId().equals(student.getId()) &&
                        e.getStatus() == EnrollmentStatus.ENROLLED) {
                    enrolledHere = true;
                    break;
                }
            }

            if (!enrolledHere) continue;

            // Check time conflict
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
    public Result<Enrollment> adminOverrideEnroll(String sid, String secId, String adminId, String reason) {
        if (adminId == null || personRepo.findById(adminId).isEmpty()) {
            return Result.fail("Invalid Admin");
        }
        
        var sOpt = studentRepo.findById(sid);
        var secOpt = sectionRepo.findById(secId);
        
        if (sOpt.isEmpty() || secOpt.isEmpty()) return Result.fail("Student/Section not found");
        if (!(personRepo.findById(adminId).orElse(null) instanceof Admin)) {
            return Result.fail("Admin privileges required");
        }
        
        Enrollment enr = new Enrollment(sOpt.get(), secOpt.get());
        enr.setStatus(EnrollmentStatus.ENROLLED);
        secOpt.get().addEnrollment(enr);
        enrollmentRepo.save(enr);
        
        logs.add(new AdminOverrideLog(adminId, "FORCE_ENROLL", secId, reason));
        
        return Result.ok(enr);
    }

    public List<AdminOverrideLog> getOverrideLogs() {
        return new ArrayList<>(logs);
    }
}
