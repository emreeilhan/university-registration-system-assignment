package edu.uni.registration.validation;

import edu.uni.registration.model.Section;

public class CapacityValidator {
    public boolean hasCapacity(Section section) {
        if (section == null) return false;
        return !section.isFull();
    }

    public boolean hasWaitlistCapacity(Section section) {
        if (section == null) return false;
        return section.isFull() && !section.isWaitlistFull();
    }
}


