package edu.uni.registration.model;

public class TranscriptEntry {
    private final Section section;
    private final Grade grade;

    public TranscriptEntry(Section section, Grade grade) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }
        if (grade == null) {
            throw new IllegalArgumentException("Grade cannot be null");
        }
        this.section = section;
        this.grade = grade;
    }

    //Getter methods
    public Section getSection() {
        return section;
    }

    public Grade getGrade() {
        return grade;
    }

    public int getCredits(){
        return section.getCourse().getCredits();
    }

    public double getQualityPoints(){
        return getCredits() * grade.getPoints();
    }

    @Override
    public String toString() {
        return "TranscriptEntry{" +
                "sectionId='" + section.getId() + '\'' +
                ", courseCode='" + section.getCourse().getCode() + '\'' +
                ", term='" + section.getTerm() + '\'' +
                ", grade=" + grade +
                ", credits=" + getCredits() +
                ", qualityPoints=" + getQualityPoints() +
                '}';
    }
}
