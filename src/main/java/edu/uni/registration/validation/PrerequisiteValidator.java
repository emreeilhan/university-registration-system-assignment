package edu.uni.registration.validation;
import edu.uni.registration.model.Grade;
import edu.uni.registration.model.Course;
import edu.uni.registration.model.Transcript;
import edu.uni.registration.model.TranscriptEntry;

import java.util.HashSet;
import java.util.Set;

/**
 * Checks if a student passed all prerequisite courses (grade C or better).
 */
public class PrerequisiteValidator {

    /** Admin override bypasses all prerequisite checks. */
    public boolean hasCompletedPrerequisites(Transcript transcript, Course targetCourse, boolean adminOverride) {
        if (adminOverride) {
            return true;
        }
        return hasCompletedPrerequisites(transcript, targetCourse);
    }

    public boolean hasCompletedPrerequisites(Transcript transcript, Course targetCourse) {
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

