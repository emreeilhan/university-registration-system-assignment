package edu.uni.registration.util;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Section;
import edu.uni.registration.model.TimeSlot;
import edu.uni.registration.repository.SectionRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

/**
 * Specification pattern for course filtering.
 */
public class CourseQuery implements Specification<Course> {
    
    private String code;
    private String title;
    private Integer minCredits;
    private Integer maxCredits;
    private String instructorName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private SectionRepository sectionRepo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMinCredits() {
        return minCredits;
    }

    public void setMinCredits(Integer minCredits) {
        this.minCredits = minCredits;
    }

    public Integer getMaxCredits() {
        return maxCredits;
    }

    public void setMaxCredits(Integer maxCredits) {
        this.maxCredits = maxCredits;
    }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String name) { this.instructorName = name; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek day) { this.dayOfWeek = day; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime t) { this.startTime = t; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime t) { this.endTime = t; }
    
    public void setSectionRepository(SectionRepository repo) {
        this.sectionRepo = repo;
    }

    @Override
    public boolean isSatisfiedBy(Course course) {
        if (course == null) return false;
        
        if (code != null && !code.isBlank()) {
            if (!course.getCode().toLowerCase(Locale.ROOT).contains(code.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        
        if (title != null && !title.isBlank()) {
            if (!course.getTitle().toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }

        if (minCredits != null && course.getCredits() < minCredits) return false;
        if (maxCredits != null && course.getCredits() > maxCredits) return false;
        
        if (needsSectionCheck()) {
            return checkSections(course);
        }
        
        return true;
    }
    
    private boolean needsSectionCheck() {
        return (instructorName != null && !instructorName.isBlank()) 
            || dayOfWeek != null 
            || startTime != null 
            || endTime != null;
    }
    
    private boolean checkSections(Course course) {
        if (sectionRepo == null) return false;
        
        List<Section> sections = sectionRepo.findAll();
        for (Section sec : sections) {
            if (!sec.getCourse().getCode().equals(course.getCode())) continue;
            
            if (instructorName != null && !instructorName.isBlank()) {
                if (sec.getInstructor() == null) continue;
                String fullName = sec.getInstructor().getFullName().toLowerCase(Locale.ROOT);
                if (!fullName.contains(instructorName.toLowerCase(Locale.ROOT))) continue;
            }
            
            if (dayOfWeek != null || startTime != null || endTime != null) {
                boolean timeOk = false;
                for (TimeSlot slot : sec.getMeetingTimes()) {
                    boolean slotMatches = true;
                    if (dayOfWeek != null && slot.getDayOfWeek() != dayOfWeek) slotMatches = false;
                    if (startTime != null && slot.getStart().isBefore(startTime)) slotMatches = false;
                    if (endTime != null && slot.getEnd().isAfter(endTime)) slotMatches = false;
                    if (slotMatches) { timeOk = true; break; }
                }
                if (!timeOk) continue;
            }
            
            return true;
        }
        return false;
    }
}


