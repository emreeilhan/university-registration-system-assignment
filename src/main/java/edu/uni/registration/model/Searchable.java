package edu.uni.registration.model;

/**
 * Interface for entities that can be searched by keyword.
 */
public interface Searchable {
    /**
     * Checks if entity matches keyword (case-insensitive).
     */
    boolean matchesKeyword(String keyword);
}


