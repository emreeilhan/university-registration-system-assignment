package edu.uni.registration.service;

import edu.uni.registration.model.Enrollment;
import edu.uni.registration.model.Section;

import java.util.List;

public interface RegistrationService {
    Enrollment enrollStudentInSection(String studentId, String sectionId);
    void dropStudentInSection(String studentId, String sectionId);
    List<Section> getCurrentSchedule(String studentId, String term);
}


