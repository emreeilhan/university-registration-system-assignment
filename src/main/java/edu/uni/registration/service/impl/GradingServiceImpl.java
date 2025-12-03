package edu.uni.registration.service.impl;

import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Gradable;
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

/**
 * Grade posting and GPA calculation.
 */
public class GradingServiceImpl implements GradingService {
    private final StudentRepository studentRepo;
    private final SectionRepository sectionRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final TranscriptRepository transcriptRepo;

    public GradingServiceImpl(StudentRepository studentRepo, SectionRepository sectionRepo, EnrollmentRepository enrollmentRepo, TranscriptRepository transcriptRepo) {
        this.studentRepo = studentRepo;
        this.sectionRepo = sectionRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.transcriptRepo = transcriptRepo;
    }

    @Override
    public Result<Void> postGrade(String insId, String secId, String stuId, Grade grade) {
        var sOpt = studentRepo.findById(stuId);
        if (sOpt.isEmpty()) return Result.fail("Student not found");
        
        var secOpt = sectionRepo.findById(secId);
        if (secOpt.isEmpty()) return Result.fail("Section not found");
        
        var eOpt = enrollmentRepo.findByStudentAndSection(sOpt.get(), secOpt.get());
        if (eOpt.isEmpty()) return Result.fail("Not enrolled");
        
        Enrollment enr = eOpt.get();
        enr.assignGrade(grade);
        
        var t = transcriptRepo.findById(stuId)
                .orElseGet(() -> transcriptRepo.save(new Transcript(sOpt.get())));
        
        t.addEntry(new edu.uni.registration.model.TranscriptEntry(secOpt.get(), grade));
        return Result.ok(null);
    }

    @Override
    public Result<Double> computeGPA(String stuId) {
        var tOpt = transcriptRepo.findById(stuId);
        if (tOpt.isEmpty()) return Result.fail("No transcript");
        return Result.ok(tOpt.get().getGpa());
    }
}
