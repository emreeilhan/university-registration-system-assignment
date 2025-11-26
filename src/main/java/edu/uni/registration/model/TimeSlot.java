package edu.uni.registration.model;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Time slot for a class meeting. Immutable - stores day, start/end time, and room.
 */
public class TimeSlot implements Comparable<TimeSlot> {
    private final DayOfWeek dayOfWeek;
    private final LocalTime start;
    private final LocalTime end;
    private final String room;

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
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getRoom() {
        return room;
    }

    /**
     * Checks if this time slot overlaps with another (same day, intersecting times).
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




