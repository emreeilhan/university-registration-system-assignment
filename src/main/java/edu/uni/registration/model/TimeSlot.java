package edu.uni.registration.model;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Represents a time slot for a class meeting.
 * Immutable value object that stores the day, start time, end time, and room.
 */
public class TimeSlot implements Comparable<TimeSlot> {
    private final DayOfWeek dayOfWeek;
    private final LocalTime start;
    private final LocalTime end;
    private final String room;

    /**
     * Creates a new time slot with the given information.
     *
     * @param dayOfWeek the day of the week (MONDAY, TUESDAY, etc.)
     * @param start the start time
     * @param end the end time
     * @param room the room number or location
     * @throws IllegalArgumentException if any parameter is null or start time is not before end time
     */
    public TimeSlot(DayOfWeek dayOfWeek, LocalTime start, LocalTime end, String room) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week cannot be null");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.dayOfWeek = dayOfWeek;
        this.start = start;
        this.end = end;
        this.room = room;
    }
    /**
     * Gets the day of the week for this time slot.
     *
     * @return the day of week
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Gets the start time of this time slot.
     *
     * @return the start time
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Gets the end time of this time slot.
     *
     * @return the end time
     */
    public LocalTime getEnd() {
        return end;
    }

    /**
     * Gets the room location for this time slot.
     *
     * @return the room number or location
     */
    public String getRoom() {
        return room;
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     * Two time slots overlap if they are on the same day and their time ranges intersect.
     *
     * @param other the other time slot to check against
     * @return true if they overlap, false otherwise
     */
    public boolean overlaps(TimeSlot other) {
        if(other == null) {
            return false;
        }
        if(!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    @Override
    public int compareTo(TimeSlot other) {
        int dayCompare = this.dayOfWeek.compareTo(other.dayOfWeek);
        if (dayCompare != 0) {
            return dayCompare;
        }
        return this.start.compareTo(other.start);
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "dayOfWeek=" + dayOfWeek +
                ", start=" + start +
                ", end=" + end +
                ", room='" + room + '\'' +
                '}';
    }
}




