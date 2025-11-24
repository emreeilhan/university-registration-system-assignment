package edu.uni.registration.model;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Represents a student's academic transcript.
 * Contains all completed courses with grades and calculates GPA.
 */
public class Transcript {
    private final Student student;
    private final List<TranscriptEntry> entries;

    /**
     * Creates a new transcript for the given student.
     *
     * @param student the student this transcript belongs to
     * @throws IllegalArgumentException if student is null
     */
    public Transcript(Student student){
        if(student == null)
            throw new IllegalArgumentException("Student cannot be null");
        this.student = student;
        this.entries = new ArrayList<>();
    }

    /**
     * Gets the student this transcript belongs to.
     *
     * @return the student object
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Gets a read-only list of all transcript entries.
     *
     * @return an unmodifiable list of transcript entries
     */
    public List<TranscriptEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Adds a new entry to this transcript.
     *
     * @param entry the transcript entry to add
     * @throws IllegalArgumentException if entry is null
     */
    public void addEntry(TranscriptEntry entry){
        if(entry == null){
            throw new IllegalArgumentException("Entry cannot be null");
        }
        entries.add(entry);
    }

    /**
     * Calculates the total credit hours earned.
     * Only counts credits from courses with grades that count towards GPA (excludes I and W).
     *
     * @return the total credits
     */
    public int getTotalCredits(){
        int total = 0;
        for(TranscriptEntry entry : entries){
            Grade g = entry.getGrade();
            // Exclude entries that should not count towards GPA/credits (e.g., I/W)
            if (g != null && g.countsTowardsGpa()) {
                total += entry.getCredits();
            }
        }
        return total;
    }

    /**
     * Calculates the total quality points earned.
     * Quality points = credits × grade points, only for grades that count towards GPA.
     *
     * @return the total quality points
     */
    public double getTotalQualityPoints(){
        double total = 0;
        for(TranscriptEntry entry : entries){
            Grade g = entry.getGrade();
            if (g != null && g.countsTowardsGpa()) {
                total += entry.getQualityPoints();
            }
        }
        return total;
    }

    /**
     * Calculates the student's GPA (Grade Point Average).
     * GPA = total quality points / total credits.
     *
     * @return the GPA, or 0.0 if no credits earned
     */
    public double getGpa(){
        int totalCredits = getTotalCredits();
        if(totalCredits == 0){
            return 0.0;
        }
        return getTotalQualityPoints() / totalCredits;
    }

    @Override
    public String toString() {
        return "Transcript{" +
                "studentId='" + student.getId() + '\'' +
                ", studentName='" + student.getFullName() + '\'' +
                ", totalCredits=" + getTotalCredits() +
                ", gpa=" + String.format("%.2f", getGpa()) +
                '}';
    }
}
