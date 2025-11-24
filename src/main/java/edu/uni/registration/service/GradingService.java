package edu.uni.registration.service;

import edu.uni.registration.model.Grade;
import edu.uni.registration.util.Result;

/**
 * Service interface for handling grading operations.
 * Allows instructors to post grades and calculate student GPAs.
 */
public interface GradingService {
    /**
     * Posts a grade for a student in a specific section.
     *
     * @param instructorId the ID of the instructor posting the grade
     * @param sectionId the ID of the section
     * @param studentId the ID of the student receiving the grade
     * @param grade the grade to assign
     * @return a Result indicating success or failure
     */
    Result<Void> postGrade(String instructorId, String sectionId, String studentId, Grade grade);

    /**
     * Calculates and returns the GPA for a student.
     *
     * @param studentId the ID of the student
     * @return a Result containing the GPA, or an error message
     */
    Result<Double> computeGPA(String studentId);
}


