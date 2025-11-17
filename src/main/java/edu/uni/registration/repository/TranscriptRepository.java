package edu.uni.registration.repository;

import edu.uni.registration.model.Transcript;
import java.util.*;

public class TranscriptRepository implements Repository<Transcript, String> {

    private final Map<String, Transcript> storage = new HashMap<>();

    @Override
    public Optional<Transcript> findById(String studentId) {
        if (studentId == null || studentId.isBlank()) return Optional.empty();
        return Optional.ofNullable(storage.get(studentId));
    }

    @Override
    public List<Transcript> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Transcript save(Transcript transcript) {
        if (transcript == null) throw new IllegalArgumentException("Transcript cannot be null");
        storage.put(transcript.getStudent().getId(), transcript);
        return transcript;
    }

    @Override
    public void deleteById(String studentId) {
        if (studentId != null) storage.remove(studentId);
    }
}
