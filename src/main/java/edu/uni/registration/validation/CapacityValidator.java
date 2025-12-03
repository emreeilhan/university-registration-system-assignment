package edu.uni.registration.validation;

import edu.uni.registration.model.Section;

/**
 * Checks section capacity and waitlist availability.
 */
public class CapacityValidator {

    public boolean hasCapacity(Section section) {
        if (section == null) return false;
        return !section.isFull();
    }

    /** Returns true only if section is full but waitlist has room. */
    public boolean hasWaitlistCapacity(Section section) {
        if (section == null) return false;
        return section.isFull() && !section.isWaitlistFull();
    }
}


