package edu.uni.registration.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Section implements Schedulable {
    private final String id;
    private final Course course;
    private final String term;

    private Instructor instructor;
    private int capacity;

    private int waitlistCapacity = 10; // Default waitlist capacity

    private final List<TimeSlot> meetingTimes;
    private final List<Enrollment> roster;

    public Section(String id, Course course, String term, int capacity){
        this.id = id;
        this.course = course;
        this.term = term;
        this.capacity = capacity;
        this.meetingTimes = new ArrayList<>();
        this.roster = new ArrayList<>();
    }

    public void setWaitlistCapacity(int waitlistCapacity) {
        this.waitlistCapacity = waitlistCapacity;
    }

    public int getWaitlistCapacity() {
        return waitlistCapacity;
    }

    //Getter methods
    public String getId() {
        return id;
    }
    public Course getCourse() {
        return course;
    }

    public String getTerm() {
        return term;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<TimeSlot> getMeetingTimes() {
        return Collections.unmodifiableList(meetingTimes);
    }

    public List<Enrollment> getRoster() {
        return Collections.unmodifiableList(roster);
    }

   //Setter methods
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    //Helper methods
    public boolean isFull() {
        long enrolledCount = roster.stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                .count();
        return enrolledCount >= capacity;
    }

    public boolean isWaitlistFull() {
        long waitlistedCount = roster.stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.WAITLISTED)
                .count();
        return waitlistedCount >= waitlistCapacity;
    }

    public  void addMeetingTime(TimeSlot timeSlot) {
        if(timeSlot == null) {
            throw new IllegalArgumentException("TimeSlot cannot be null");
        }
        meetingTimes.add(timeSlot);
    }

    public void addEnrollment(Enrollment enrollment) {
        if(enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
        roster.add(enrollment);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id='" + id + '\'' +
                ", course=" + (course != null ? course.getCode() : "N/A") +
                ", term='" + term + '\'' +
                ", instructor=" + (instructor != null ? instructor.getFullName() : "TBA") +
                ", capacity=" + capacity +
                ", enrolled=" + roster.size() +
                '}';
        }
}

