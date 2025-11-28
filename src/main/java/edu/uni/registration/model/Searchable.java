package edu.uni.registration.model;

/**
 * Interface for entities that can be searched by keyword.
 */
public interface Searchable {
    boolean matchesKeyword(String keyword);
}


