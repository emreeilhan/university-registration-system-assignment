package edu.uni.registration.model;

/**
 * Represents a single entry in a student's transcript.
 * Contains information about a completed section and the grade received.
 */
public class TranscriptEntry {
    private final Section section;
    private final Grade grade;

    /**
     * Creates a new transcript entry for a completed section with a grade.
     *
     * @param section the section that was completed
     * @param grade the grade received in that section
     * @throws IllegalArgumentException if section or grade is null
     */
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

    /**
     * Gets the section this entry represents.
     *
     * @return the section object
     */
    public Section getSection() {
        return section;
    }

    /**
     * Gets the grade received in this section.
     *
     * @return the grade
     */
    public Grade getGrade() {
        return grade;
    }

    /**
     * Gets the number of credits for this course.
     *
     * @return the credit hours
     */
    public int getCredits(){
        return section.getCourse().getCredits();
    }

    /**
     * Calculates the quality points for this entry.
     * Quality points = credits × grade points.
     *
     * @return the quality points
     */
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
