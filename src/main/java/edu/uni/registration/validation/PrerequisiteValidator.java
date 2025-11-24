package edu.uni.registration.validation;
import edu.uni.registration.model.Grade;
import edu.uni.registration.model.Course;
import edu.uni.registration.model.Transcript;
import edu.uni.registration.model.TranscriptEntry;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates whether a student has completed the prerequisites for a course.
 * A prerequisite is considered passed if the student received a grade with at least 2.0 points (C or better).
 */
public class PrerequisiteValidator {

    /**
     * Checks if prerequisites are completed, with optional admin override.
     * If admin override is true, always returns true regardless of prerequisites.
     *
     * @param transcript the student's transcript
     * @param targetCourse the course to check prerequisites for
     * @param adminOverride true if admin is overriding the check, false otherwise
     * @return true if prerequisites are met or override is active, false otherwise
     */
    public boolean hasCompletedPrerequisites(Transcript transcript, Course targetCourse, boolean adminOverride) {
        if (adminOverride) {
            return true;
        }
        return hasCompletedPrerequisites(transcript, targetCourse);
    }

    /**
     * Checks if the student has completed all prerequisites for the target course.
     * A course is considered passed if the grade is C or better (2.0 points or higher).
     *
     * @param transcript the student's transcript containing completed courses
     * @param targetCourse the course to check prerequisites for
     * @return true if all prerequisites are completed, false otherwise
     */
    public boolean hasCompletedPrerequisites(Transcript transcript ,Course targetCourse) {
        if(targetCourse.getPrerequisites().isEmpty()) {
            return true;
        }
        Set<String> passedCourses = new HashSet<>();
        for(TranscriptEntry entry: transcript.getEntries()) {
            Grade g =  entry.getGrade();
            if(g != null && g.getPoints() >= 2.0 ) {
                passedCourses.add(entry.getSection().getCourse().getCode());
            }
        }
        for(String prereq: targetCourse.getPrerequisites()) {
            if(!passedCourses.contains(prereq)) {
                return false;
            }
        }
        return true;
    }
}

