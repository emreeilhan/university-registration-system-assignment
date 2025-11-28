package edu.uni.registration.util;

import edu.uni.registration.model.Course;
import edu.uni.registration.model.Searchable;
import edu.uni.registration.model.Section;
import edu.uni.registration.model.TimeSlot;
import edu.uni.registration.repository.SectionRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

/**
 * Handy filter for course searches. 
 * Pick and combine what you care about: code, title, credits,
 * instructor, days, or times—only matching courses are returned.
 */
public class CourseSpecification implements Specification<Course> {
    
    // Course-level criteria
    private final String code;
    private final String title;
    private final Integer minCredits;
    private final Integer maxCredits;
    
    // Section-level criteria (requires SectionRepository)
    private final String instructorName;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    
    private final SectionRepository sectionRepository;
    
    
    private CourseSpecification(Builder builder, SectionRepository sectionRepository) {
        this.code = builder.code;
        this.title = builder.title;
        this.minCredits = builder.minCredits;
        this.maxCredits = builder.maxCredits;
        this.instructorName = builder.instructorName;
        this.dayOfWeek = builder.dayOfWeek;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.sectionRepository = sectionRepository;
    }

    public static CourseSpecification fromQuery(CourseQuery query, SectionRepository sectionRepository) {
        if (query == null) {
            return null;
        }
        
        return new Builder(sectionRepository)
                .withCode(query.getCode())
                .withTitle(query.getTitle())
                .withMinCredits(query.getMinCredits())
                .withMaxCredits(query.getMaxCredits())
                .withInstructorName(query.getInstructorName())
                .withDayOfWeek(query.getDayOfWeek())
                .withStartTime(query.getStartTime())
                .withEndTime(query.getEndTime())
                .build();
    }
    
    
    @Override
    public boolean isSatisfiedBy(Course course) {
        if (course == null) {
            return false;
        }
        
        // Check course-level criteria
        if (!matchesCourseFields(course)) {
            return false;
        }
        
        // Check section-level criteria if any are specified
        if (hasSectionCriteria()) {
            return matchesSectionCriteria(course);
        }
        
        return true;
    }
    
    /**
    
    private boolean matchesCourseFields(Course course) {
        // Use Searchable interface for keyword matching (polymorphism)
        // Course implements Searchable, so we can use the interface method
        Searchable searchableCourse = course;
        
        if (code != null && !code.isBlank()) {
            if (!searchableCourse.matchesKeyword(code)) {
                return false;
            }
        }
        
        if (title != null && !title.isBlank()) {
            if (!searchableCourse.matchesKeyword(title)) {
                return false;
            }
        }
        
        // Credits range check (not part of Searchable interface)
        if (minCredits != null && course.getCredits() < minCredits) {
            return false;
        }
        
        if (maxCredits != null && course.getCredits() > maxCredits) {
            return false;
        }
        
        return true;
    }
    
    /** True if the query asked for instructor or time-based filtering. */
    private boolean hasSectionCriteria() {
        return (instructorName != null && !instructorName.isBlank()) ||
               dayOfWeek != null ||
               startTime != null ||
               endTime != null;
    }
    
    /**
     * Looks at every section for the course and returns true as soon as one of
     * them matches the instructor/time filters.
     */
    private boolean matchesSectionCriteria(Course course) {
        if (sectionRepository == null) {
            // If no repository provided, cannot check section criteria
            return false;
        }
        
        List<Section> allSections = sectionRepository.findAll();
        
        for (Section section : allSections) {
            if (!section.getCourse().getCode().equals(course.getCode())) {
                continue;
            }
            
            if (matchesSection(section)) {
                return true; // At least one section matches
            }
        }
        
        return false; // No sections match
    }
    
    /** Per-section check for instructor and time window constraints. */
    private boolean matchesSection(Section section) {
        // Check instructor
        if (instructorName != null && !instructorName.isBlank()) {
            if (section.getInstructor() == null || 
                !section.getInstructor().getFullName().toLowerCase(Locale.ROOT)
                    .contains(instructorName.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        
        // Check time window
        if (dayOfWeek != null || startTime != null || endTime != null) {
            boolean timeMatch = false;
            
            for (TimeSlot slot : section.getMeetingTimes()) {
                boolean slotOk = true;
                
                if (dayOfWeek != null && slot.getDayOfWeek() != dayOfWeek) {
                    slotOk = false;
                }
                
                if (startTime != null && slot.getStart().isBefore(startTime)) {
                    slotOk = false;
                }
                
                if (endTime != null && slot.getEnd().isAfter(endTime)) {
                    slotOk = false;
                }
                
                if (slotOk) {
                    timeMatch = true;
                    break;
                }
            }
            
            if (!timeMatch) {
                return false;
            }
        }
        
        return true;
    }
    
    
    public static class Builder {
        private String code;
        private String title;
        private Integer minCredits;
        private Integer maxCredits;
        private String instructorName;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private final SectionRepository sectionRepository;
        
        public Builder(SectionRepository sectionRepository) {
            this.sectionRepository = sectionRepository;
        }
        
        public Builder withCode(String code) {
            this.code = code;
            return this;
        }
        
        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder withMinCredits(Integer minCredits) {
            this.minCredits = minCredits;
            return this;
        }
        
        public Builder withMaxCredits(Integer maxCredits) {
            this.maxCredits = maxCredits;
            return this;
        }
        
        public Builder withInstructorName(String instructorName) {
            this.instructorName = instructorName;
            return this;
        }
        
        public Builder withDayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }
        
        public Builder withStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder withEndTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public CourseSpecification build() {
            return new CourseSpecification(this, sectionRepository);
        }
    }
}

