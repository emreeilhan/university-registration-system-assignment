package edu.uni.registration.validation;
import edu.uni.registration.model.Grade;
import edu.uni.registration.model.Course;
import edu.uni.registration.model.Transcript;
import edu.uni.registration.model.TranscriptEntry;

import java.util.HashSet;
import java.util.Set;

public class PrerequisiteValidator {
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

