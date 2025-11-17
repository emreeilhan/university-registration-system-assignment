package edu.uni.registration.repository;
import edu.uni.registration.model.Student;
import java.util.*;

public class StudentRepository implements Repository<Student,String> {
    private final Map<String,Student> storage = new HashMap<>();

    @Override
    public Optional<Student> findById(String id) {
        if(id == null || id.isBlank()){
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Student save(Student student) {
        if(student == null){
            throw new NullPointerException("Student cannot be null");
        }
        if(student.getId() == null || student.getId().isBlank()){
            throw new NullPointerException("Student ID cannot be null");
        }
        storage.put(student.getId(),student);
        return student;
    }

    @Override
    public void deleteById(String id) {
        if(id == null || id.isBlank()){
            return;
        }
        storage.remove(id);
    }

    //helper methods for testing
    public boolean existsById(String id) {
        return id != null && storage.containsKey(id);
    }

    public long count() {
        return storage.size();
    }

    void clear() {
        storage.clear();
        }


}
