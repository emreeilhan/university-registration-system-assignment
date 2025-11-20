package edu.uni.registration.service.impl;

import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Grade;
import edu.uni.registration.model.Section;
import edu.uni.registration.model.Student;
import edu.uni.registration.repository.EnrollmentRepository;
import edu.uni.registration.repository.SectionRepository;
import edu.uni.registration.repository.StudentRepository;
import edu.uni.registration.repository.TranscriptRepository;
import edu.uni.registration.service.GradingService;
import edu.uni.registration.model.Transcript;

public class GradingServiceImpl implements GradingService {
    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TranscriptRepository transcriptRepository;

    public GradingServiceImpl(StudentRepository studentRepository, SectionRepository sectionRepository, EnrollmentRepository enrollmentRepository, TranscriptRepository transcriptRepository) {
        if (studentRepository == null || sectionRepository == null || enrollmentRepository == null || transcriptRepository == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.transcriptRepository = transcriptRepository;
    }

    @Override
    public void postGrade(String instructorId, String sectionId, String studentId, Grade grade) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));
        Enrollment enrollment = enrollmentRepository.findByStudentAndSection(student, section).orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        enrollment.assignGrade(grade);
        Transcript transcript = transcriptRepository.findById(studentId).orElseGet(() -> transcriptRepository.save(new Transcript(student)));
        transcript.addEntry(new edu.uni.registration.model.TranscriptEntry(section, grade));
    }

    @Override
    public double computeGPA(String studentId) {
        Transcript transcript = transcriptRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Transcript not found for student: " + studentId));
        return transcript.getGpa();
    }
}


