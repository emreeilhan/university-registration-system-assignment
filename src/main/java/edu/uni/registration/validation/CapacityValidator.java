package edu.uni.registration.validation;

import edu.uni.registration.model.Section;

/**
 * Validates whether a section has available capacity for enrollment or waitlist.
 */
public class CapacityValidator {
    /**
     * Checks if a section has available spots for enrollment.
     *
     * @param section the section to check
     * @return true if there is capacity, false if full or section is null
     */
    public boolean hasCapacity(Section section) {
        if (section == null) return false;
        return !section.isFull();
    }

    /**
     * Checks if a section has available spots on the waitlist.
     * This only returns true if the section is full but the waitlist is not full.
     *
     * @param section the section to check
     * @return true if waitlist has capacity, false otherwise
     */
    public boolean hasWaitlistCapacity(Section section) {
        if (section == null) return false;
        return section.isFull() && !section.isWaitlistFull();
    }
}


