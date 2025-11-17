package edu.uni.registration.repository;

import edu.uni.registration.model.Person;

import java.util.*;

public class PersonRepository implements Repository<Person, String> {

    private final Map<String, Person> storage = new HashMap<>();

    @Override
    public Optional<Person> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Person save(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        if (person.getId() == null || person.getId().isBlank()) {
            throw new IllegalArgumentException("Person id cannot be null or blank");
        }
        storage.put(person.getId(), person);
        return person;
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        storage.remove(id);
    }

    
}


