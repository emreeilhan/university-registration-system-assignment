package edu.uni.registration.validation;

import edu.uni.registration.model.Schedulable;
import edu.uni.registration.model.TimeSlot;

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


