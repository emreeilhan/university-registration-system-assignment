package edu.uni.registration;

import edu.uni.registration.model.Schedulable;
import edu.uni.registration.model.TimeSlot;
import edu.uni.registration.validation.ScheduleConflictChecker;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleConflictCheckerTest {

    private final ScheduleConflictChecker checker = new ScheduleConflictChecker();

    private static class TestSchedulable implements Schedulable {
        private final List<TimeSlot> slots;

        TestSchedulable(List<TimeSlot> slots) {
            this.slots = slots;
        }

        @Override
        public List<TimeSlot> getMeetingTimes() {
            return slots;
        }
    }

    @Test
    void shouldDetectConflict_whenTimeSlotsOverlap() {
        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "A");
        TimeSlot slot2 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30), "B");

        Schedulable s1 = new TestSchedulable(List.of(slot1));
        Schedulable s2 = new TestSchedulable(List.of(slot2));

        assertTrue(checker.conflicts(s1, s2));
    }

    @Test
    void shouldNotDetectConflict_whenTimeSlotsAreAdjacent() {
        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "A");
        TimeSlot slot2 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "B");

        Schedulable s1 = new TestSchedulable(List.of(slot1));
        Schedulable s2 = new TestSchedulable(List.of(slot2));

        assertFalse(checker.conflicts(s1, s2));
    }

    @Test
    void shouldReturnFalse_whenInputIsNull() {
        Schedulable s1 = new TestSchedulable(List.of());
        assertFalse(checker.conflicts(s1, null));
        assertFalse(checker.conflicts(null, s1));
    }
}


