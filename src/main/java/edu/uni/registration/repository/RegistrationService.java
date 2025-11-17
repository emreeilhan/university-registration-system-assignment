package edu.uni.registration.repository;

import edu.uni.registration.model.*;
import edu.uni.registration.model.Enrollment.EnrollmentStatus;
import edu.uni.registration.validation.PrerequisiteValidator;
import edu.uni.registration.service.RegistrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistrationService implements RegistrationService {

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final PrerequisiteValidator prerequisiteValidator;
    private final TranscriptRepository transcriptRepository;

    public RegistrationService(StudentRepository studentRepository,
                               SectionRepository sectionRepository,PrerequisiteValidator prerequisiteValidator, TranscriptRepository transcriptRepository) {
        if (studentRepository == null || sectionRepository == null  || prerequisiteValidator == null || transcriptRepository == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.prerequisiteValidator = prerequisiteValidator;
        this.transcriptRepository = transcriptRepository;
    }

    public Enrollment enrollStudentInSection(String studentId, String sectionId) {
        Student student = findStudentOrThrow(studentId);
        Section section = findSectionOrThrow(sectionId);

        // time conflict
        Section conflicting = findFirstConflictSection(student, section);
        if (conflicting != null) {
            throw new IllegalStateException(
                    "Student " + studentId +
                            " has a time conflict between section " + sectionId +
                            " and section " + conflicting.getId()
            );
        }

        Enrollment enrollment = new Enrollment(student, section);

        if (section.isFull()) {
            enrollment.setStatus(EnrollmentStatus.WAITLISTED);
        } else {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        }
        Transcript transcript = transcriptRepository.findById(student.getId())
                .orElseThrow(() -> new IllegalStateException("Transcript not found for student: " + student.getId()));
        if (!prerequisiteValidator.hasCompletedPrerequisites(transcript, section.getCourse())) {
            throw new IllegalStateException("Student has not completed prerequisites.");
        }

        section.addEnrollment(enrollment);
        return enrollment;
    }

    public void dropStudentInSection(String studentId, String sectionId) {
        Student student = findStudentOrThrow(studentId);
        Section section = findSectionOrThrow(sectionId);

        Enrollment target = null;

        for (Enrollment e : section.getRoster()) {
            if (e.getStudent().getId().equals(student.getId()) &&
                    e.getStatus() == EnrollmentStatus.ENROLLED) {
                target = e;
                break;
            }
        }

        if (target == null) return;

        target.setStatus(EnrollmentStatus.DROPPED);

        
        Enrollment waitlisted = null;

        for (Enrollment e : section.getRoster()) {
            if (e.getStatus() == EnrollmentStatus.WAITLISTED) {
                waitlisted = e;
                break;
            }
        }

        if (waitlisted != null) {
            waitlisted.setStatus(EnrollmentStatus.ENROLLED);
        }
    }

    public List<Section> getCurrentSchedule(String studentId, String term) {
        Student student = findStudentOrThrow(studentId);
        List<Section> allSections = sectionRepository.findAll();
        List<Section> result = new ArrayList<>();

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
        return result;
    }

    

    private Student findStudentOrThrow(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student id cannot be null");
        }
        Optional<Student> opt = studentRepository.findById(studentId);
        return opt.orElseThrow(
                () -> new IllegalArgumentException("Student not found: " + studentId)
        );
    }

    private Section findSectionOrThrow(String sectionId) {
        if (sectionId == null || sectionId.isBlank()) {
            throw new IllegalArgumentException("Section id cannot be null");
        }
        Optional<Section> opt = sectionRepository.findById(sectionId);
        return opt.orElseThrow(
                () -> new IllegalArgumentException("Section not found: " + sectionId)
        );
    }

    private Section findFirstConflictSection(Student student, Section target) {

        List<Section> all = sectionRepository.findAll();

        for (Section existing : all) {

            boolean enrolledHere = false;

            for (Enrollment e : existing.getRoster()) {
                if (e.getStudent().getId().equals(student.getId()) &&
                        e.getStatus() == EnrollmentStatus.ENROLLED) {
                    enrolledHere = true;
                    break;
                }
            }

            if (!enrolledHere)
                continue;

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
}
