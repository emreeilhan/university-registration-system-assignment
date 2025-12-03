package edu.uni.registration;

import edu.uni.registration.model.TimeSlot;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void shouldDetectOverlap_whenSameDayAndOverlappingTimes() {
        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "101");
        TimeSlot slot2 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30), "102");
        assertTrue(slot1.overlaps(slot2));
        assertTrue(slot2.overlaps(slot1));
    }

    @Test
    void shouldNotOverlap_whenDifferentDays() {
        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "101");
        TimeSlot slot2 = new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "102");
        assertFalse(slot1.overlaps(slot2));
    }

    @Test
    void shouldNotOverlap_whenTimesAreAdjacent() {
        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "101");
        TimeSlot slot2 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), "102");
        assertFalse(slot1.overlaps(slot2));
    }

    @Test
    void shouldReturnFalse_whenComparingWithNull() {
        TimeSlot slot1 = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "101");
        assertFalse(slot1.overlaps(null));
    }

    @Test
    void shouldThrowException_whenInvalidConstructorArgs() {
        assertThrows(IllegalArgumentException.class, () -> 
            new TimeSlot(null, LocalTime.of(10, 0), LocalTime.of(11, 0), "101"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(10, 0), "101"));
    }
}


