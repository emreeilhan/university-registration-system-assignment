package edu.uni.registration.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a section (class offering) of a course for a specific term.
 * A section has an instructor, capacity limit, meeting times, and a roster of enrolled students.
 */
public class Section implements Schedulable {
    private final String id;
    private final Course course;
    private final String term;

    private Instructor instructor;
    private int capacity;

    private int waitlistCapacity = 10; // Default waitlist capacity

    private final List<TimeSlot> meetingTimes;
    private final List<Enrollment> roster;

    /**
     * Creates a new section with the given information.
     *
     * @param id the unique section ID (e.g., "CS101-01")
     * @param course the course this section belongs to
     * @param term the term this section is offered in (e.g., "Fall 2023")
     * @param capacity the maximum number of students that can enroll
     */
    public Section(String id, Course course, String term, int capacity){
        this.id = id;
        this.course = course;
        this.term = term;
        this.capacity = capacity;
        this.meetingTimes = new ArrayList<>();
        this.roster = new ArrayList<>();
    }

    /**
     * Sets the maximum number of students that can be on the waitlist.
     *
     * @param waitlistCapacity the new waitlist capacity
     */
    public void setWaitlistCapacity(int waitlistCapacity) {
        this.waitlistCapacity = waitlistCapacity;
    }

    /**
     * Gets the maximum number of students that can be on the waitlist.
     *
     * @return the waitlist capacity
     */
    public int getWaitlistCapacity() {
        return waitlistCapacity;
    }

    /**
     * Gets the unique section ID.
     *
     * @return the section ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the course this section belongs to.
     *
     * @return the course object
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Gets the term this section is offered in.
     *
     * @return the term name
     */
    public String getTerm() {
        return term;
    }

    /**
     * Gets the instructor assigned to this section.
     *
     * @return the instructor, or null if not assigned yet
     */
    public Instructor getInstructor() {
        return instructor;
    }

    /**
     * Gets the maximum number of students that can enroll in this section.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets a read-only list of meeting times for this section.
     *
     * @return an unmodifiable list of time slots
     */
    public List<TimeSlot> getMeetingTimes() {
        return Collections.unmodifiableList(meetingTimes);
    }

    /**
     * Gets a read-only list of all enrollments (roster) for this section.
     *
     * @return an unmodifiable list of enrollments
     */
    public List<Enrollment> getRoster() {
        return Collections.unmodifiableList(roster);
    }

    /**
     * Sets the instructor assigned to this section.
     *
     * @param instructor the instructor to assign
     */
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    /**
     * Sets the maximum capacity for this section.
     *
     * @param capacity the new capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Checks if this section is full (all enrolled spots are taken).
     *
     * @return true if the section is full, false otherwise
     */
    public boolean isFull() {
        int enrolledCount = 0;
        for (Enrollment e : roster) {
            if (e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED) {
                enrolledCount++;
            }
        }
        return enrolledCount >= capacity;
    }

    /**
     * Checks if the waitlist for this section is full.
     *
     * @return true if the waitlist is full, false otherwise
     */
    public boolean isWaitlistFull() {
        int waitlistedCount = 0;
        for (Enrollment e : roster) {
            if (e.getStatus() == Enrollment.EnrollmentStatus.WAITLISTED) {
                waitlistedCount++;
            }
        }
        return waitlistedCount >= waitlistCapacity;
    }

    /**
     * Adds a meeting time to this section's schedule.
     *
     * @param timeSlot the time slot to add
     * @throws IllegalArgumentException if timeSlot is null
     */
    public void addMeetingTime(TimeSlot timeSlot) {
        if(timeSlot == null) {
            throw new IllegalArgumentException("TimeSlot cannot be null");
        }
        meetingTimes.add(timeSlot);
    }

    /**
     * Adds an enrollment to this section's roster.
     *
     * @param enrollment the enrollment to add
     * @throws IllegalArgumentException if enrollment is null
     */
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

