package edu.uni.registration.model;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Academic record with GPA calculation.
 */
public class Transcript {
    private final Student student;
    private final List<TranscriptEntry> entries;

    public Transcript(Student student){
        if(student == null)
            throw new IllegalArgumentException("Student cannot be null");
        this.student = student;
        this.entries = new ArrayList<>();
    }

    public Student getStudent() {
        return student;
    }

    public List<TranscriptEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void addEntry(TranscriptEntry entry){
        if(entry == null){
            throw new IllegalArgumentException("Entry cannot be null");
        }
        entries.add(entry);
    }

    /** Excludes I and W grades. */
    public int getTotalCredits(){
        int total = 0;
        for(TranscriptEntry entry : entries){
            Grade g = entry.getGrade();
            if (g != null && g.countsTowardsGpa()) {
                total += entry.getCredits();
            }
        }
        return total;
    }

    /**
     * Total quality points (credits × grade points, only for GPA-counting grades).
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
     * Calculates GPA (quality points / credits). Returns 0.0 if no credits.
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
