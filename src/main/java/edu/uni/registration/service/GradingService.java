package edu.uni.registration.service;

import edu.uni.registration.model.Grade;
import edu.uni.registration.util.Result;

public interface GradingService {
    Result<Void> postGrade(String instructorId, String sectionId, String studentId, Grade grade);
    Result<Double> computeGPA(String studentId);
}


