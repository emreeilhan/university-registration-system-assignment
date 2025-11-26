package edu.uni.registration.model;

/**
 * Interface for entities that can be searched by keyword.
 * Provides a common contract for searchable entities in the system.
 * 
 * This interface enables polymorphic search behavior across different entity types.
 */
public interface Searchable {
    /**
     * Checks if this entity matches the given search keyword.
     * The implementation should perform case-insensitive matching against
     * relevant fields of the entity (e.g., code, title, name).
     * 
     * @param keyword the search term to match against
     * @return true if the entity matches the keyword, false otherwise
     */
    boolean matchesKeyword(String keyword);
}


