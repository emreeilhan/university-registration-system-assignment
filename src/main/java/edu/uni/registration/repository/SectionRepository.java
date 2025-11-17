package edu.uni.registration.repository;
import edu.uni.registration.model.Section;
import java.util.*;

public class SectionRepository implements Repository<Section, String> {
    private final Map<String, Section> storage = new HashMap<>();

    @Override
    public Optional<Section> findById(String id) {
        if(id == null ||  id.isBlank()){
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Section> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Section save(Section section) {
        if(section == null){
            throw new IllegalArgumentException("Section cannot be null");
        }
        if(section.getId() == null || section.getId().isBlank()){
            throw new IllegalArgumentException("Section ID cannot be null");
        }
        storage.put(section.getId(), section);
        return section;
    }

    @Override
    public void deleteById(String id) {
        if(id == null ||  id.isBlank()){
            return;
        }
        storage.remove(id);
    }

    //Helper method(s)
    public boolean existsById(String id) {
        return id != null & storage.containsKey(id);
    }

    public List<Section> findByCourseCode(String courseCode){
        if(courseCode == null || courseCode.isBlank()){
            return List.of();
        }
        String code = courseCode.trim();
        List<Section> result = new ArrayList<>();
        for(Section section : storage.values()){
            if(section.getCourse() != null && code.equals(section.getCourse().getCode())){
                result.add(section);
            }
        }
        return result;
    }

    public List<Section> findByTerm(String term){
        if(term == null || term.isBlank()){
            return List.of();
        }
        String t = term.trim();
        List<Section> result = new ArrayList<>();
        for(Section section : storage.values()){
            if(t.equals(section.getTerm())){
                result.add(section);
            }
        }
        return result;
    }

    void clear(){
        storage.clear();
    }
}
