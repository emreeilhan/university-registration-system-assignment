package edu.uni.registration.model;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class Transcript {
    private final Student student;
    private final List<TranscriptEntry> entries;

    public Transcript(Student student){
        if(student == null)
            throw new IllegalArgumentException("Student cannot be null");
        this.student = student;
        this.entries = new ArrayList<>();
    }

    //Getter methods
    public Student getStudent() {
        return student;
    }
    public List<TranscriptEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    //Helper method(s)
    public void addEntry(TranscriptEntry entry){
        if(entry == null){
            throw new IllegalArgumentException("Entry cannot be null");
        }
        entries.add(entry);
    }

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
