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
import edu.uni.registration.util.Result;

import java.util.Optional;

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
    public Result<Void> postGrade(String instructorId, String sectionId, String studentId, Grade grade) {
        Optional<Student> sOpt = studentRepository.findById(studentId);
        if (sOpt.isEmpty()) return Result.fail("Student not found: " + studentId);
        
        Optional<Section> secOpt = sectionRepository.findById(sectionId);
        if (secOpt.isEmpty()) return Result.fail("Section not found: " + sectionId);
        
        Student student = sOpt.get();
        Section section = secOpt.get();
        
        // In a real app, we would check if the instructorId matches the section's instructor
        
        Optional<Enrollment> eOpt = enrollmentRepository.findByStudentAndSection(student, section);
        if (eOpt.isEmpty()) return Result.fail("Enrollment not found");
        
        Enrollment enrollment = eOpt.get();
        enrollment.assignGrade(grade);
        
        Transcript transcript = transcriptRepository.findById(studentId)
                .orElseGet(() -> transcriptRepository.save(new Transcript(student)));
        
        transcript.addEntry(new edu.uni.registration.model.TranscriptEntry(section, grade));
        return Result.ok(null);
    }

    @Override
    public Result<Double> computeGPA(String studentId) {
        Optional<Transcript> tOpt = transcriptRepository.findById(studentId);
        if (tOpt.isEmpty()) {
            return Result.fail("Transcript not found for student: " + studentId);
        }
        return Result.ok(tOpt.get().getGpa());
    }
}
