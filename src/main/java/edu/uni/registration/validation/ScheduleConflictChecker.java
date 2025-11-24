package edu.uni.registration.validation;

import edu.uni.registration.model.Schedulable;
import edu.uni.registration.model.TimeSlot;

/**
 * Checks if two schedulable items (like sections) have overlapping meeting times.
 * A conflict occurs if any time slot from one overlaps with any time slot from the other.
 */
public class ScheduleConflictChecker {
    /**
     * Checks if two schedulable items have conflicting meeting times.
     *
     * @param a the first schedulable item (e.g., a section)
     * @param b the second schedulable item (e.g., another section)
     * @return true if there is a time conflict, false otherwise
     */
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


