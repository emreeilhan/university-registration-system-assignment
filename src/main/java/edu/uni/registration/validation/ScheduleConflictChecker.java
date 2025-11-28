package edu.uni.registration.validation;

import edu.uni.registration.model.Schedulable;
import edu.uni.registration.model.TimeSlot;

/**
 * Determines if two items with schedules (for example, course sections) overlap in their meeting times.
 * In other words, if any of the time slots for one overlaps with any time slot for the other,
 * there is a conflict.
 * 
 * This can be used to help students avoid double-booking classes or events.
 */
public class ScheduleConflictChecker {
    public boolean conflicts(Schedulable a, Schedulable b) {
        if (a == null || b == null) return false;
        for (TimeSlot ta : a.getMeetingTimes()) {
            for (TimeSlot tb : b.getMeetingTimes()) {
                if (ta.overlaps(tb)) {
                    return true;
                }
            }
        }
        return false;
    }
}


