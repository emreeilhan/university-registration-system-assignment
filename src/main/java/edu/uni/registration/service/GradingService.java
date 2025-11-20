package edu.uni.registration.service;

import edu.uni.registration.model.Grade;

public interface GradingService {
    void postGrade(String instructorId, String sectionId, String studentId, Grade grade);
    double computeGPA(String studentId);
}


